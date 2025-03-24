import { APP_BASE_HREF, KeyValue, PlatformLocation } from '@angular/common';
import { inject, Inject, Injectable } from '@angular/core';
import { ActiveCartFacade, Cart } from '@spartacus/cart/base/root';
import { CheckoutDeliveryAddressCreatedEvent, CheckoutDeliveryAddressSetEvent, CheckoutPaymentDetailsSetEvent } from '@spartacus/checkout/base/root';
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
  LoggerService,
  OCC_USER_ID_ANONYMOUS,
  Query,
  QueryNotifier,
  QueryService,
  QueryState,
  UserIdService,
  WindowRef
} from '@spartacus/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { map, switchMap, take, tap } from 'rxjs/operators';
import { WorldpayApmConnector } from '../../connectors/worldpay-apm/worldpay-apm.connector';
import {
  ClearWorldpayPaymentDetailsEvent,
  SelectWorldpayAPMEvent,
  SetWorldpayAPMRedirectResponseEvent,
  SetWorldpaySaveAsDefaultCreditCardEvent,
  SetWorldpaySavedCreditCardEvent
} from '../../events/worldpay.events';
import { WorldpayApmFacade } from '../../facade/worldpay-apm-facade';
import { ApmData, ApmPaymentDetails, APMRedirectResponse, OccCmsComponentWithMedia, PaymentMethod } from '../../interfaces';
import { COMPONENT_APM_NORMALIZER } from '../../occ/converters';
import { getBaseHref, trimLastSlashFromUrl } from '../../utils';
import { WorldpayCheckoutPaymentService } from '../worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayOrderService } from '../worldpay-order/worldpay-order.service';

@Injectable({
  providedIn: 'root'
})
export class WorldpayApmService implements WorldpayApmFacade {
  baseHref: string;
  protected selectedApm$: BehaviorSubject<ApmPaymentDetails> = new BehaviorSubject<ApmPaymentDetails>(null);
  protected apmRedirectUrl$: BehaviorSubject<APMRedirectResponse> = new BehaviorSubject<APMRedirectResponse>(null);
  protected logger: LoggerService = inject(LoggerService);
  /**
   * Command used to get available APMs
   * @since 6.4.0
   * @returns Query<ApmData[]> - Query with ApmData[]
   */
  protected getAvailableApmQuery$: Query<ApmData[]> =
    this.queryService.create<ApmData[]>(
      (): Observable<ApmData[]> => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]: [string, string]): Observable<ApmData[]> =>
          this.worldpayApmConnector.getAvailableApms(userId, cartId)
        )
      ),
      {
        reloadOn: this.reloadSelectedApmEvents()
      }
    );

  /**
   * Command used to set APM payment details.
   * Executes the command to set the APM payment details and dispatches the CheckoutPaymentDetailsSetEvent.
   * @type {Command<ApmPaymentDetails, Cart>}
   * @since 6.4.0
   */
  protected setApmPaymentDetailsCommand$: Command<ApmPaymentDetails, Cart> =
    this.commandService.create<ApmPaymentDetails, Cart>(
      (apmPaymentDetails: ApmPaymentDetails): Observable<Cart> => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]: [string, string]): Observable<Cart> =>
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
   * @returns Command<{ apm: ApmPaymentDetails; save: boolean }, APMRedirectResponse> - Command with ApmPaymentDetails and APMRedirectResponse
   * @since 6.4.0
   */
  protected getWorldpayAPMRedirectUrlCommand$: Command<{ apm: ApmPaymentDetails; save: boolean }, APMRedirectResponse> =
    this.commandService.create<{ apm: ApmPaymentDetails; save: boolean }, APMRedirectResponse>(
      ({
        apm,
        save
      }: { apm: ApmPaymentDetails; save: boolean }): Observable<APMRedirectResponse> => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]: [string, string]): Observable<APMRedirectResponse> =>
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
   * Checks if the conditions for checkout are met.
   * Combines the user ID, cart ID, and guest cart status to determine if checkout can proceed.
   * Throws an error if any of the conditions are not met.
   * @returns {Observable<[string, string]>} An observable emitting a tuple containing the user ID and cart ID.
   * @since 6.4.0
   */
  checkoutPreconditions(): Observable<[string, string]> {
    return combineLatest([
      this.userIdService.takeUserId(),
      this.activeCartFacade.takeActiveCartId(),
      this.activeCartFacade.isGuestCart(),
    ]).pipe(
      take(1),
      map(([userId, cartId, isGuestCart]: [string, string, boolean]): [string, string] => {
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
   * Returns an observable that emits the loading state of the available APMs.
   * @returns {Observable<boolean>} An observable emitting a boolean indicating the loading state.
   * @since 6.4.0
   */
  getLoading(): Observable<boolean> {
    return this.getWorldpayAvailableApmsLoading();
  }

  /**
   * Retrieves the APM component by its ID and associates it with a payment method code.
   * @param {string} componentUid - The unique identifier of the component.
   * @param {PaymentMethod} code - The payment method code to associate with the component.
   * @returns {Observable<ApmData>} An observable emitting the APM data with the associated payment method code.
   * @since 6.4.0
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
   * Selects an APM (Alternative Payment Method) and updates the selected APM state.
   * Dispatches the SelectWorldpayAPMEvent with the selected APM details.
   * @param {ApmPaymentDetails} apm - The APM payment details to select.
   * @since 6.4.0
   */
  selectAPM(apm: ApmPaymentDetails): void {
    this.selectedApm$.next(apm);
    this.eventService.dispatch({ apm }, SelectWorldpayAPMEvent);
  }

  /**
   * Get selected APM from state.
   * @returns {Observable<ApmPaymentDetails>} An observable emitting the selected APM payment details.
   * @since 6.4.0
   */
  getSelectedAPMFromState(): Observable<ApmPaymentDetails> {
    return this.selectedApm$.asObservable();
  }

  /**
   * Get selected APM event.
   * @returns {Observable<ApmData>} An observable emitting the selected APM data.
   * @since 6.4.0
   */
  getSelectedAPMEvent(): Observable<ApmData> {
    return this.eventService.get(SelectWorldpayAPMEvent).pipe(
      map((event: SelectWorldpayAPMEvent) => event.apm)
    );
  }

  /**
   * Retrieves the Worldpay APM redirect URL.
   * @param {ApmPaymentDetails} apm - The APM payment details.
   * @param {boolean} save - Indicates whether to save the payment details.
   * @returns {Observable<APMRedirectResponse>} An observable emitting the APM redirect response.
   * @since 6.4.0
   */
  getWorldpayAPMRedirectUrl(apm: ApmPaymentDetails, save: boolean): Observable<APMRedirectResponse> {
    return this.getWorldpayAPMRedirectUrlCommand$.execute({
      apm,
      save
    });
  }

  /**
   * Sets the Worldpay APM redirect URL.
   * @param {APMRedirectResponse} apmRedirectUrl - The APM redirect response to set.
   * @since 6.4.0
   */
  setWorldpayAPMRedirectUrl(apmRedirectUrl: APMRedirectResponse): void {
    this.apmRedirectUrl$.next(apmRedirectUrl);
  }

  /**
   * Dispatches the SetWorldpayAPMRedirectResponseEvent with the provided APM redirect URL.
   * @param {APMRedirectResponse} apmRedirectUrl - The APM redirect response to set and dispatch.
   * @since 6.4.0
   */
  setWorldpayAPMRedirectUrlEvent(apmRedirectUrl: APMRedirectResponse): void {
    this.setWorldpayAPMRedirectUrl(apmRedirectUrl);
    return this.eventService.dispatch({
      apmRedirectUrl
    }, SetWorldpayAPMRedirectResponseEvent);
  }

  /**
   * Retrieves the Worldpay APM redirect URL from the state.
   * @returns {Observable<APMRedirectResponse>} An observable emitting the APM redirect response.
   * @since 6.4.0
   */
  getWorldpayAPMRedirectUrlFromState(): Observable<APMRedirectResponse> {
    return this.eventService.get(SetWorldpayAPMRedirectResponseEvent).pipe(
      map((event: SetWorldpayAPMRedirectResponseEvent) => event.apmRedirectUrl)
    );
  }

  /**
   * Retrieves the state of available APMs (Alternative Payment Methods).
   * @returns {Observable<QueryState<ApmData[]>>} An observable emitting the query state of available APMs.
   * @since 6.4.0
   */
  requestAvailableApmsState(): Observable<QueryState<ApmData[]>> {
    return this.getAvailableApmQuery$.getState();
  }

  /**
   * Returns an observable that emits the loading state of the available APMs.
   * @returns {Observable<boolean>} An observable emitting a boolean indicating the loading state.
   * @since 6.4.0
   */
  getWorldpayAvailableApmsLoading(): Observable<boolean> {
    return this.requestAvailableApmsState().pipe(
      map((queryState: QueryState<ApmData[]>) => queryState.loading),
    );
  }

  /**
   * Retrieves the available APMs (Alternative Payment Methods).
   * @returns {Observable<ApmData[]>} An observable emitting an array of APM data.
   * @since 6.4.0
   */
  getWorldpayAvailableApms(): Observable<ApmData[]> {
    return this.requestAvailableApmsState().pipe(
      map((queryState: QueryState<ApmData[]>) => queryState.data),
    );
  }

  /**
   * Retrieves the APM (Alternative Payment Method) redirect URL and updates the state.
   * @param {ApmData} apm - The APM data.
   * @param {boolean} save - Indicates whether to save the payment details.
   * @since 6.4.0
   */
  getAPMRedirectUrl(apm: ApmData, save: boolean): void {
    this.getWorldpayAPMRedirectUrl(apm, save).pipe(
      take(1)
    ).subscribe({
      next: (response: APMRedirectResponse): void => {
        const apmRedirect: APMRedirectResponse = { ...response };
        if (apmRedirect?.parameters?.entry?.length > 0) {
          const currentHost: string = this.winRef.location.host;
          apmRedirect.parameters.entry.forEach((entry: KeyValue<string, string>, index: number): void => {
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
        this.logger.error('WorldpayApmService getAPMRedirectUrl error', error);
        this.showErrorMessage(error as HttpErrorModel);
      }
    });
  }

  /**
   * Displays an error message using the GlobalMessageService.
   * @param {HttpErrorModel} error - The error model containing the error details.
   * @since 6.4.0
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
   * Listens for the SetWorldpaySavedCreditCardEvent and updates the save credit card value in the WorldpayCheckoutPaymentService.
   * @returns {Observable<void>} An observable that completes when the event is processed.
   * @since 6.4.0
   */
  setWorldpaySavedCreditCardEvent(): Observable<void> {
    return this.eventService.get(SetWorldpaySavedCreditCardEvent).pipe(
      map((event: SetWorldpaySavedCreditCardEvent) => this.worldpayCheckoutPaymentService.setSaveCreditCardValue(event?.saved))
    );
  }

  /**
   * Listens for the SetWorldpaySaveAsDefaultCreditCardEvent and updates the save as default card value in the WorldpayCheckoutPaymentService.
   * @returns {Observable<void>} An observable that completes when the event is processed.
   * @since 6.4.0
   */
  setWorldpaySaveAsDefaultCreditCardEvent(): Observable<void> {
    return this.eventService.get(SetWorldpaySaveAsDefaultCreditCardEvent).pipe(
      map((event: SetWorldpaySaveAsDefaultCreditCardEvent) => this.worldpayCheckoutPaymentService.setSaveAsDefaultCardValue(event?.saved))
    );
  }

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
}
