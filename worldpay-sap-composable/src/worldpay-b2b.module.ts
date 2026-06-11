import { NgModule } from '@angular/core';
import { WorldpayB2bFeatureModule } from './b2b';
import { WorldpayGuaranteedPaymentsFeatureModule } from './features';

/**
 * Root Angular module that bundles Worldpay features for B2B storefronts.
 * This module is intended to be imported at application level to enable:
 * - B2B-specific Worldpay flows and CMS integrations (`WorldpayB2bFeatureModule`)
 * - Guaranteed Payments support (`WorldpayGuaranteedPaymentsFeatureModule`)
 *
 * @since 221121.11.0
 */
@NgModule({
  imports: [
    WorldpayB2bFeatureModule,
    WorldpayGuaranteedPaymentsFeatureModule
  ],
})

export class WorldpayB2bModule {
}
