import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayCheckoutPaymentRedirectFailureGuard } from '../../../core';
import { WorldpayB2bCheckoutPaymentMethodComponent } from './worldpay-b2b-checkout-payment-method.component';

/**
 * Providers for the Worldpay B2B Checkout Payment Method feature.
 *
 * This array includes the CMS configuration for the CheckoutPaymentDetails component,
 * specifying the component and required guards for route protection in B2B checkout flows.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayB2BCheckoutPaymentMethod()`.
 * 2. The configuration ensures the correct component and guards are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_B2B_CHECKOUT_PAYMENT_METHOD_PROVIDERS: Provider[] = [
  provideConfig({
    cmsComponents: {
      CheckoutPaymentDetails: {
        component: WorldpayB2bCheckoutPaymentMethodComponent,
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
 * Factory function to provide the Worldpay B2B Checkout Payment Method feature.
 *
 * Use this function to register all required providers for the B2B checkout payment method feature globally.
 *
 * @returns EnvironmentProviders for the Worldpay B2B Checkout Payment Method feature
 * @since 221121.11.0
 */
export function provideWorldpayB2BCheckoutPaymentMethod(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_B2B_CHECKOUT_PAYMENT_METHOD_PROVIDERS);
}