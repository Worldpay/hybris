import { NgModule } from '@angular/core';
import { WorldpayFeatureModule } from '../features';
import { WorldpayCheckoutScheduledReplenishmentModule } from '../scheduled-replenishment';
import { WorldpayB2bCheckoutPaymentMethodModule, WorldpayB2BCheckoutReviewSubmitModule } from './cms-components';
import { WorldpayB2BCheckoutDeliveryAddressModule } from './cms-components/worldpay-b2b-checkout-delivery-address/worldpay-b2b-checkout-delivery-address.module';
import { OccWorldpayB2bModule } from './core';
import { worldpayB2bConnectorsProviders } from './core/connectors/worldpay-b2b-connectors.providers';
import { worldpayB2bFacadesProviders } from './core/facade/worldpay-b2b-facades.providers';

/**
 * Angular module for the Worldpay B2B feature.
 *
 * This module enables integration of all B2B-specific Worldpay providers for advanced business-to-business payment flows.
 * For new implementations, it is recommended to use the standalone provider approach and register providers via `worldpayB2BProvider()`
 * in your app configuration.
 *
 * ### Standalone Usage:
 * 1. Register providers in your `app.config.ts` (or equivalent) using `worldpayB2BProvider()`.
 *
 * ### Module Usage:
 * Import this module to automatically register the required providers for B2B payment flows.
 *
 * @since 221121.11.0
 * - All B2B-specific providers are aggregated in `worldpayB2BProvider()`.
 */
@NgModule({
  imports: [
    WorldpayFeatureModule,
    OccWorldpayB2bModule,
    WorldpayB2bCheckoutPaymentMethodModule,
    WorldpayB2BCheckoutReviewSubmitModule,
    WorldpayCheckoutScheduledReplenishmentModule,
    WorldpayB2BCheckoutDeliveryAddressModule
  ],
  providers: [
    ...worldpayB2bConnectorsProviders(),
    ...worldpayB2bFacadesProviders
  ]
})

export class WorldpayB2bFeatureModule {
}
