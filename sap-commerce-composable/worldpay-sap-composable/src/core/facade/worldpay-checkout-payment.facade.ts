import { Injectable } from '@angular/core';
import { Address, facadeFactory, QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import { WORLDPAY_CHECKOUT_PAYMENT_FEATURE } from './worldpay-feature-name';
import { CheckoutPaymentFacade } from '@spartacus/checkout/base/root';
import { PaymentDetails } from '@spartacus/cart/base/root';

@Injectable({
  providedIn: 'root',
  useFactory: () =>
    facadeFactory({
      facade: WorldpayCheckoutPaymentFacade,
      feature: WORLDPAY_CHECKOUT_PAYMENT_FEATURE,
      methods: [
        'getPublicKey',
        'createPaymentDetails',
        'useExistingPaymentDetails',
        'setPaymentAddress',
      ],
    }),
})
export abstract class WorldpayCheckoutPaymentFacade extends CheckoutPaymentFacade {

  /**
   * Abstract method used to create new payment details
   * @since 6.4.0
   */
  abstract getPublicKey(): Observable<QueryState<string>>;

  /**
   * Abstract method used to create new payment details
   * @since 6.4.0
   * @param paymentDetails
   */
  abstract useExistingPaymentDetails(paymentDetails: PaymentDetails): Observable<unknown>;

  /**
   * Abstract method used to create new payment details
   * @since 6.4.0
   * @param address
   */
  abstract setPaymentAddress(address: Address): Observable<Address>;

  /**
   * Abstract method used to create new payment details
   * @since 6.4.0
   */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  abstract override getPaymentDetailsState(): Observable<QueryState<PaymentDetails | undefined>>;

}
