import { Cart } from '@spartacus/cart/base/root';
import { PaymentDetails } from "@spartacus/core";
import { Order } from '@spartacus/order/root';
import { Observable } from 'rxjs';
import { ApmData, ApmPaymentDetails, APMRedirectResponse } from '../../interfaces';

export abstract class WorldpayApmAdapter {

  /**
   * Redirect authorise APM payment
   * @since 4.3.6
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {ApmPaymentDetails} apm
   * @param {boolean} save - 'true' to save payment data.
   * @returns {Observable<APMRedirectResponse>} - APMRedirectResponse as Observable
   */
  abstract authoriseApmRedirect(
    userId: string,
    cartId: string,
    apm: ApmPaymentDetails,
    save: boolean
  ): Observable<APMRedirectResponse>;

  /**
   * Get a list of all available APM's for the given cart
   * @since 4.3.6
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @returns {Observable<ApmData[]>} - ApmData as Observable
   */
  abstract getAvailableApms(userId: string, cartId: string): Observable<ApmData[]>;

  /**
   * Try placing the order after the user has been sent back from PSP to Spartacus
   * @since 4.3.6
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @returns {Observable<Order>} - Order as Observable
   */
  abstract placeRedirectOrder(userId: string, cartId: string): Observable<Order>;

  /**
   * Try placing the order after the user has been sent back from PSP to Spartacus
   * @since 4.3.6
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {string} orderId - Order ID
   * @returns {Observable<Order>} - Order as Observable
   */
  abstract placeBankTransferOrderRedirect(userId: string, cartId: string, orderId: string): Observable<Order>;

  /**
   * Set APM Payment Information
   * @since 4.3.6
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {ApmPaymentDetails} apmPaymentDetails - Apm Payment Details
   * @returns {Observable<Cart>} - Cart as Observable
   */
  abstract setAPMPaymentInfo(
    userId: string,
    cartId: string,
    apmPaymentDetails: ApmPaymentDetails
  ): Observable<Cart>;

  /**
   * Use existing payment details
   * @since 4.3.6
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {PaymentDetails} paymentDetails - Payment Details
   * @returns {Observable<PaymentDetails>} - Payment Details as Observable
   */
  abstract useExistingPaymentDetails(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails
  ): Observable<PaymentDetails>;
}
