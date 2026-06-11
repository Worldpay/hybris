import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { OccConfig, provideConfig } from '@spartacus/core';
import { worldpayB2BOccConfig } from '../core';

/**
 * Providers for the Worldpay B2B OCC feature.
 *
 * This array registers OCC configuration required by Worldpay B2B integrations.
 *
 * ### Usage:
 * Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayB2BOcc()`.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_B2B_OCC_PROVIDERS: Provider[] = [
  provideConfig(worldpayB2BOccConfig as OccConfig)
];

/**
 * Factory function to provide the Worldpay B2B OCC feature.
 *
 * Use this function to register OCC configuration globally for Worldpay B2B features.
 *
 * @returns EnvironmentProviders for the Worldpay B2B OCC feature
 * @since 221121.11.0
 */
export function worldpayB2BOccConfigProvider(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_B2B_OCC_PROVIDERS);
}