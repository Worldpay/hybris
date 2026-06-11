import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import {
  WORLDPAY_B2B_APM_PROVIDERS,
  WORLDPAY_B2B_APM_SEPA_PROVIDERS,
  WORLDPAY_B2B_CHECKOUT_PAYMENT_METHOD_PROVIDERS,
  WORLDPAY_B2B_CHECKOUT_REVIEW_SUBMIT_PROVIDERS
} from '../cms-components';
import { worldpayB2bConnectorsProviders } from '../core/connectors/worldpay-b2b-connectors.providers';
import { worldpayB2bFacadesProviders } from '../core/facade/worldpay-b2b-facades.providers';
import { WORLDPAY_B2B_OCC_PROVIDERS } from './worldpay-b2b-occ-config.provider';

/**
 * Factory function to provide all Worldpay B2B features.
 *
 * This function aggregates all necessary providers for B2B-specific Worldpay features, including:
 * - OCC configuration
 * - APM (Alternative Payment Methods)
 * - SEPA APM variant
 * - Checkout Payment Method
 * - Checkout Review Submit
 *
 * Register this provider in your `app.config.ts` (or equivalent) using `provideWorldpayB2B()`
 * to enable all required dependencies for business-to-business payment flows.
 *
 * @returns EnvironmentProviders for all Worldpay B2B features
 * @since 221121.11.0
 */
export function provideWorldpayB2B(): EnvironmentProviders {
  const providers: Provider[] = [
    ...WORLDPAY_B2B_OCC_PROVIDERS,
    ...WORLDPAY_B2B_APM_PROVIDERS,
    ...WORLDPAY_B2B_APM_SEPA_PROVIDERS,
    ...WORLDPAY_B2B_CHECKOUT_PAYMENT_METHOD_PROVIDERS,
    ...WORLDPAY_B2B_CHECKOUT_REVIEW_SUBMIT_PROVIDERS,
    ...worldpayB2bConnectorsProviders(),
    ...worldpayB2bFacadesProviders
  ];

  return makeEnvironmentProviders(providers);
}