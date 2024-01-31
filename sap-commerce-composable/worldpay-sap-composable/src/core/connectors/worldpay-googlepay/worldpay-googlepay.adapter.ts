/* eslint-disable @typescript-eslint/no-explicit-any */
import { Observable } from 'rxjs';
import { GooglePayMerchantConfiguration, PlaceOrderResponse } from '../../interfaces';

export abstract class WorldpayGooglepayAdapter {

  /**
   * Request current merchant configuration
   * @since 6.4.0
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @returns {Observable<GooglePayMerchantConfiguration>} - GooglePayMerchantConfiguration as Observable
   */
  abstract getGooglePayMerchantConfiguration(
    userId: string,
    cartId: string
  ): Observable<GooglePayMerchantConfiguration>;

  /**
   * Authorise GooglePay payment
   * @since 6.4.0
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {any} token - GooglePay Payment Method
   * @param {any} billingAddress - Billing Address
   * @param {boolean} savePaymentMethod - 'true' to save payment data.
   * @returns {Observable<PlaceOrderResponse>} - PlaceOrderResponse as Observable
   */
  abstract authoriseGooglePayPayment(
    userId: string,
    cartId: string,
    token: any,
    billingAddress: any,
    savePaymentMethod: boolean
  ): Observable<PlaceOrderResponse>;

}
