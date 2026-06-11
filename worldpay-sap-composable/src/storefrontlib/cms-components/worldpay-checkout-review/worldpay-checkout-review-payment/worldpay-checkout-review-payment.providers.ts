import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayCheckoutReviewPaymentGuard } from '../../../../core';
import { WorldpayCheckoutReviewPaymentComponent } from './worldpay-checkout-review-payment.component';

/**
 * Providers for the Worldpay Checkout Review Payment feature.
 *
 * This array includes the CMS configuration for the CheckoutReviewPayment CMS component,
 * specifying the component and required guards for route protection.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayCheckoutReviewPayment()`.
 * 2. The configuration ensures the correct component and guards are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_CHECKOUT_REVIEW_PAYMENT_FEATURE_PROVIDERS: Provider[] = [
  provideConfig({
    cmsComponents: {
      CheckoutReviewPayment: {
        component: WorldpayCheckoutReviewPaymentComponent,
        guards: [CheckoutAuthGuard, CartNotEmptyGuard, WorldpayCheckoutReviewPaymentGuard],
      },
    },
  } as CmsConfig)
];

/**
 * Factory function to provide all Worldpay Checkout Review Payment feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay Checkout Review Payment feature
 * @since 221121.11.0
 */
export function provideWorldpayCheckoutReviewPayment(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_CHECKOUT_REVIEW_PAYMENT_FEATURE_PROVIDERS);
}