import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayCheckoutScheduleReplenishmentOrderComponent } from './worldpay-checkout-schedule-replenishment-order.component';

/**
 * Providers for the Worldpay Checkout Schedule Replenishment Order feature.
 *
 * This array includes the CMS configuration for the CheckoutScheduleReplenishmentOrder CMS component,
 * specifying the component and required guards for route protection.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayScheduleReplenishmentOrder()`.
 * 2. The configuration ensures the correct component and guards are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_CHECKOUT_SCHEDULE_REPLENISHMENT_ORDER_FEATURE_PROVIDERS: Provider[] = [
  provideConfig({
    cmsComponents: {
      CheckoutScheduleReplenishmentOrder: {
        component: WorldpayCheckoutScheduleReplenishmentOrderComponent,
        guards: [CheckoutAuthGuard, CartNotEmptyGuard],
      },
    },
  } as CmsConfig)
];

/**
 * Factory function to provide all Worldpay Checkout Schedule Replenishment Order feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay Checkout Schedule Replenishment Order feature
 * @since 221121.11.0
 */
export function provideWorldpayScheduleReplenishmentOrder(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_CHECKOUT_SCHEDULE_REPLENISHMENT_ORDER_FEATURE_PROVIDERS);
}