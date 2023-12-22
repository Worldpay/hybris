import { Inject, Injectable } from '@angular/core';
import {
  CmsService,
  Command,
  CommandService,
  CommandStrategy,
  ConverterService,
  CurrencySetEvent,
  EventService,
  GlobalMessageService,
  GlobalMessageType,
  HttpErrorModel,
  OCC_USER_ID_ANONYMOUS,
  Query,
  QueryNotifier,
  QueryService,
  QueryState,
  UserIdService,
  WindowRef
} from '@spartacus/core';
import { map, switchMap, take, tap } from 'rxjs/operators';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { COMPONENT_APM_NORMALIZER } from '../../occ/converters';
import { ApmData, ApmPaymentDetails, APMRedirectResponse, OccCmsComponentWithMedia, PaymentMethod } from '../../interfaces';
import { ActiveCartFacade, Cart } from '@spartacus/cart/base/root';
import { WorldpayApmFacade } from '../../facade/worldpay-apm-facade';
import { CheckoutDeliveryAddressCreatedEvent, CheckoutDeliveryAddressSetEvent, CheckoutPaymentDetailsSetEvent } from '@spartacus/checkout/base/root';
import { WorldpayApmConnector } from '../../connectors/worldpay-apm/worldpay-apm.connector';
import {
  ClearWorldpayPaymentDetailsEvent,
  SelectWorldpayAPMEvent,
  SetWorldpayAPMRedirectResponseEvent,
  SetWorldpaySaveAsDefaultCreditCardEvent,
  SetWorldpaySavedCreditCardEvent
} from '../../events/worldpay.events';
import { WorldpayOrderService } from '../worldpay-order/worldpay-order.service';
import { APP_BASE_HREF, PlatformLocation } from '@angular/common';
import { getBaseHref, trimLastSlashFromUrl } from '../../utils';
import { WorldpayCheckoutPaymentService } from '../worldpay-checkout/worldpay-checkout-payment.service';

@Injectable({
  providedIn: 'root'
})
export class WorldpayApmService implements WorldpayApmFacade {
  baseHref: string;
  protected selectedApm$: BehaviorSubject<ApmPaymentDetails> = new BehaviorSubject<ApmPaymentDetails>(null);
  protected apmRedirectUrl$: BehaviorSubject<APMRedirectResponse> = new BehaviorSubject<APMRedirectResponse>(null);

  /**
   * Reload events for selected APM
   * @since 6.4.0
   * @returns QueryNotifier[] - Array of QueryNotifier
   */
  protected reloadSelectedApmEvents(): QueryNotifier[] {
    return [
      CurrencySetEvent,
      CheckoutDeliveryAddressSetEvent,
      CheckoutDeliveryAddressCreatedEvent
    ];
  }

  /**
   * Command used to get available APMs
   * @since 6.4.0
   * @returns Query<ApmData[]> - Query with ApmData[]
   */
  protected getAvailableApmQuery$: Query<ApmData[]> =
    this.queryService.create<ApmData[]>(
      () => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]) =>
          this.worldpayApmConnector.getAvailableApms(userId, cartId)
        )
      ),
      {
        reloadOn: this.reloadSelectedApmEvents()
      }
    );

  /**
   * Command used to set APM payment details
   * @since 6.4.0
   * @returns Command<ApmPaymentDetails, Cart> - Command with ApmPaymentDetails and Cart
   */
  protected setApmPaymentDetailsCommand$: Command<ApmPaymentDetails, Cart> =
    this.commandService.create<ApmPaymentDetails, Cart>(
      (apmPaymentDetails: ApmPaymentDetails) => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]) =>
          this.worldpayApmConnector.setAPMPaymentInfo(
            userId,
            cartId,
            apmPaymentDetails
          ).pipe(
            tap((response: Cart): void => {
              this.selectAPM(apmPaymentDetails);
              this.eventService.dispatch(
                {
                  userId,
                  cartId,
                  paymentDetailsId: response.apmCode,
                  cartCode: cartId
                },
                CheckoutPaymentDetailsSetEvent
              );
            })
          )
        )
      )
    );

  /**
   * Command used to get APM redirect url
   * @since 6.4.0
   * @returns Command<{ apm: ApmPaymentDetails; save: boolean }, APMRedirectResponse> - Command with ApmPaymentDetails and APMRedirectResponse
   */
  protected getWorldpayAPMRedirectUrlCommand$: Command<{ apm: ApmPaymentDetails; save: boolean }, APMRedirectResponse> =
    this.commandService.create<{ apm: ApmPaymentDetails; save: boolean }, APMRedirectResponse>(
      ({
        apm,
        save
      }) => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]) =>
          this.worldpayApmConnector.authoriseApmRedirect(userId, cartId, apm, save).pipe(
            tap(
              (apmRedirect: APMRedirectResponse): void => {
                this.setWorldpayAPMRedirectUrl(apmRedirect);
                this.eventService.dispatch(
                  {
                    userId,
                    cartId,
                    apmRedirect
                  },
                  ClearWorldpayPaymentDetailsEvent
                );
              }
            )
          )
        ),
      ),
      {
        strategy: CommandStrategy.CancelPrevious
      }
    );

  /**
   * Constructor
   * @param activeCartFacade ActiveCartFacade
   * @param userIdService UserIdService
   * @param commandService CommandService
   * @param queryService QueryService
   * @param eventService EventService
   * @param cmsService  CmsService
   * @param convertService ConverterService
   * @param globalMessageService GlobalMessageService
   * @param winRef WindowRef
   * @param worldpayOrderService WorldpayOrderService
   * @param worldpayApmConnector WorldpayApmConnector
   * @param worldpayCheckoutPaymentService WorldpayCheckoutPaymentService
   * @param platformLocation PlatformLocation
   * @param appBaseHref string
   */
  constructor(
    protected activeCartFacade: ActiveCartFacade,
    protected userIdService: UserIdService,
    protected commandService: CommandService,
    protected queryService: QueryService,
    protected eventService: EventService,
    protected cmsService: CmsService,
    protected convertService: ConverterService,
    protected globalMessageService: GlobalMessageService,
    protected winRef: WindowRef,
    protected worldpayOrderService: WorldpayOrderService,
    protected worldpayApmConnector: WorldpayApmConnector,
    protected worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService,
    protected platformLocation: PlatformLocation,
    @Inject(APP_BASE_HREF) protected appBaseHref: string
  ) {
    this.baseHref = getBaseHref(this.platformLocation);

    if (!this.baseHref && this.appBaseHref?.length) {
      this.baseHref = trimLastSlashFromUrl(this.appBaseHref);
    }

    this.setWorldpaySavedCreditCardEvent().subscribe();
    this.setWorldpaySaveAsDefaultCreditCardEvent().subscribe();
  }

  /**
   * Checks if the conditions for checkout are met
   * @since 6.4.0
   */
  checkoutPreconditions(): Observable<[string, string]> {
    return combineLatest([
      this.userIdService.takeUserId(),
      this.activeCartFacade.takeActiveCartId(),
      this.activeCartFacade.isGuestCart(),
    ]).pipe(
      take(1),
      map(([userId, cartId, isGuestCart]): [string, string] => {
        if (
          !userId ||
          !cartId ||
          (userId === OCC_USER_ID_ANONYMOUS && !isGuestCart)
        ) {
          throw new Error('Checkout conditions not met');
        }
        return [userId, cartId];
      })
    );
  }

  /**
   * Returns the current state of the available apms loading
   * @since 6.4.0
   */
  getLoading(): Observable<boolean> {
    return this.getWorldpayAvailableApmsLoading();
  }

  /**
   * Get APM component by id
   * @since 6.4.0
   * @param componentUid string
   * @param code PaymentMethod
   */
  getApmComponentById(componentUid: string, code: PaymentMethod): Observable<ApmData> {
    return this.cmsService.getComponentData<OccCmsComponentWithMedia>(componentUid)
      .pipe(this.convertService.pipeable(COMPONENT_APM_NORMALIZER),
        map((apmData: ApmData) => ({
          ...apmData,
          code
        }))
      );
  }

  /**
   * Get selected APM
   * @since 6.4.0
   * @param apm
   */
  selectAPM(apm: ApmPaymentDetails): void {
    this.selectedApm$.next(apm);
    this.eventService.dispatch({ apm }, SelectWorldpayAPMEvent);
  }

  /**
   * Get selected APM from state
   * @since 6.4.0
   */
  getSelectedAPMFromState(): Observable<ApmPaymentDetails> {
    return this.selectedApm$.asObservable();
  }

  /**
   * Get selected APM from state
   * @since 6.4.0
   */
  getSelectedAPMEvent(): Observable<ApmData> {
    return this.eventService.get(SelectWorldpayAPMEvent).pipe(
      map((event: SelectWorldpayAPMEvent) => event.apm)
    );
  }

  /**
   * Get APM redirect url
   * @since 6.4.0
   */
  getWorldpayAPMRedirectUrl(apm: ApmPaymentDetails, save: boolean): Observable<APMRedirectResponse> {
    return this.getWorldpayAPMRedirectUrlCommand$.execute({
      apm,
      save
    });
  }

  /**
   * Set APM redirect url
   * @since 6.4.0
   * @param apmRedirectUrl APMRedirectResponse
   */
  setWorldpayAPMRedirectUrl(apmRedirectUrl: APMRedirectResponse): void {
    this.apmRedirectUrl$.next(apmRedirectUrl);
  }

  /**
   * Set APM redirect url event
   * @since 6.4.0
   * @param apmRedirectUrl
   */
  setWorldpayAPMRedirectUrlEvent(apmRedirectUrl: APMRedirectResponse): void {
    this.setWorldpayAPMRedirectUrl(apmRedirectUrl);
    return this.eventService.dispatch({
      apmRedirectUrl
    }, SetWorldpayAPMRedirectResponseEvent);
  }

  /**
   * Get APM redirect url
   * @since 6.4.0
   */
  getWorldpayAPMRedirectUrlFromState(): Observable<APMRedirectResponse> {
    return this.eventService.get(SetWorldpayAPMRedirectResponseEvent).pipe(
      map((event: SetWorldpayAPMRedirectResponseEvent) => event.apmRedirectUrl)
    );
  }

  /**
   * Redirect authorise APM payment
   * @since 6.4.0
   */
  requestAvailableApmsState(): Observable<QueryState<ApmData[]>> {
    return this.getAvailableApmQuery$.getState();
  }

  getWorldpayAvailableApmsLoading(): Observable<boolean> {
    return this.requestAvailableApmsState().pipe(
      map((queryState: QueryState<ApmData[]>) => queryState.loading),
    );
  }

  /**
   * Get available APMs
   * @since 6.4.0
   */
  getWorldpayAvailableApms(): Observable<ApmData[]> {
    return this.requestAvailableApmsState().pipe(
      map((queryState: QueryState<ApmData[]>) => queryState.data),
    );
  }

  /**
   * Get Worldpay APM Redirect Url
   * @since 6.4.0
   * @param apm ApmData
   * @param save boolean
   */
  getAPMRedirectUrl(apm: ApmData, save: boolean): void {
    this.getWorldpayAPMRedirectUrl(apm, save).pipe(
      take(1)
    ).subscribe({
      next: (response: APMRedirectResponse): void => {
        const apmRedirect = { ...response };
        if (apmRedirect?.parameters?.entry?.length > 0) {
          const currentHost = this.winRef.location.host;
          apmRedirect.parameters.entry.forEach((entry, index) => {
            const entryValue: string = entry.value.toLowerCase();
            if (entryValue.toLowerCase().includes(currentHost.toLowerCase()) && !entryValue.includes(this.baseHref.toLowerCase())) {
              apmRedirect.parameters.entry[index].value = entryValue.replace(currentHost, currentHost + this.baseHref);
            }
          });
        }
        this.setWorldpayAPMRedirectUrlEvent(apmRedirect);
      },
      error: (error: unknown): void => {
        this.worldpayOrderService.clearLoading();
        this.showErrorMessage(error as HttpErrorModel);
      }
    });
  }

  /**
   * Show error message
   * @param error HttpErrorModel
   */
  showErrorMessage(error: HttpErrorModel): void {
    const errorMessage: string = error?.details?.[0]?.message || ' ';
    this.globalMessageService.add({ key: errorMessage }, GlobalMessageType.MSG_TYPE_ERROR);
  }

  /**
   * set APM payment details
   * @since 6.4.0
   * @param apmPaymentDetails ApmPaymentDetails
   */
  setApmPaymentDetails(apmPaymentDetails: ApmPaymentDetails): Observable<Cart> {
    return this.setApmPaymentDetailsCommand$.execute(apmPaymentDetails);
  }

  /**
   * Clear Worldpay Payment Details Event
   * @since 6.4.0
   */
  setWorldpaySavedCreditCardEvent(): Observable<void> {
    return this.eventService.get(SetWorldpaySavedCreditCardEvent).pipe(
      map((event: SetWorldpaySavedCreditCardEvent) => this.worldpayCheckoutPaymentService.setSaveCreditCardValue(event.saved))
    );
  }

  /**
   * Clear Worldpay Payment Details Event
   * @since 6.4.0
   */
  setWorldpaySaveAsDefaultCreditCardEvent(): Observable<void> {
    return this.eventService.get(SetWorldpaySaveAsDefaultCreditCardEvent).pipe(
      map((event: SetWorldpaySaveAsDefaultCreditCardEvent) => this.worldpayCheckoutPaymentService.setSaveAsDefaultCardValue(event.saved))
    );
  }
}
