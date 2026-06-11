import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { AuthGuard, CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayOrderOverviewComponent } from './worldpay-order-overview/worldpay-order-overview.component';

/**
 * Providers for the Worldpay Order Details feature.
 *
 * This array includes the CMS configuration for the AccountOrderDetailsOverviewComponent and
 * AccountOrderDetailsSimpleOverviewComponent, specifying the component and required guards for route protection.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayOrderDetails()`.
 * 2. The configuration ensures the correct component and guards are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_ORDER_DETAILS_FEATURE_PROVIDERS: Provider[] = [
  provideConfig({
    cmsComponents: {
      AccountOrderDetailsOverviewComponent: {
        component: WorldpayOrderOverviewComponent,
        guards: [AuthGuard],
      },
      AccountOrderDetailsSimpleOverviewComponent: {
        component: WorldpayOrderOverviewComponent,
        guards: [AuthGuard],
        data: {
          simple: true,
        },
      }
    },
  } as CmsConfig)
];

/**
 * Factory function to provide all Worldpay Order Details feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay Order Details feature
 * @since 221121.11.0
 */
export function provideWorldpayOrderDetails(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_ORDER_DETAILS_FEATURE_PROVIDERS);
}