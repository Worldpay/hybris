import { Injectable } from '@angular/core';
import { Cart } from '@spartacus/cart/base/root';
import { PaymentDetails } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Observable } from 'rxjs';
import { ApmData, ApmPaymentDetails, APMRedirectResponse } from '../../interfaces';
import { WorldpayApmAdapter } from './worldpay-apm.adapter';

@Injectable({
  providedIn: 'root'
})
export class WorldpayApmConnector {

  /**
   * Constructor
   *
   * Initializes the WorldpayApmConnector with the provided WorldpayApmAdapter.
   *
   * @param adapter - WorldpayApmAdapter instance used for making APM-related requests
   * @since 6.4.0
   */
  constructor(
    protected adapter: WorldpayApmAdapter
  ) {
  }

  /**
   * Authorise APM Redirect
   *
   * This method sends a request to authorise an Alternative Payment Method (APM) redirect.
   * It uses the provided user ID, card ID, APM payment details, and a flag indicating whether to save the payment details.
   * The response is an observable emitting the APM redirect response.
   *
   * @param userId - User ID
   * @param cartId - Card ID
   * @param apm - APM payment details
   * @param save - Flag indicating whether to save the payment details
   * @returns Observable<APMRedirectResponse> - Observable emitting the APM redirect response
   * @since 6.4.0
   */
  authoriseApmRedirect(
    userId: string,
    cartId: string,
    apm: ApmPaymentDetails,
    save: boolean
  ): Observable<APMRedirectResponse> {
    return this.adapter.authoriseApmRedirect(
      userId,
      cartId,
      apm,
      save
    );
  }

  /**
   * Get a list of all available APM's for the given cart
   * @since 6.4.0
   * @param userId - User ID - User ID
   * @param cartId - Cart ID
   * @returns Observable<ApmData[]> - ApmData as Observable
   */
  getAvailableApms(
    userId: string,
    cartId: string
  ): Observable<ApmData[]> {
    return this.adapter.getAvailableApms(userId, cartId);
  }

  /**
   * Try placing the order after the user has been sent back from PSP to Spartacus
   * @since 6.4.0
   * @param userId - User ID - User ID
   * @param cartId - Cart ID
   * @returns Observable<Order> - Order as Observable
   */
  placeOrderRedirect(
    userId: string,
    cartId: string
  ): Observable<Order> {
    return this.adapter.placeRedirectOrder(userId, cartId);
  }

  /**
   * Place Bank Transfer Order Redirect
   *
   * This method attempts to place an order after the user has been redirected back from the Payment Service Provider (PSP) to Spartacus.
   * It uses the provided user ID, cart ID, and order ID.
   * The response is an observable emitting the placed order.
   *
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param orderId - Order ID
   * @returns Observable<Order> - Observable emitting the placed order
   * @since 6.4.0
   */
  placeBankTransferOrderRedirect(
    userId: string,
    cartId: string,
    orderId: string
  ): Observable<Order> {
    return this.adapter.placeBankTransferOrderRedirect(userId, cartId, orderId);
  }

  /**
   * Set APM payment info
   * @since 6.4.0
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param apmPaymentDetails - ApmPaymentDetails
   * @returns Observable<Cart> - Cart as Observable
   */
  public setAPMPaymentInfo(
    userId: string,
    cartId: string,
    apmPaymentDetails: ApmPaymentDetails
  ): Observable<Cart> {
    return this.adapter.setAPMPaymentInfo(userId, cartId, apmPaymentDetails);
  }

  /**
   * Use Existing Payment Details
   *
   * This method uses the provided existing payment details for the given user and cart.
   * The response is an observable emitting the payment details.
   *
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param paymentDetails - Payment details to be used
   * @returns Observable<PaymentDetails> - Observable emitting the payment details
   * @since 6.4.0
   */
  public useExistingPaymentDetails(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails
  ): Observable<PaymentDetails> {
    return this.adapter.useExistingPaymentDetails(
      userId,
      cartId,
      paymentDetails
    );
  }
}
