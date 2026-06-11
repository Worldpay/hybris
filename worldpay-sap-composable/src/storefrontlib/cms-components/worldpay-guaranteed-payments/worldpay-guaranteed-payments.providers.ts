import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { worldpayGuaranteedPaymentsAdapterProvider, worldpayGuaranteedPaymentsConnectorProvider, worldpayGuaranteedPaymentsFacadeProviders } from '../../../core';

/**
 * Providers for the Worldpay Guaranteed Payments feature.
 *
 * This array includes all necessary providers for connectors, adapters, and facades
 * related to the Guaranteed Payments functionality. Register these providers in your
 * `app.config.ts` (or equivalent) using `provideWorldpayGuaranteedPayments()` to enable
 * all required dependencies for the feature.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayGuaranteedPayments()`.
 * 2. The configuration ensures all connectors, adapters, and facades are available for the feature.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_PROVIDERS: Provider[] = [
  ...worldpayGuaranteedPaymentsAdapterProvider,
  ...worldpayGuaranteedPaymentsConnectorProvider,
  ...worldpayGuaranteedPaymentsFacadeProviders,
];

/**
 * Factory function to provide all Worldpay Guaranteed Payments feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay Guaranteed Payments feature
 * @since 221121.11.0
 */
export function provideWorldpayGuaranteedPayments(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_PROVIDERS);
}