import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayB2BApmSepaComponent } from './worldpay-b2b-apm-sepa.component';

/**
 * Providers for the Worldpay B2B APM SEPA feature.
 *
 * This array includes the CMS configuration for the WorldpayAPMComponent SEPA variant,
 * specifying the component and required guards for route protection.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_B2B_APM_SEPA_PROVIDERS: Provider[] = [
  provideConfig({
    cmsComponents: {
      WorldpayAPMComponent: {
        component: WorldpayB2BApmSepaComponent,
        guards: [CheckoutAuthGuard, CartNotEmptyGuard]
      },
    }
  } as CmsConfig)
];

/**
 * Factory function to provide the Worldpay B2B APM SEPA feature.
 *
 * Use this function to register all required providers for the B2B SEPA APM feature globally.
 *
 * ### Usage:
 * Register this provider in your `app.config.ts` (or equivalent) using `provideWorldpayB2BApmSepa()`.
 *
 * @returns EnvironmentProviders for the Worldpay B2B APM SEPA feature
 * @since 221121.11.0
 */
export function provideWorldpayB2BApmSepa(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_B2B_APM_SEPA_PROVIDERS);
}
