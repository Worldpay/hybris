import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayB2bApmComponent } from './worldpay-b2b-apm.component';

/**
 * Providers for the Worldpay B2B APM feature.
 *
 * This array includes the CMS configuration for the WorldpayAPMComponent,
 * specifying the component and required guards for route protection.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayB2BApm()`.
 * 2. The configuration ensures the correct component and guards are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_B2B_APM_PROVIDERS: Provider[] = [
  provideConfig({
    cmsComponents: {
      WorldpayAPMComponent: {
        component: WorldpayB2bApmComponent,
        guards: [CheckoutAuthGuard, CartNotEmptyGuard]
      },
    }
  } as CmsConfig)
];

/**
 * Factory function to provide the Worldpay B2B APM feature.
 *
 * @returns EnvironmentProviders for the Worldpay B2B APM feature
 * @since 221121.11.0
 */
export function provideWorldpayB2BApm(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_B2B_APM_PROVIDERS);
}
