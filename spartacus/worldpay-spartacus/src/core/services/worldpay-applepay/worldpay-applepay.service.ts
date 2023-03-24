import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { select, Store } from '@ngrx/store';
import { StateWithWorldpay } from '../../store/worldpay.state';
import { ApplePayAuthorization, ApplePayPaymentRequest } from '../../connectors/worldpay.adapter';
import { getWorldpayApplePayMerchantSession, getWorldpayApplePayPaymentAuthorization, getWorldpayApplePayPaymentRequest } from '../../store/worldpay.selectors';
import { AuthoriseApplePayPayment, RequestApplePayPaymentRequest, StartApplePaySession, ValidateApplePayMerchant } from '../../store/worldpay.action';
import { take } from 'rxjs/operators';
import { ActiveCartService, GlobalMessageService, GlobalMessageType, UserIdService, WindowRef } from '@spartacus/core';
import { createApplePaySession } from './worldpay-applepay-session';

@Injectable({
  providedIn: 'root'
})
export class WorldpayApplepayService {

  AppleSession: any;
  nativeWindow = this.winRef.nativeWindow as any;

  constructor(
    protected worldpayStore: Store<StateWithWorldpay>,
    protected activeCartService: ActiveCartService,
    protected userIdService: UserIdService,
    protected globalMessageService: GlobalMessageService,
    protected winRef: WindowRef
  ) {
    this.AppleSession = createApplePaySession(this.winRef);
  }

  getApplePaySessionFromWindow(): any {
    return this.AppleSession;
  }

  applePayButtonAvailable(): boolean {
    const applePaySession = this.nativeWindow['ApplePaySession'];
    return !!(applePaySession && applePaySession.canMakePayments());
  }

  enableApplePayButton(): Observable<ApplePayPaymentRequest> {
    if (this.applePayButtonAvailable()) {
      this.requestApplePayPaymentRequest();
      return this.getPaymentRequestFromState();
    }
    return of(null);
  }

  createSession(paymentRequest: ApplePayPaymentRequest): any {
    const session = new this.AppleSession(5, paymentRequest);
    session.onvalidatemerchant = this.onValidateMerchant.bind(this);
    session.onpaymentauthorized = this.onPaymentAuthorized.bind(this);

    session.onerror = this.onPaymentError.bind(this);
    session.oncancel = this.onPaymentError.bind(this);

    session.begin();

    this.worldpayStore.dispatch(new StartApplePaySession());

    return session;
  }

  /**
   * Create observable for ApplePay Payment Request
   */
  getPaymentRequestFromState(): Observable<ApplePayPaymentRequest> {
    return this.worldpayStore.pipe(select(getWorldpayApplePayPaymentRequest));
  }

  getMerchantSesssionFromState(): Observable<any> {
    return this.worldpayStore.pipe(select(getWorldpayApplePayMerchantSession));
  }

  getPaymentAuthorizationFromState(): Observable<ApplePayAuthorization> {
    return this.worldpayStore.pipe(
      select(getWorldpayApplePayPaymentAuthorization)
    );
  }

  /**
   * Dispatch event to request Apple Pay request that can be used to initialize the ApplePaySession
   */
  requestApplePayPaymentRequest(): void {
    this.worldpayStore.dispatch(
      new RequestApplePayPaymentRequest({
        userId: this.getUserId(),
        cartId: this.getCartId()
      })
    );
  }

  private getCartId(): string {
    let cartId: string;
    this.activeCartService
      .getActiveCartId()
      .pipe(take(1))
      .subscribe((res: string) => (cartId = res))
      .unsubscribe();
    return cartId;
  }

  private getUserId(): string {
    let userId: string;
    this.userIdService
      .getUserId()
      .pipe(take(1))
      .subscribe((res: string) => (userId = res))
      .unsubscribe();
    return userId;
  }

  private onValidateMerchant(event: { validationURL: string }): void {
    this.worldpayStore.dispatch(
      new ValidateApplePayMerchant({
        userId: this.getUserId(),
        cartId: this.getCartId(),
        validationURL: event.validationURL
      })
    );
  }

  private onPaymentAuthorized(event: { payment: any }): void {
    this.worldpayStore.dispatch(
      new AuthoriseApplePayPayment({
        userId: this.getUserId(),
        cartId: this.getCartId(),
        payment: event.payment
      })
    );
  }

  private onPaymentError(err): void {
    this.globalMessageService.add(
      { key: 'paymentForm.applePay.cancelled' },
      GlobalMessageType.MSG_TYPE_ERROR
    );
  }
}
