import { EnvironmentProviders, importProvidersFrom, makeEnvironmentProviders } from '@angular/core';
import { WorldpayComponentsModule } from '../cms-components';

/**
 * Provides all Worldpay storefront feature modules as environment providers.
 *
 * This function aggregates all relevant Worldpay feature modules (standalone components and pages)
 * and registers them globally for the application. Use this provider in your root module or
 * application configuration to enable all Worldpay storefront features.
 *
 * Included modules:
 * - WorldpayCheckoutPaymentMethodModule
 * - WorldpayCheckoutDeliveryAddressModule
 * - WorldpayDdcIframeModule
 * - WorldpayDdcIframeRoutingModule
 * - Worldpay3dsChallengeIframeModule
 * - WorldpayCheckoutPlaceOrderModule
 * - WorldpayCheckoutReviewPaymentModule
 * - WorldpayCartSharedModule
 * - WorldpayOrderConfirmationModule
 * - WorldpayOrderDetailsModule
 * - WorldpayEventsModule
 * - WorldpayPaymentMethodsModule
 *
 * ### Usage:
 * 1. Register this provider in your root module or application configuration using `provideWorldpayStorefront()`.
 * 2. The configuration ensures all Worldpay storefront modules are available globally.
 *
 * @returns EnvironmentProviders for all Worldpay storefront modules
 * @since 221121.11.0
 */
export function provideWorldpayComponents(): EnvironmentProviders {
  return makeEnvironmentProviders([
    importProvidersFrom(WorldpayComponentsModule),
  ]);
}