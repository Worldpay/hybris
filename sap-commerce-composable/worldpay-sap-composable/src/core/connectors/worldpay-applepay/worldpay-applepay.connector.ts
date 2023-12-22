/* eslint-disable @typescript-eslint/no-explicit-any */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { WorldpayApplepayAdapter } from './worldpay-applepay.adapter';
import { ApplePayAuthorization, ApplePayPaymentRequest, ValidateMerchant } from '../../interfaces';

@Injectable({
  providedIn: 'root'
})
export class WorldpayApplepayConnector {

  /**
   * Constructor
   * @since 6.4.0
   * @param adapter
   */
  constructor(
    protected adapter: WorldpayApplepayAdapter
  ) {
  }

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
   * @returns Observable<ValidateMerchant> - ValidateMerchant as Observable
   */
  public validateApplePayMerchant(
    userId: string,
    cartId: string,
    validationURL: string
  ): Observable<ValidateMerchant> {
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
    payment: any
  ): Observable<ApplePayAuthorization> {
    return this.adapter.authorizeApplePayPayment(userId, cartId, payment);
  }
}
