import { NgModule } from '@angular/core';
import { WorldpayCheckoutPlaceOrderComponent } from './worldpay-checkout-place-order.component';
import { WORLDPAY_CHECKOUT_PLACE_ORDER_FEATURE_PROVIDERS } from './worldpay-checkout-place-order.providers';

/**
 * Angular module for the Worldpay Checkout Place Order feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayCheckoutPlaceOrderComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayCheckoutPlaceOrder()`.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * CMS configuration and exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayCheckoutPlaceOrderComponent` is now a standalone component.
 * - All feature-specific providers and CMS mappings have been moved to `provideWorldpayCheckoutPlaceOrder()`.
 */
@NgModule({
  imports: [WorldpayCheckoutPlaceOrderComponent],
  exports: [WorldpayCheckoutPlaceOrderComponent],
  providers: WORLDPAY_CHECKOUT_PLACE_ORDER_FEATURE_PROVIDERS,
})
export class WorldpayCheckoutPlaceOrderModule {
}
