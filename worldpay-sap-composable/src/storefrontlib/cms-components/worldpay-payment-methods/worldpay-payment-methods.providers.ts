import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { AuthGuard, CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayPaymentMethodsComponent } from './worldpay-payment-methods.component';

/**
 * Providers for the Worldpay Payment Methods feature.
 *
 * This array includes the CMS configuration for the AccountPaymentDetailsComponent,
 * specifying the component and required guards for route protection.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayPaymentMethods()`.
 * 2. The configuration ensures the correct component and guards are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_PAYMENT_METHODS_FEATURE_PROVIDERS: Provider[] = [
  provideConfig({
    cmsComponents: {
      AccountPaymentDetailsComponent: {
        component: WorldpayPaymentMethodsComponent,
        guards: [AuthGuard],
      },
    },
  } as CmsConfig)
];

/**
 * Factory function to provide all Worldpay Payment Methods feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay Payment Methods feature
 * @since 221121.11.0
 */
export function provideWorldpayPaymentMethods(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_PAYMENT_METHODS_FEATURE_PROVIDERS);
}