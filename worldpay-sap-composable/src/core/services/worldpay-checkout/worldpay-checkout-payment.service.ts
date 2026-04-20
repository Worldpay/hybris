import { APP_BASE_HREF, PlatformLocation } from '@angular/common';
import { inject, Inject, Injectable, OnDestroy } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { ActiveCartFacade, PaymentType } from '@spartacus/cart/base/root';
import { CheckoutPaymentService } from '@spartacus/checkout/base/core';
import { CheckoutPaymentDetailsCreatedEvent, CheckoutPaymentDetailsSetEvent, CheckoutQueryFacade, CheckoutState } from '@spartacus/checkout/base/root';
import {
  Address,
  Command,
  CommandService,
  CommandStrategy,
  EventService,
  GlobalMessageService,
  GlobalMessageType,
  LoadUserPaymentMethodsEvent,
  LoggerService,
  OCC_USER_ID_ANONYMOUS,
  PaymentDetails,
  Query,
  QueryService,
  QueryState,
  UserIdService
} from '@spartacus/core';
import { BehaviorSubject, combineLatest, defer, Observable, of, Subscription, throwError } from 'rxjs';
import { catchError, distinctUntilChanged, filter, map, switchMap, take, tap } from 'rxjs/operators';
import { WorldpayCheckoutPaymentConnector, WorldpayConnector } from '../../connectors';
import {
  ClearGooglepayEvent,
  ClearInitialPaymentRequestEvent,
  CreateWorldpayPaymentDetailsEvent,
  DDC3dsJwtSetEvent,
  GetDDC3dsJwtEvent,
  InitialPaymentRequestSetEvent,
  SetPaymentAddressEvent,
  SetWorldpayPublicKeyEvent,
  ThreeDsChallengeIframeUrlSetEvent,
  ThreeDsDDCIframeUrlSetEvent,
  ThreeDsSetEvent
} from '../../events';
import { WorldpayACHFacade, WorldpayCheckoutPaymentFacade } from '../../facade';
import { ACHPaymentForm, PaymentMethod, ThreeDsDDCInfo, ThreeDsInfo, WorldpayApmPaymentInfo } from '../../interfaces';
import { getBaseHref, trimLastSlashFromUrl } from '../../utils';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
declare let Worldpay: any;

@Injectable()
export class WorldpayCheckoutPaymentService extends CheckoutPaymentService implements WorldpayCheckoutPaymentFacade, OnDestroy {
  baseHref: string;
  protected cseToken$: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);
  protected threeDsChallengeIframeUrl$: BehaviorSubject<SafeResourceUrl> = new BehaviorSubject<SafeResourceUrl>(null);
  protected threeDsChallengeInfo$: BehaviorSubject<ThreeDsInfo> = new BehaviorSubject(null);
  protected threeDsDDCIframeUrl$: BehaviorSubject<SafeResourceUrl> = new BehaviorSubject<SafeResourceUrl>(null);
  protected threeDsDDCInfo$: BehaviorSubject<ThreeDsDDCInfo> = new BehaviorSubject<ThreeDsDDCInfo>(null);
  protected publicKey$: BehaviorSubject<string> = new BehaviorSubject<string>(null);
  protected subscriptions: Subscription = new Subscription();
  protected saveCreditCard$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  protected saveAsDefaultCreditCard$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  protected logger: LoggerService = inject(LoggerService);
  /**
   * Get Public Key Query
   * @since 6.4.0
   */
  protected getPublicKeyQuery$: Query<string> =
    this.queryService.create((): Observable<string> =>
      this.worldpayConnector.getPublicKey().pipe(
        tap({
          next: (response: string): void => {
            this.eventService.dispatch({
              publicKey: response
            }, SetWorldpayPublicKeyEvent);
          },
          error: (): void => {
            this.globalMessageService.add({ key: 'paymentForm.publicKey.requestFailed' },
              GlobalMessageType.MSG_TYPE_ERROR
            );
          }
        }),
      ),
    );

  protected getThreeDsDDCJwtQuery$: Query<ThreeDsDDCInfo> =
    this.queryService.create((): Observable<ThreeDsDDCInfo> =>
      this.worldpayConnector.getDDC3dsJwt().pipe(
        tap((response: ThreeDsDDCInfo): void => {
          this.eventService.dispatch({ dDC3dsJwt: response }, GetDDC3dsJwtEvent);
          this.eventService.dispatch({ threeDsDDCInfo: response }, ThreeDsSetEvent);
        })
      )
    );

  /**
   * Create Worldpay Payment Method Command
   * @since 6.4.0
   * @param paymentDetails PaymentDetails
   * @param cseToken string
   */
  protected createWorldpayPaymentMethodCommand: Command<{ paymentDetails: PaymentDetails; cseToken: string }, PaymentDetails> =
    this.commandService.create<{ paymentDetails: PaymentDetails; cseToken: string }, PaymentDetails>(
      ({
        paymentDetails,
        cseToken
      }: {
        paymentDetails: PaymentDetails;
        cseToken: string;
      }): Observable<PaymentDetails> =>
        this.checkoutPreconditions().pipe(
          switchMap(([userId, cartId]: [string, string]): Observable<PaymentDetails> =>
            this.checkoutPaymentConnector.createWorldpayPaymentDetails(userId, cartId, paymentDetails, cseToken).pipe(
              tap((response: PaymentDetails): void => {
                this.eventService.dispatch({
                  paymentDetails: response,
                  cseToken
                }, CreateWorldpayPaymentDetailsEvent);

                this.eventService.dispatch(
                  {
                    userId,
                    cartId,
                    paymentDetails: response
                  },
                  CheckoutPaymentDetailsCreatedEvent
                );
              })
            )
          )
        ),
      { strategy: CommandStrategy.CancelPrevious }
    );

  /**
   * Set Payment Method Command
   * @since 6.4.0
   */
  protected override setPaymentMethodCommand: Command<PaymentDetails, PaymentDetails> =
    this.commandService.create<PaymentDetails>(
      (paymentDetails: PaymentDetails): Observable<PaymentDetails> =>
        this.checkoutPreconditions().pipe(
          switchMap(([userId, cartId]: [string, string]): Observable<PaymentDetails> =>
            this.checkoutPaymentConnector.useExistingPaymentDetails(userId, cartId, paymentDetails)
              .pipe(
                tap((response: PaymentDetails): void => {
                  if (userId === OCC_USER_ID_ANONYMOUS) {
                    this.eventService.dispatch({
                      userId,
                      cartId,
                      paymentDetails: response
                    }, CheckoutPaymentDetailsCreatedEvent);
                  } else {
                    this.eventService.dispatch({ userId }, LoadUserPaymentMethodsEvent);
                    this.eventService.dispatch(
                      {
                        userId,
                        cartId,
                        paymentDetailsId: paymentDetails.id
                      },
                      CheckoutPaymentDetailsSetEvent
                    );
                  }
                })
              )
          )
        ),
      { strategy: CommandStrategy.CancelPrevious }
    );

  /**
   * Set Payment Address Command
   * @since 6.4.0
   */
  protected setPaymentAddressCommand: Command<Address, Address> =
    this.commandService.create<Address>(
      (address: Address): Observable<Address> =>
        this.checkoutPreconditions().pipe(
          switchMap(([userId, cartId]: [string, string]): Observable<Address> => this.worldpayConnector.setPaymentAddress(userId, cartId, address).pipe(
            tap((response: Address): void => {
              this.eventService.dispatch({ address: response }, SetPaymentAddressEvent);
            }))
          )
        ),
      { strategy: CommandStrategy.CancelPrevious }
    );

  constructor(
    protected override activeCartFacade: ActiveCartFacade,
    protected override userIdService: UserIdService,
    protected override queryService: QueryService,
    protected override commandService: CommandService,
    protected override eventService: EventService,
    protected override checkoutPaymentConnector: WorldpayCheckoutPaymentConnector,
    protected override checkoutQueryFacade: CheckoutQueryFacade,
    protected sanitizer: DomSanitizer,
    protected router: Router,
    protected platformLocation: PlatformLocation,
    protected globalMessageService: GlobalMessageService,
    protected worldpayConnector: WorldpayConnector,
    protected worldpayACHFacade: WorldpayACHFacade,
    @Inject(APP_BASE_HREF) protected appBaseHref: string
  ) {
    super(
      activeCartFacade,
      userIdService,
      queryService,
      commandService,
      eventService,
      checkoutPaymentConnector,
      checkoutQueryFacade,
    );
    this.baseHref = getBaseHref(this.platformLocation);

    if (!this.baseHref && this.appBaseHref?.length) {
      this.baseHref = trimLastSlashFromUrl(this.appBaseHref);
    }

    this.listenThreeDsSetEvent();
    this.listenCreateWorldpayPaymentDetailsEvent();
    this.listenClearPaymentRequestEvent();
    this.listenInitialPaymentRequestEvent();
    this.listenThreeDsChallengeIframeUrlSetEvent();
    this.listenThreeDsDDCIframeUrlSetEvent();
  }

  /**
   * Create Worldpay Payment Details Event
   * @since 6.4.0
   */
  listenCreateWorldpayPaymentDetailsEvent(): void {
    this.subscriptions.add(
      this.eventService.get(CreateWorldpayPaymentDetailsEvent).subscribe({
        next: (event: CreateWorldpayPaymentDetailsEvent): void => this.cseToken$.next(event.cseToken)
      })
    );
  }

  /**
   * Initial Payment Request Event
   * @since 6.4.0
   */
  listenInitialPaymentRequestEvent(): void {
    this.subscriptions.add(
      this.eventService.get(InitialPaymentRequestSetEvent).subscribe({
        next: (event: InitialPaymentRequestSetEvent): void => {
          this.setThreeDsChallengeInfo(event.threeDSecureInfo);
        }
      })
    );
  }

  /**
   * Initial Payment Request Event
   * @since 6.4.0
   */
  listenClearPaymentRequestEvent(): void {
    this.subscriptions.add(
      this.eventService.get(ClearInitialPaymentRequestEvent).subscribe({
        next: (): void => {
          this.threeDsChallengeIframeUrl$.next(null);
          this.threeDsChallengeInfo$.next(null);
          this.threeDsDDCIframeUrl$.next(null);
          this.threeDsDDCInfo$.next(null);
          this.eventService.dispatch({}, ClearGooglepayEvent);
        }
      })
    );
  }

  listenThreeDsSetEvent(): void {
    this.subscriptions.add(
      this.eventService.get(ThreeDsSetEvent).subscribe({
        next: (event: ThreeDsSetEvent): void => {
          if (event.threeDsDDCIframeUrl) {
            this.threeDsDDCIframeUrl$.next(event.threeDsDDCIframeUrl);
          }

          if (event.threeDsChallengeIframeUrl) {
            this.threeDsChallengeIframeUrl$.next(event.threeDsChallengeIframeUrl);
          }

          if (event.threeDsDDCInfo) {
            this.threeDsDDCInfo$.next(event.threeDsDDCInfo);
          }

          if (event.threeDsChallengeInfo) {
            this.threeDsChallengeInfo$.next(event.threeDsChallengeInfo);
          }
        }
      })
    );
  }

  /**
   * Get Public Key
   * @since 6.4.0
   */
  getPublicKey(): Observable<QueryState<string>> {
    return this.getPublicKeyQuery$.getState();
  }

  /**
   * Get Public Key From State
   * @since 6.4.0
   */
  generatePublicKey(): Observable<string> {
    return this.getPublicKey().pipe(
      filter((state: QueryState<string>): boolean => state?.loading === false),
      map((state: QueryState<string>): string => state?.data),
      tap((key: string): void => {
        this.eventService.dispatch({ publicKey: key }, SetWorldpayPublicKeyEvent);
      })
    );
  }

  /**
   * Get Three Ds DDC Jwt
   * @since 4.3.6
   */
  getThreeDsDDCJwt(): Observable<QueryState<ThreeDsDDCInfo>> {
    return this.getThreeDsDDCJwtQuery$.getState();
  }

  /**
   * Get Cse Token From State
   * @since 6.4.0
   */
  getCseTokenFromState(): Observable<string> {
    return this.cseToken$.asObservable();
  }

  /**
   * Get Three Ds Challenge Info From State
   * @since 6.4.0
   */
  getThreeDsChallengeInfoFromState(): Observable<ThreeDsInfo> {
    return this.threeDsChallengeInfo$.asObservable();
  }

  /**
   * Get Three Ds DDC Iframe Url From State
   * @since 6.4.0
   */
  getThreeDsDDCIframeUrlFromState(): Observable<SafeResourceUrl> {
    return this.threeDsDDCIframeUrl$.asObservable();
  }

  /**
   * Get Three Ds Challenge Iframe Url From State
   * @since 6.4.0
   */
  getThreeDsChallengeIframeUrlFromState(): Observable<SafeResourceUrl> {
    return this.threeDsChallengeIframeUrl$.asObservable();
  }

  /**
   * Get Public Key From State
   * @since 6.4.0
   */
  getPublicKeyFromState(): Observable<string> {
    return this.publicKey$.asObservable();
  }

  /**
   * Set Public Key
   * @since 6.4.0
   * @param publicKey string
   */
  setPublicKey(publicKey: string): void {
    Worldpay.setPublicKey(publicKey);
    this.publicKey$.next(publicKey);
  }

  /**
   * Create Payment Details
   * @since 6.4.0
   * @param paymentDetails PaymentDetails
   */
  override createPaymentDetails(paymentDetails: PaymentDetails): Observable<PaymentDetails> {
    return this.generatePublicKey().pipe(
      filter((key: string | null | undefined): key is string => !!key),
      take(1),
      tap((key: string): void => this.setPublicKey(key)),
      catchError((error: unknown): Observable<never> => {
        this.logger.error('Failed obtaining public key', { error });
        return throwError((): unknown => error);
      }),
      switchMap((): Observable<string> => this.generateCseToken(paymentDetails)),
      switchMap((cseToken: string): Observable<PaymentDetails> => {
        if (!cseToken) {
          return throwError((): Error => new Error('CSE token generation failed'));
        }

        this.setCseToken(cseToken);

        return this.createWorldpayPaymentMethodCommand.execute({
          paymentDetails,
          cseToken,
        });
      })
    );
  }

  /**
   * Generate Cse Token
   * @since 6.4.0
   * @param paymentDetails PaymentDetails
   */
  generateCseToken(paymentDetails: PaymentDetails): Observable<string> {
    return defer((): Observable<string> => {
      try {
        return of(Worldpay.encrypt({
          cvc: Number(paymentDetails.cvn),
          cardHolderName: paymentDetails.accountHolderName,
          cardNumber: paymentDetails.cardNumber,
          expiryMonth: paymentDetails.expiryMonth,
          expiryYear: paymentDetails.expiryYear
        }, (error: string[]): void => {
          if (error?.length) {
            error.map((value: string): void => {
              this.globalMessageService.add({ key: `checkoutReview.encrypt.${value}` }, GlobalMessageType.MSG_TYPE_ERROR);
            });
          }
        }));
      } catch (error) {
        return throwError((): unknown => error);
      }
    });
  }

  /**
   * Update Cse Token
   * @since 6.4.0
   * @param cseToken string
   */
  setCseToken(cseToken: string): void {
    this.cseToken$.next(cseToken);
  }

  /**
   * Use Existing Payment Details
   * @since 6.4.0
   * @param paymentDetails PaymentDetails
   */
  useExistingPaymentDetails(paymentDetails: PaymentDetails): Observable<PaymentDetails> {
    return this.setPaymentMethodCommand.execute(paymentDetails);
  }

  /**
   * Set Payment Address
   * @since 6.4.0
   * @param address Address
   */
  setPaymentAddress(address: Address): Observable<Address> {
    return this.setPaymentAddressCommand.execute(address);
  }

  /**
   * Get Serialized Url
   * @since 4.3.6
   * @param url string
   * @param params { [key: string]: string }
   */
  getSerializedUrl(url: string, params: { [key: string]: string }): Observable<string> {
    const paramURL: string = this.router.serializeUrl(this.router.createUrlTree([url], { queryParams: params }));
    return of(this.baseHref ? (this.baseHref + paramURL).replace('//', '/') : paramURL);
  }

  /**
   * Set Three Ds DDC Iframe Url
   * @since 4.3.6
   * @param ddcUrl string
   * @param cardNumber string
   * @param jwt string
   */
  setThreeDsDDCIframeUrl(
    ddcUrl: string,
    cardNumber: string,
    jwt: string
  ): void {
    this.getSerializedUrl('worldpay-3ds-device-detection', {
      action: ddcUrl,
      bin: cardNumber,
      jwt
    }).pipe(
      distinctUntilChanged(),
      take(1),
      tap((url: string): void => {
        const safeUrl: SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
        this.eventService.dispatch({ threeDsDDCIframeUrl: safeUrl }, ThreeDsDDCIframeUrlSetEvent);
      })
    ).subscribe();
  }

  /**
   * Set Three Ds Challenge Iframe Url
   * @since 4.3.6
   * @param challengeUrl
   * @param jwt
   * @param merchantData
   */
  setThreeDsChallengeIframeUrl(
    challengeUrl: string,
    jwt: string,
    merchantData: string
  ): void {
    this.getSerializedUrl('worldpay-3ds-challenge', {
      action: challengeUrl,
      md: merchantData,
      jwt
    }).pipe(
      distinctUntilChanged(),
      take(1),
      tap((url: string): void => {
        const safeUrl: SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
        this.eventService.dispatch({ worldpayChallengeIframeUrl: safeUrl }, ThreeDsChallengeIframeUrlSetEvent);
      })
    ).subscribe();
  }

  /**
   * Set Three Ds Challenge Info
   * @since 6.4.0
   * @param value ThreeDsInfo
   */
  setThreeDsChallengeInfo(value: ThreeDsInfo): void {
    this.threeDsChallengeInfo$.next(value);
  }

  /**
   * Listen Set Three DsDDC Info Event
   * @since 6.4.0
   */
  listenSetThreeDsDDCInfoEvent(): void {
    this.subscriptions.add(
      this.eventService.get(DDC3dsJwtSetEvent).subscribe({
        next: (ddcInfo: DDC3dsJwtSetEvent): void => {
          this.setThreeDsDDCInfo(ddcInfo.ddcInfo);
        }
      })
    );
  }

  /**
   * Set Three DsDDCInfo Value
   * @param value ThreeDsDDCInfo
   */
  setThreeDsDDCInfo(value: ThreeDsDDCInfo): void {
    this.threeDsDDCInfo$.next(value);
  }

  /**
   * Get Three DsDDCInfo From State
   * @since 4.3.6
   */
  getDDCInfoFromState(): Observable<ThreeDsDDCInfo> {
    return this.threeDsDDCInfo$.asObservable();
  }

  /**
   * Listen Three Ds Challenge Iframe Url Set Event
   * @since 6.4.0
   */
  listenThreeDsChallengeIframeUrlSetEvent(): void {
    this.subscriptions.add(
      this.eventService.get(ThreeDsChallengeIframeUrlSetEvent).subscribe({
        next: (event: ThreeDsChallengeIframeUrlSetEvent): void => {
          this.eventService.dispatch({
            threeDsDDCIframeUrl: null,
            threeDsChallengeIframeUrl: event.worldpayChallengeIframeUrl
          }, ThreeDsSetEvent);
        }
      })
    );
  }

  /**
   * Listen Three Ds DDC Iframe Url Set Event
   * @since 6.4.0
   */
  listenThreeDsDDCIframeUrlSetEvent(): void {
    this.subscriptions.add(
      this.eventService.get(ThreeDsDDCIframeUrlSetEvent).subscribe({
        next: (event: ThreeDsDDCIframeUrlSetEvent): void => {
          this.eventService.dispatch({
            threeDsDDCIframeUrl: event.threeDsDDCIframeUrl,
          }, ThreeDsSetEvent);
        }
      })
    );
  }

  /**
   * Set Save Credit Card Value
   * @param saveCreditCard
   */
  setSaveCreditCardValue(saveCreditCard: boolean): void {
    this.saveCreditCard$.next(saveCreditCard);
  }

  /**
   * Get Save Credit Card Value From State
   * @since 6.4.0
   */
  getSaveCreditCardValueFromState(): Observable<boolean> {
    return this.saveCreditCard$.asObservable();
  }

  /**
   * Set Save As Default Credit Card Value
   * @param saveAsDefaultCreditCard
   */
  setSaveAsDefaultCardValue(saveAsDefaultCreditCard: boolean): void {
    this.saveAsDefaultCreditCard$.next(saveAsDefaultCreditCard);
  }

  /**
   * Get Save As Default Credit Card Value From State
   * @since 6.4.0
   */
  getSaveAsDefaultCardValueFromState(): Observable<boolean> {
    return this.saveAsDefaultCreditCard$.asObservable();
  }

  /**
   * Get Payment Details State
   * @since 6.4.0
   */
  override getPaymentDetailsState(): Observable<QueryState<WorldpayApmPaymentInfo | undefined>> {
    return combineLatest([
      this.getSaveCreditCardValueFromState(),
      this.getSaveAsDefaultCardValueFromState(),
      this.worldpayACHFacade.getACHPaymentFormValue(),
      this.checkoutQueryFacade.getCheckoutDetailsState(),
    ]).pipe(
      // eslint-disable-next-line max-len
      map(([saveCreditCard, setAsDefaultPayment, achPaymentFormValue, checkoutState]: [boolean, boolean, ACHPaymentForm, QueryState<CheckoutState>]): QueryState<WorldpayApmPaymentInfo> => {

        if (!checkoutState.data) {
          return {
            ...checkoutState,
            data: undefined
          };
        }

        const paymentType: PaymentType = checkoutState.data?.paymentType;
        const worldpayAPMPaymentInfo: WorldpayApmPaymentInfo = checkoutState.data?.worldpayAPMPaymentInfo;
        const paymentDetails: PaymentDetails = checkoutState.data?.paymentInfo;

        let updatedPaymentType: PaymentType = paymentType || {};

        if (worldpayAPMPaymentInfo && paymentType) {
          worldpayAPMPaymentInfo.isAPM = true;
          updatedPaymentType = {
            ...paymentType,
            code: worldpayAPMPaymentInfo?.apmCode || paymentType?.code,
            displayName: worldpayAPMPaymentInfo?.name,
          };
        }
        const paymentInfo: WorldpayApmPaymentInfo = {
          ...paymentDetails,
          ...updatedPaymentType,
          ...worldpayAPMPaymentInfo,
        };

        if (paymentInfo.id) {
          paymentInfo.defaultPayment = setAsDefaultPayment || paymentDetails?.defaultPayment;
          paymentInfo.saved = saveCreditCard || checkoutState.data?.paymentInfo?.saved;
        }

        if (paymentInfo.apmCode === PaymentMethod.ACH) {
          paymentInfo.achPaymentForm = achPaymentFormValue;
        }

        return {
          ...checkoutState,
          data: paymentInfo
        };
      }),
    );
  }

  getSelectedPaymentTypeState(): Observable<QueryState<PaymentType | undefined>> {
    return this.checkoutQueryFacade.getCheckoutDetailsState()
      .pipe(map((state: QueryState<CheckoutState | undefined>): QueryState<PaymentType> => ({
        ...state,
        data: state.data?.paymentType
      })));
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
