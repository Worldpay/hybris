import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CartNotEmptyGuard, CheckoutAuthGuard, } from '@spartacus/checkout/base/components';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayB2BCheckoutReviewSubmitComponent } from './worldpay-b2b-checkout-review-submit.component';

/**
 * Providers for the Worldpay B2B Checkout Review Submit feature.
 *
 * This array includes the CMS configuration for the CheckoutReviewOrder component,
 * specifying the component and required guards for route protection.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayB2BCheckoutReviewSubmit()`.
 * 2. The configuration ensures the correct component and guards are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_B2B_CHECKOUT_REVIEW_SUBMIT_PROVIDERS: Provider[] = [
  provideConfig({
    cmsComponents: {
      CheckoutReviewOrder: {
        component: WorldpayB2BCheckoutReviewSubmitComponent,
        guards: [CheckoutAuthGuard, CartNotEmptyGuard],
      },
    },
  } as CmsConfig)
];

/**
 * Factory function to provide all Worldpay B2B Checkout Review Submit feature providers.
 *
 * @returns EnvironmentProviders for the Worldpay B2B Checkout Review Submit feature
 * @since 221121.11.0
 */
export function provideWorldpayB2BCheckoutReviewSubmit(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_B2B_CHECKOUT_REVIEW_SUBMIT_PROVIDERS);
}