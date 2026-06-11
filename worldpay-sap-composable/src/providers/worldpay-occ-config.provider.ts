import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { OccConfig, provideConfig } from '@spartacus/core';
import { worldpayOccConfig } from '../core';

/**
 * Providers for the Worldpay Occ Config.
 * @since 221121.11.0
 */
export const worldpayOccConfigProvider: () => Provider[] = (): Provider[] => ([
  provideConfig(worldpayOccConfig as OccConfig)
]);

/**
 * Provides the OCC configuration for Worldpay as environment providers.
 *
 * This function registers the OCC configuration required for Worldpay features.
 * Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayOccConfig()`
 * to enable the OCC endpoints and configuration for Worldpay integration.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayOccConfig()`.
 * 2. The configuration ensures the OCC endpoints are available for the Worldpay features.
 *
 * @since 221121.11.0
 */
export function provideWorldpayOccConfig(): EnvironmentProviders {
  return makeEnvironmentProviders(worldpayOccConfigProvider());
}