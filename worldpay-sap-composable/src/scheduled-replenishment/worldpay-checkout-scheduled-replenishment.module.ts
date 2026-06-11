import { NgModule } from '@angular/core';
import { WorldpayCheckoutScheduledReplenishmentComponentsModule } from './components/worldpay-checkout-scheduled-replenishment-components.module';

/**
 * Angular module for the Worldpay Checkout Scheduled Replenishment feature.
 *
 * This module is maintained for backward compatibility. For new implementations,
 * it is recommended to use the standalone `WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent`.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) if needed.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically imports the required
 * scheduled replenishment components module.
 *
 * @since 221121.11.0
 * - `WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent` is now a standalone component.
 */
@NgModule({
  imports: [WorldpayCheckoutScheduledReplenishmentComponentsModule],
})
export class WorldpayCheckoutScheduledReplenishmentModule {
}
