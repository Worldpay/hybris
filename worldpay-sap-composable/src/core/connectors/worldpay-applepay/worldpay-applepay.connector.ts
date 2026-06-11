/* eslint-disable @typescript-eslint/no-explicit-any */
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApplePayAuthorization, ApplePayMerchantSession, ApplePayPayment, ApplePayPaymentRequest } from '../../models';
import { WorldpayApplepayAdapter } from './worldpay-applepay.adapter';

@Injectable({
  providedIn: 'root'
})
export class WorldpayApplepayConnector {
  protected adapter: WorldpayApplepayAdapter = inject(WorldpayApplepayAdapter);

  /**
   * Request Apple Pay Payment Request
   * @since 6.4.0
   * @param userId - User ID - User ID
   * @param cartId - Cart ID
   * @returns Observable<ApplePayPaymentRequest>
   */
  public requestApplePayPaymentRequest(
    userId: string,
    cartId: string
  ): Observable<ApplePayPaymentRequest> {
    return this.adapter.requestApplePayPaymentRequest(userId, cartId);
  }

  /**
   * Validate Apple Pay Merchant
   * @since 6.4.0
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param validationURL - Validation URL
   * @returns Observable<ApplePayMerchantSession> - Merchant Session as Observable
   */
  public validateApplePayMerchant(
    userId: string,
    cartId: string,
    validationURL: string
  ): Observable<ApplePayMerchantSession> {
    return this.adapter.validateApplePayMerchant(userId, cartId, validationURL);
  }

  /**
   * Authorize Apple Pay Payment
   * @since 6.4.0
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param payment - ApplePay Payment Method
   * @returns Observable<ApplePayAuthorization> - ApplePayAuthorization as Observable
   */
  public authorizeApplePayPayment(
    userId: string,
    cartId: string,
    payment: ApplePayPayment
  ): Observable<ApplePayAuthorization> {
    return this.adapter.authorizeApplePayPayment(userId, cartId, payment);
  }
}
