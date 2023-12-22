/* eslint-disable @typescript-eslint/no-explicit-any */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GooglePayMerchantConfiguration, PlaceOrderResponse } from '../../interfaces';
import { WorldpayGooglepayAdapter } from './worldpay-googlepay.adapter';

@Injectable({
  providedIn: 'root'
})
export class WorldpayGooglePayConnector {

  /**
   * Constructor
   * @since 6.4.0
   * @param adapter WorldpayApplepayAdapter
   */
  constructor(
    protected adapter: WorldpayGooglepayAdapter
  ) {
  }

  /**
   * Request current merchant configuration
   * @since 6.4.0
   * @param userId - User ID - User ID
   * @param cartId - Cart ID
   * @returns Observable<GooglePayMerchantConfiguration> - GooglePayMerchantConfiguration as Observable
   */
  getGooglePayMerchantConfiguration(
    userId: string,
    cartId: string
  ): Observable<GooglePayMerchantConfiguration> {
    return this.adapter.getGooglePayMerchantConfiguration(userId, cartId);
  }

  /**
   * Authorise GooglePay payment
   * @since 6.4.0
   * @param userId - User ID - User ID
   * @param cartId - Cart ID
   * @param token any
   * @param billingAddress any
   * @param savePaymentMethod boolean
   *
   * @returns Observable<PlaceOrderResponse>
   */
  authoriseGooglePayPayment(
    userId: string,
    cartId: string,
    token: any,
    billingAddress: any,
    savePaymentMethod: boolean
  ): Observable<PlaceOrderResponse> {
    return this.adapter.authoriseGooglePayPayment(
      userId,
      cartId,
      token,
      billingAddress,
      savePaymentMethod
    );
  }
}
