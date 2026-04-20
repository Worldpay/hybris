/* eslint-disable @typescript-eslint/no-explicit-any */
import { Observable } from 'rxjs';
import { ApplePayAuthorization, ApplePayPaymentRequest, ValidateMerchant } from '../../interfaces';

export abstract class WorldpayApplepayAdapter {

  /**
   * Request ApplePay Payment Request for given user and cart
   *
   * https://developer.apple.com/documentation/apple_pay_on_the_web/apple_pay_js_api/requesting_an_apple_pay_payment_session
   * @since 6.4.0
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @returns {Observable<ApplePayPaymentRequest>} - ApplePayPaymentRequest as Observable
   */
  abstract requestApplePayPaymentRequest(
    userId: string,
    cartId: string
  ): Observable<ApplePayPaymentRequest>;

  /**
   * Validate the merchant for ApplePay
   * @since 6.4.0
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {string} validationURL - Validation URL
   * @returns {Observable<ValidateMerchant>} - ValidateMerchant as Observable
   */
  abstract validateApplePayMerchant(
    userId: string,
    cartId: string,
    validationURL: string
  ): Observable<ValidateMerchant>;

  /**
   * Handle the order after Apple Pay has authorized the payment
   * @since 6.4.0
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {any} payment - Apple Payment Method
   * @returns {Observable<ApplePayAuthorization>} - ApplePayAuthorization as Observable
   */
  abstract authorizeApplePayPayment(
    userId: string,
    cartId: string,
    payment: any
  ): Observable<ApplePayAuthorization>;

}
