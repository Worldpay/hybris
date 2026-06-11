import { NgModule } from '@angular/core';
import { WorldpayCheckoutScheduledReplenishmentPlaceOrderModule } from './checkout-place-order';
import { WorldpayCheckoutScheduleReplenishmentOrderModule } from './checkout-schedule-replenishment-order';

/**
 * Angular module for the Worldpay Checkout Scheduled Replenishment components.
 *
 * This module is maintained for backward compatibility. For new implementations,
 * it is recommended to use the standalone `WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent` or `WorldpayCheckoutScheduleReplenishmentOrderComponent`.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent` or `WorldpayCheckoutScheduleReplenishmentOrderComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) if needed.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically imports the required scheduled replenishment components.
 *
 * @since 221121.11.0
 * - `WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent` is now a standalone component.
 */
@NgModule({
  imports: [
    WorldpayCheckoutScheduledReplenishmentPlaceOrderModule,
    WorldpayCheckoutScheduleReplenishmentOrderModule
  ],
  exports: [
    WorldpayCheckoutScheduledReplenishmentPlaceOrderModule,
    WorldpayCheckoutScheduleReplenishmentOrderModule
  ]
})
export class WorldpayCheckoutScheduledReplenishmentComponentsModule {
}
