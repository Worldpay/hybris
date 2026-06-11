import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayApmComponent } from './worldpay-apm.component';

/**
 * Providers for the Worldpay APM (Alternative Payment Methods) feature.
 *
 * This array includes the CMS configuration for the WorldpayAPMComponent CMS component,
 * specifying the component and required guards for route protection.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayApm()`.
 * 2. The configuration ensures the correct component and guards are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_APM_FEATURE_PROVIDERS: Provider[] = [
  provideConfig({
    cmsComponents: {
      WorldpayAPMComponent: {
        component: WorldpayApmComponent,
        guards: [CheckoutAuthGuard, CartNotEmptyGuard]
      },
    }
  } as CmsConfig)
];

/**
 * Factory function to provide all Worldpay APM feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay APM feature
 * @since 221121.11.0
 */
export function provideWorldpayApm(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_APM_FEATURE_PROVIDERS);
}