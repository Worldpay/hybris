import { CheckoutPaymentAdapter } from '@spartacus/checkout/base/core';
import { PaymentDetails } from '@spartacus/core';
import { Observable } from 'rxjs';

export abstract class WorldpayCheckoutPaymentAdapter extends CheckoutPaymentAdapter {

  /**
   * Abstract method used to create new payment details
   * @since 6.4.0
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {PaymentDetails} paymentDetails - Payment Details
   * @param {string} cseToken - CSE Token
   * @returns {Observable<PaymentDetails>} - PaymentDetails as Observable
   */
  abstract createWorldpayPaymentDetails(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails,
    cseToken: string
  ): Observable<PaymentDetails>;

  /**
   * Abstract method set existing payment details
   * @since 6.4.0
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {PaymentDetails} paymentDetails - Payment Details
   * @returns {Observable<PaymentDetails>} - PaymentDetails as Observable
   */
  abstract useExistingPaymentDetails(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails
  ): Observable<PaymentDetails>;
}
