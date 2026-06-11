import { NgModule } from '@angular/core';
import { WorldpayB2bCheckoutPaymentMethodComponent } from './worldpay-b2b-checkout-payment-method.component';
import { WORLDPAY_B2B_CHECKOUT_PAYMENT_METHOD_PROVIDERS } from './worldpay-b2b-checkout-payment-method.providers';

/**
 * Angular module for the Worldpay B2B Checkout Payment Method feature.
 *
 * This module enables integration of the WorldpayB2bCheckoutPaymentMethodComponent and its providers
 * for B2B checkout payment method functionality. For new implementations, it is recommended to use
 * the standalone WorldpayB2bCheckoutPaymentMethodComponent and register providers via `provideWorldpayB2BCheckoutPaymentMethod()`
 * in your app configuration.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayB2bCheckoutPaymentMethodComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayB2BCheckoutPaymentMethod()`.
 *
 * ### Module Usage:
 * Import this module to automatically register the required providers for the CheckoutPaymentDetails
 * CMS component and export the standalone component.
 *
 * @since 221121.11.0
 */
@NgModule({
  imports: [WorldpayB2bCheckoutPaymentMethodComponent],
  exports: [WorldpayB2bCheckoutPaymentMethodComponent],
  providers: WORLDPAY_B2B_CHECKOUT_PAYMENT_METHOD_PROVIDERS
})
export class WorldpayB2bCheckoutPaymentMethodModule {
}
