import { Observable } from 'rxjs';
import { Address } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import {BrowserInfo, PlaceOrderResponse, ThreeDsDDCInfo } from '../interfaces';
import { PaymentDetails } from '@spartacus/cart/base/root';

export abstract class WorldpayAdapter {

  /**
   * Abstract method get Public Key
   * @since 6.4.0
   * @returns {Observable<string>} - PublicKey string as Observable
   */
  abstract getPublicKey(): Observable<string>;

  /**
   * Abstract method used to create set paymetn address
   * @since 6.4.0
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {Address} address - Address
   * @returns {Observable<Address>} - Address as Observable
   */
  abstract setPaymentAddress(
    userId: string,
    cartId: string,
    address: Address
  ): Observable<Address>;

  /**
   * get the JWT and form url for 3DS Flex Device Data Collection (DDC)
   * https://developer.worldpay.com/docs/wpg/directintegration/3ds2
   * @since 6.4.0
   * @returns {Observable<ThreeDsDDCInfo>} - ThreeDsDDCInfo as Observable
   */
  abstract getDDC3dsJwt(): Observable<ThreeDsDDCInfo>;

  /**
   * Perform the initial XML Payment Request.
   * Based on the response from this call we show a challenge or we perform the second payment request
   * https://developer.worldpay.com/docs/wpg/directintegration/3ds2#initial-xml-payment-request
   * @since 6.4.0
   * @param {string} userId - User ID uid of current user
   * @param {string} cartId - Cart ID
   * @param {PaymentDetails} paymentDetails anonymized payment details
   * @param {string} dfReferenceId reference id obtained during DDT
   * @param {string} challengeWindowSize width x height of challenge window
   * @param {string} cseToken CSE token
   * @param {string} acceptedTermsAndConditions boolean must be true
   * @param {string} deviceSession optional FraudSight unique session id
   * @param {BrowserInfo} browserInfo Browser Information
   */
  abstract initialPaymentRequest(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails,
    dfReferenceId: string,
    challengeWindowSize: string,
    cseToken: string,
    acceptedTermsAndConditions: boolean,
    deviceSession: string,
    browserInfo: BrowserInfo
  ): Observable<PlaceOrderResponse>;

  /**
   * Get the order given userId and code
   * @since 6.4.0
   * @param {string} userId - User ID uid of current user
   * @param {string} code code of the newly created order
   * @param guestCustomer {boolean} boolean indicating if the user is a guest
   * @returns {Observable<Order>} - Order as Observable
   */
  abstract getOrder(userId: string, code: string, guestCustomer: boolean): Observable<Order>;
}
