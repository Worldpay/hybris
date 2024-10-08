import { Injectable } from '@angular/core';
import { Cart } from '@spartacus/cart/base/root';
import { PaymentDetails } from "@spartacus/core";
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
   * @since 6.4.0
   * @param adapter - WorldpayApmAdapter
   */
  constructor(
    protected adapter: WorldpayApmAdapter
  ) {
  }

  /**
   * Redirect authorise APM payment
   * @since 6.4.0
   * @param userId - User ID
   * @param cardId string
   * @param apm ApmPaymentDetails
   * @param save boolean
   * @returns Observable<APMRedirectResponse> - APMRedirectResponse as Observable
   */
  authoriseApmRedirect(
    userId: string,
    cardId: string,
    apm: ApmPaymentDetails,
    save: boolean
  ): Observable<APMRedirectResponse> {
    return this.adapter.authoriseApmRedirect(
      userId,
      cardId,
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
   * Try placing the order after the user has been sent back from PSP to Spartacus
   * @since 6.4.0
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param orderId - Order ID
   * @returns Observable<Order> - Order as Observable
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
   * Use existing payment details
   * @since 6.4.0
   * @param userId - User ID
   * @param cartId
   * @param paymentDetails PaymentDetails
   * @returns Observable<PaymentDetails> - PaymentDetails as Observable
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
