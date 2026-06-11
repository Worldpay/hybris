import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { worldpayFraudsightAdapterProvider, worldpayFraudsightConnectorProviders, worldpayFraudsightFacadeProviders } from '../../../core';

/**
 * Providers for the Worldpay Fraudsight Risk feature.
 *
 * This array includes all necessary providers for connectors, adapters, and facades
 * related to the Fraudsight Risk functionality. Register these providers in your
 * `app.config.ts` (or equivalent) using `provideWorldpayFraudSight()` to enable
 * all required dependencies for the feature.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayFraudSight()`.
 * 2. The configuration ensures all connectors, adapters, and facades are available for the feature.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_FRAUDSIGHT_FEATURE_PROVIDERS: Provider[] = [
  ...worldpayFraudsightConnectorProviders,
  ...worldpayFraudsightAdapterProvider,
  ...worldpayFraudsightFacadeProviders,
];

/**
 * Factory function to provide all Worldpay Fraudsight Risk feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay Fraudsight Risk feature
 * @since 221121.11.0
 */
export function provideWorldpayFraudSight(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_FRAUDSIGHT_FEATURE_PROVIDERS);
}