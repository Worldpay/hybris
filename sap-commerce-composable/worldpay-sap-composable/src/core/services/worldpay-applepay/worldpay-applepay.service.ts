/* eslint-disable @typescript-eslint/no-explicit-any */
import { inject, Injectable } from '@angular/core';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import {
  Command,
  CommandService,
  CommandStrategy,
  EventService,
  GlobalMessageService,
  GlobalMessageType,
  LoggerService,
  OCC_USER_ID_ANONYMOUS,
  Query,
  QueryService,
  QueryState,
  UserIdService,
  WindowRef
} from '@spartacus/core';
import { OrderFacade } from '@spartacus/order/root';
import { BehaviorSubject, combineLatest, Observable, of } from 'rxjs';
import { distinctUntilChanged, map, switchMap, take, tap } from 'rxjs/operators';
import { WorldpayApplepayConnector } from '../../connectors/worldpay-applepay/worldpay-applepay.connector';
import { ApplePayAuthorizePaymentEvent, ApplePayMerchantSessionEvent, RequestApplePayPaymentRequestEvent } from '../../events/applepay.events';
import { WorldpayApplepayFacade } from '../../facade/worldpay-applepay.facade';
import { ApplePayAuthorization, ApplePayPaymentRequest, PlaceOrderResponse } from '../../interfaces';
import { createApplePaySession } from './worldpay-applepay-session';

@Injectable({
  providedIn: 'root'
})
export class WorldpayApplepayService implements WorldpayApplepayFacade {

  AppleSession: any;
  nativeWindow: any = this.winRef.nativeWindow;
  merchantSession$: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  paymentAuthorization$: BehaviorSubject<ApplePayAuthorization> = new BehaviorSubject<ApplePayAuthorization>(null);
  protected logger: LoggerService = inject(LoggerService);

  /**
   * Query to request Apple Pay Payment Request.
   * @protected
   * @type {Query<ApplePayPaymentRequest>}
   * @since 6.4.0
   */
  protected applePayPaymentRequest$: Query<ApplePayPaymentRequest> =
    this.queryService.create<ApplePayPaymentRequest>(
      (): Observable<ApplePayPaymentRequest> => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]: [string, string]): Observable<ApplePayPaymentRequest> =>
          this.worldpayApplepayConnector.requestApplePayPaymentRequest(userId, cartId,).pipe(
            tap((response: ApplePayPaymentRequest): void => {
              this.eventService.dispatch({
                applePayPaymentRequest: response
              }, RequestApplePayPaymentRequestEvent);
            })
          )
        )
      )
    );

  /**
   * Command to validate the Apple Pay Merchant.
   * @protected
   * @type {Command<{ validationURL: string }, ApplePayPaymentRequest>}
   * @since 6.4.0
   */
  protected validateApplePayMerchantCommand: Command<{ validationURL: string }, ApplePayPaymentRequest> =
    this.commandService.create<{ validationURL: string }, ApplePayPaymentRequest>(
      ({ validationURL }: { validationURL: string }): Observable<any> => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]: [string, string]): Observable<any> =>
          this.worldpayApplepayConnector.validateApplePayMerchant(userId, cartId, validationURL).pipe(
            tap((response: any): void => {
              this.eventService.dispatch({
                merchantSession: response
              }, ApplePayMerchantSessionEvent);
            })
          )
        )
      ),
      {
        strategy: CommandStrategy.CancelPrevious
      }
    );

  /**
   * Command to authorize the Apple Pay payment.
   * @protected
   * @type {Command<{ payment: any }, PlaceOrderResponse>}
   * @since 6.4.0
   */
  protected authorizeApplepayPaymentCommand: Command<{ payment: any }, PlaceOrderResponse> =
    this.commandService.create<{ payment: any }, PlaceOrderResponse>(
      ({ payment }: { payment: any }): Observable<PlaceOrderResponse> => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]: [string, string]): Observable<PlaceOrderResponse> =>
          this.worldpayApplepayConnector.authorizeApplePayPayment(userId, cartId, payment).pipe(
            tap((response: PlaceOrderResponse): void => {
              this.eventService.dispatch({
                authorizePaymentEvent: response
              }, ApplePayAuthorizePaymentEvent);
            })
          )
        )
      ),
      {
        strategy: CommandStrategy.CancelPrevious
      }
    );

  /**
   * Constructor for WorldpayApplepayService
   * @param {ActiveCartFacade} activeCartFacade - Facade for active cart operations
   * @param {UserIdService} userIdService - Service for user ID operations
   * @param {GlobalMessageService} globalMessageService - Service for displaying global messages
   * @param {OrderFacade} orderFacade - Facade for order operations
   * @param {CommandService} commandService - Service for creating and managing commands
   * @param {QueryService} queryService - Service for creating and managing queries
   * @param {EventService} eventService - Service for event handling
   * @param {WindowRef} winRef - Reference to the window object
   * @param {WorldpayApplepayConnector} worldpayApplepayConnector - Connector for Worldpay Apple Pay operations
   * @since 6.4.0
   */
  constructor(
    protected activeCartFacade: ActiveCartFacade,
    protected userIdService: UserIdService,
    protected globalMessageService: GlobalMessageService,
    protected orderFacade: OrderFacade,
    protected commandService: CommandService,
    protected queryService: QueryService,
    protected eventService: EventService,
    protected winRef: WindowRef,
    protected worldpayApplepayConnector: WorldpayApplepayConnector,
  ) {
    this.AppleSession = createApplePaySession(this.winRef);
    this.applepayMerchantSessionEvent();
    this.authorizePaymentEvent();
  }

  /**
   * Dispatch event to request Apple Pay request that can be used to initialize the ApplePaySession.
   * @since 6.4.0
   */
  applepayMerchantSessionEvent(): void {
    this.eventService.get(ApplePayMerchantSessionEvent).pipe(
      distinctUntilChanged()
    ).subscribe({
      next: (event: ApplePayMerchantSessionEvent): void => {
        this.merchantSession$.next(event.merchantSession);
      }
    });
  }

  /**
   * Dispatch event to request Apple Pay request that can be used to initialize the ApplePaySession
   * @since 6.4.0
   */
  authorizePaymentEvent(): void {
    this.eventService.get(ApplePayAuthorizePaymentEvent).pipe(
      distinctUntilChanged()
    ).subscribe({
      next: (event: ApplePayAuthorizePaymentEvent): void => {
        this.paymentAuthorization$.next(event.authorizePaymentEvent);
      }
    });
  }

  /**
   * Get ApplePaySession from window.
   * @returns {any} The ApplePaySession object.
   * @since 4.3.6
   */
  getApplePaySessionFromWindow(): any {
    return this.AppleSession;
  }

  /**
   * Check if Apple Pay button is available.
   * @returns {boolean} True if the Apple Pay button is available, false otherwise.
   * @since 4.3.6
   */
  applePayButtonAvailable(): boolean {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const applePaySession: any = this.nativeWindow.ApplePaySession;
    return !!(applePaySession && applePaySession.canMakePayments());
  }

  /**
   * Enable Apple Pay button.
   * @returns {Observable<ApplePayPaymentRequest>} An observable that emits the Apple Pay payment request if the Apple Pay button is available, otherwise null.
   * @since 4.3.6
   */
  enableApplePayButton(): Observable<ApplePayPaymentRequest> {
    if (this.applePayButtonAvailable()) {
      return this.getPaymentRequestFromState();
    }
    return of(null);
  }

  /**
   * Create an Apple Pay session.
   * @param {ApplePayPaymentRequest} paymentRequest - The payment request object for Apple Pay.
   * @returns {any} The created Apple Pay session.
   * @since 4.3.6
   */
  createSession(paymentRequest: ApplePayPaymentRequest): any {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const session: any = new this.AppleSession(5, paymentRequest);
    session.onvalidatemerchant = this.onValidateMerchant.bind(this);
    session.onpaymentauthorized = this.onPaymentAuthorized.bind(this);
    session.onerror = this.onPaymentError.bind(this);
    session.oncancel = this.onPaymentError.bind(this);
    session.begin();
    return session;
  }

  /**
   * Request Apple Pay Payment Request.
   * @returns {Observable<QueryState<ApplePayPaymentRequest>>} An observable that emits the state of the Apple Pay payment request.
   * @since 4.3.6
   */
  requestApplePayPaymentRequest(): Observable<QueryState<ApplePayPaymentRequest>> {
    return this.applePayPaymentRequest$.getState();
  }

  /**
   * Get Apple Pay Payment Request from state.
   * @returns {Observable<ApplePayPaymentRequest>} An observable that emits the Apple Pay payment request.
   * @since 4.3.6
   */
  getPaymentRequestFromState(): Observable<ApplePayPaymentRequest> {
    return this.requestApplePayPaymentRequest().pipe(
      map((queryState: QueryState<ApplePayPaymentRequest>) => queryState.data)
    );
  }

  /**
   * Get Apple Pay Merchant Session from state.
   * @returns {Observable<PlaceOrderResponse>} An observable that emits the Apple Pay merchant session.
   * @since 4.3.6
   */
  getMerchantSessionFromState(): Observable<PlaceOrderResponse> {
    return this.merchantSession$.asObservable();
  }

  /**
   * Get Apple Pay Payment Authorization from state.
   * @returns {Observable<ApplePayAuthorization>} An observable that emits the Apple Pay payment authorization.
   * @since 4.3.6
   */
  getPaymentAuthorizationFromState(): Observable<ApplePayAuthorization> {
    return this.paymentAuthorization$.asObservable();
  }

  /**
   * Get current user id and cart id.
   * @returns {Observable<[string, string]>} An observable that emits a tuple containing the user id and cart id.
   * @throws {Error} If the checkout conditions are not met.
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
   * Validate the merchant for ApplePay.
   * @param {Object} event - The event object containing the validation URL.
   * @param {string} event.validationURL - The validation URL for the merchant.
   * @since 4.3.6
   */
  protected onValidateMerchant(event: { validationURL: string }): void {
    this.validateApplePayMerchantCommand.execute({ validationURL: event.validationURL })
      .subscribe()
      .unsubscribe();
  }

  /**
   * Handle the order after Apple Pay has authorized the payment.
   * @param {Object} event - The event object containing the payment information.
   * @param {any} event.payment - The payment information from Apple Pay.
   * @since 4.3.6
   */
  protected onPaymentAuthorized(event: { payment: any }): void {
    this.authorizeApplepayPaymentCommand.execute({ payment: event.payment })
      .subscribe({
        next: (response: PlaceOrderResponse): void => {
          this.orderFacade.setPlacedOrder(response.order);
        },
        error: (error: unknown): void => {
          this.logger.error('Applepay payment Error', error);
          this.onPaymentError();
        }
      })
      .unsubscribe();
  }

  /**
   * Handle the error when Apple Pay has cancelled the payment.
   * @since 4.3.6
   */
  protected onPaymentError(): void {
    this.globalMessageService.add(
      { key: 'paymentForm.applePay.cancelled' },
      GlobalMessageType.MSG_TYPE_ERROR
    );
  }
}
