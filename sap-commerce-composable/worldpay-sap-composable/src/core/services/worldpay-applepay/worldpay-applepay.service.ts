/* eslint-disable @typescript-eslint/no-explicit-any */
import { Injectable } from '@angular/core';
import { BehaviorSubject, combineLatest, Observable, of } from 'rxjs';
import { distinctUntilChanged, map, switchMap, take, tap } from 'rxjs/operators';
import {
  Command,
  CommandService,
  CommandStrategy,
  EventService,
  GlobalMessageService,
  GlobalMessageType,
  OCC_USER_ID_ANONYMOUS,
  Query,
  QueryService,
  QueryState,
  UserIdService,
  WindowRef
} from '@spartacus/core';
import { createApplePaySession } from './worldpay-applepay-session';
import { WorldpayApplepayFacade } from '../../facade/worldpay-applepay.facade';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { WorldpayApplepayConnector } from '../../connectors/worldpay-applepay/worldpay-applepay.connector';
import { ApplePayAuthorizePaymentEvent, ApplePayMerchantSessionEvent, RequestApplePayPaymentRequestEvent } from '../../events/applepay.events';
import { OrderFacade } from '@spartacus/order/root';
import { ApplePayAuthorization, ApplePayPaymentRequest, PlaceOrderResponse } from '../../interfaces';

@Injectable({
  providedIn: 'root'
})
export class WorldpayApplepayService implements WorldpayApplepayFacade {

  AppleSession: any;
  nativeWindow: any = this.winRef.nativeWindow;
  merchantSession$: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  paymentAuthorization$: BehaviorSubject<ApplePayAuthorization> = new BehaviorSubject<ApplePayAuthorization>(null);

  /**
   * Request Apple Pay Payment Request
   * @since 6.4.0
   */
  protected applePayPaymentRequest$: Query<ApplePayPaymentRequest> =
    this.queryService.create<ApplePayPaymentRequest>(
      () => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]) =>
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
   * Validate Apple Pay Merchant
   * @since 6.4.0
   */
  protected validateApplePayMerchantCommand: Command<{ validationURL: string }, ApplePayPaymentRequest> =
    this.commandService.create<{ validationURL: string }, ApplePayPaymentRequest>(
      ({ validationURL }) => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]) =>
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
   * Authorize Apple Pay Payment
   * @since 6.4.0
   */
  protected authorizeApplepayPaymentCommand: Command<{ payment: any }, PlaceOrderResponse> =
    this.commandService.create<{ payment: any }, PlaceOrderResponse>(
      ({ payment }) => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]) =>
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
   * Constructor
   * @since 6.4.0
   * @param activeCartFacade
   * @param userId - User IDService
   * @param globalMessageService
   * @param orderFacade
   * @param winRef
   * @param commandService
   * @param queryService
   * @param eventService
   * @param worldpayApplepayConnector
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
   * Dispatch event to request Apple Pay request that can be used to initialize the ApplePaySession
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
   * Get ApplePaySession from window
   * @since 4.3.6
   */
  getApplePaySessionFromWindow(): any {
    return this.AppleSession;
  }

  /**
   * Check if Apple Pay button is available
   * @since 4.3.6
   */
  applePayButtonAvailable(): boolean {
    const applePaySession = this.nativeWindow.ApplePaySession;
    return !!(applePaySession && applePaySession.canMakePayments());
  }

  /**
   * Enable Apple Pay button
   * @since 4.3.6
   */
  enableApplePayButton(): Observable<ApplePayPaymentRequest> {
    if (this.applePayButtonAvailable()) {
      return this.getPaymentRequestFromState();
    }
    return of(null);
  }

  /**
   * Create Apple Pay Session
   * @since 4.3.6
   */
  createSession(paymentRequest: ApplePayPaymentRequest): any {
    const session = new this.AppleSession(5, paymentRequest);
    session.onvalidatemerchant = this.onValidateMerchant.bind(this);
    session.onpaymentauthorized = this.onPaymentAuthorized.bind(this);
    session.onerror = this.onPaymentError.bind(this);
    session.oncancel = this.onPaymentError.bind(this);
    session.begin();
    return session;
  }

  /**
   * Request Apple Pay Payment Request
   * @since 4.3.6
   */
  requestApplePayPaymentRequest(): Observable<QueryState<ApplePayPaymentRequest>> {
    return this.applePayPaymentRequest$.getState();
  }

  /**
   * Get ApplePay Payment Request
   * @since 4.3.6
   */
  getPaymentRequestFromState(): Observable<ApplePayPaymentRequest> {
    return this.requestApplePayPaymentRequest().pipe(
      map((queryState: QueryState<ApplePayPaymentRequest>) => queryState.data)
    );
  }

  /**
   * Get ApplePay Merchant Session from State
   * @since 4.3.6
   */
  getMerchantSessionFromState(): Observable<PlaceOrderResponse> {
    return this.merchantSession$.asObservable();
  }

  /**
   * Get ApplePay Payment Authorization from State
   * @since 4.3.6
   */
  getPaymentAuthorizationFromState(): Observable<ApplePayAuthorization> {
    return this.paymentAuthorization$.asObservable();
  }

  /**
   * Validate the merchant for ApplePay
   * @since 4.3.6
   * @param event
   */
  protected onValidateMerchant(event: { validationURL: string }): void {
    this.validateApplePayMerchantCommand.execute({ validationURL: event.validationURL })
      .subscribe()
      .unsubscribe();
  }

  /**
   * Handle the order after Apple Pay has authorized the payment
   * @since 4.3.6
   * @param event
   */
  protected onPaymentAuthorized(event: { payment: any }): void {
    this.authorizeApplepayPaymentCommand.execute({ payment: event.payment })
      .subscribe({
        next: (response: PlaceOrderResponse) => {
          this.orderFacade.setPlacedOrder(response.order);
        },
        error: (error: unknown): void => {
          console.log('Applepay payment Error', error);
          this.onPaymentError();
        }
      })
      .unsubscribe();
  }

  /**
   * Handle the error when Apple Pay has cancelled the payment
   * @since 4.3.6
   */
  protected onPaymentError(): void {
    this.globalMessageService.add(
      { key: 'paymentForm.applePay.cancelled' },
      GlobalMessageType.MSG_TYPE_ERROR
    );
  }

  /**
   * Get current user id and cart id
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
}
