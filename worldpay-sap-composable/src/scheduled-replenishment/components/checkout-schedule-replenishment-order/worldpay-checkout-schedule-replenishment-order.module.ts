import { NgModule } from '@angular/core';
import { WorldpayCheckoutScheduleReplenishmentOrderComponent } from './worldpay-checkout-schedule-replenishment-order.component';
import { WORLDPAY_CHECKOUT_SCHEDULE_REPLENISHMENT_ORDER_FEATURE_PROVIDERS } from './worldpay-checkout-schedule-replenishment-order.providers';

/**
 * Angular module for the Worldpay Checkout Schedule Replenishment Order feature.
 *
 * This module is maintained for backward compatibility. For new implementations,
 * it is recommended to use the standalone `WorldpayCheckoutScheduleReplenishmentOrderComponent`.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayCheckoutScheduleReplenishmentOrderComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayScheduleReplenishmentOrder()` if needed.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * CMS configuration and exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayCheckoutScheduleReplenishmentOrderComponent` is now a standalone component.
 */
@NgModule({
  imports: [WorldpayCheckoutScheduleReplenishmentOrderComponent],
  exports: [WorldpayCheckoutScheduleReplenishmentOrderComponent],
  providers: WORLDPAY_CHECKOUT_SCHEDULE_REPLENISHMENT_ORDER_FEATURE_PROVIDERS,
})
export class WorldpayCheckoutScheduleReplenishmentOrderModule {
}
