import { NgModule } from '@angular/core';
import { WorldpayCheckoutPaymentMethodComponent } from './worldpay-checkout-payment-method.component';
import { WORLDPAY_CHECKOUT_PAYMENT_METHOD_FEATURE_PROVIDERS } from './worldpay-checkout-payment-method.providers';

/**
 * Angular module for the Worldpay Checkout Payment Method feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayCheckoutPaymentMethodComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayCheckoutPaymentMethod()`.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * CMS configuration and exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayCheckoutPaymentMethodComponent` is now a standalone component.
 * - All feature-specific providers and CMS mappings have been moved to `provideWorldpayCheckoutPaymentMethod()`.
 */
@NgModule({
  imports: [WorldpayCheckoutPaymentMethodComponent],
  exports: [WorldpayCheckoutPaymentMethodComponent],
  providers: WORLDPAY_CHECKOUT_PAYMENT_METHOD_FEATURE_PROVIDERS,
})
export class WorldpayCheckoutPaymentMethodModule {
}
