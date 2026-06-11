import { NgModule } from '@angular/core';
import { WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent } from './worldpay-checkout-place-order.component';
import { WORLDPAY_CHECKOUT_PLACE_ORDER_FEATURE_PROVIDERS } from './worldpay-checkout-place-order.providers';

/**
 * Angular module for the Worldpay Checkout Scheduled Replenishment Place Order feature.
 *
 * This module is maintained for backward compatibility. For new implementations,
 * it is recommended to use the standalone `WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent`.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayPlaceOrder()` if needed.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * CMS configuration and exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent` is now a standalone component.
 */
@NgModule({
  imports: [WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent],
  exports: [WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent],
  providers: WORLDPAY_CHECKOUT_PLACE_ORDER_FEATURE_PROVIDERS
})
export class WorldpayCheckoutScheduledReplenishmentPlaceOrderModule {
}
