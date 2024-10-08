import { Injectable } from '@angular/core';
import { CheckoutPaymentConnector } from '@spartacus/checkout/base/core';
import { PaymentDetails } from "@spartacus/core";
import { Observable } from 'rxjs';
import { WorldpayCheckoutPaymentAdapter } from './worldpay-checkout-payment.adapter';

@Injectable({
  providedIn: 'root'
})
export class WorldpayCheckoutPaymentConnector extends CheckoutPaymentConnector implements WorldpayCheckoutPaymentAdapter {

  /**
   * Constructor
   * @since 6.4.0
   * @param adapter WorldpayGuaranteedPaymentsAdapter
   */
  constructor(
    protected override adapter: WorldpayCheckoutPaymentAdapter
  ) {
    super(adapter);
  }

  /**
   * Method used to Create New Payment Method
   * @since 6.4.0
   * @param userId - User ID - User ID
   * @param cartId - Cart ID
   * @param paymentDetails PaymentDetails
   * @param cseToken string
   */
  public createWorldpayPaymentDetails(userId: string, cartId: string, paymentDetails: PaymentDetails, cseToken: string): Observable<PaymentDetails> {
    return this.adapter.createWorldpayPaymentDetails(userId, cartId, paymentDetails, cseToken);
  }

  /**
   * Method used to set Existing Payment Method
   * @param userId - User ID - User ID
   * @param cartId - Cart ID
   * @param paymentDetails PaymentDetails
   */
  public useExistingPaymentDetails(userId: string, cartId: string, paymentDetails: PaymentDetails): Observable<PaymentDetails> {
    return this.adapter.useExistingPaymentDetails(userId, cartId, paymentDetails);
  }
}
