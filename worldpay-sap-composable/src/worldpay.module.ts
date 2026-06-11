import { NgModule } from '@angular/core';
import { WorldpayFeatureModule, WorldpayGuaranteedPaymentsFeatureModule } from './features';

/**
 *  Root module that bundles all Worldpay capabilities for storefront integration. *
 *  This module is intended to be imported once at the application level and provides:
 * - Core Worldpay feature setup (`WorldpayFeatureModule`)
 * - Guaranteed Payments feature enablement (`WorldpayGuaranteedPaymentsFeatureModule`)
 *
 * @since 221121.11.0
 */
@NgModule({
  imports: [
    WorldpayFeatureModule,
    WorldpayGuaranteedPaymentsFeatureModule
  ],
})
export class WorldpayModule {
}
