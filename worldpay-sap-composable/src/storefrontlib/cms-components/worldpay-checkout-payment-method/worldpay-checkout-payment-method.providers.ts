import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayCheckoutPaymentRedirectFailureGuard } from '../../../core';
import { WorldpayCheckoutPaymentMethodComponent } from './worldpay-checkout-payment-method.component';

/**
 * Providers for the Worldpay Checkout Payment Method feature.
 *
 * This array includes the CMS configuration for the CheckoutPaymentDetails CMS component,
 * specifying the component and required guards for route protection.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayCheckoutPaymentMethod()`.
 * 2. The configuration ensures the correct component and guards are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_CHECKOUT_PAYMENT_METHOD_FEATURE_PROVIDERS: Provider[] = [
  provideConfig({
    cmsComponents: {
      CheckoutPaymentDetails: {
        component: WorldpayCheckoutPaymentMethodComponent,
        guards: [
          CheckoutAuthGuard,
          CartNotEmptyGuard,
          WorldpayCheckoutPaymentRedirectFailureGuard,
        ],
      },
    }
  } as CmsConfig)
];

/**
 * Factory function to provide all Worldpay Checkout Payment Method feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay Checkout Payment Method feature
 * @since 221121.11.0
 */
export function provideWorldpayCheckoutPaymentMethod(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_CHECKOUT_PAYMENT_METHOD_FEATURE_PROVIDERS);
}