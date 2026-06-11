import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent } from './worldpay-checkout-place-order.component';

/**
 * Providers for the Worldpay Checkout Scheduled Replenishment Place Order feature.
 *
 * This array includes the CMS configuration for the CheckoutPlaceOrder CMS component,
 * specifying the component and required guards for route protection.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayPlaceOrder()`.
 * 2. The configuration ensures the correct component and guards are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_CHECKOUT_PLACE_ORDER_FEATURE_PROVIDERS: Provider[] = [
  provideConfig({
    cmsComponents: {
      CheckoutPlaceOrder: {
        component: WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent,
        guards: [CheckoutAuthGuard, CartNotEmptyGuard],
      },
    },
  } as CmsConfig)
];

/**
 * Factory function to provide all Worldpay Checkout Scheduled Replenishment Place Order feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay Checkout Scheduled Replenishment Place Order feature
 * @since 221121.11.0
 */
export function provideWorldpayPlaceOrder(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_CHECKOUT_PLACE_ORDER_FEATURE_PROVIDERS);
}