import { NgModule } from '@angular/core';
import { WORLDPAY_B2B_OCC_PROVIDERS } from '../../providers/worldpay-b2b-occ-config.provider';

/**
 * Angular module for the Worldpay B2B OCC integration feature.
 *
 * This module enables integration of OCC-specific configuration and providers for advanced business-to-business payment flows.
 * For new implementations, it is recommended to use the standalone provider approach and register providers via `provideWorldpayB2BOcc()`
 * in your app configuration.
 *
 * ### Standalone Usage:
 * 1. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayB2BOcc()`.
 *
 * ### Module Usage:
 * Import this module to automatically register the required OCC providers for B2B payment flows.
 *
 * @since 221121.11.0
 * - All OCC-specific providers are aggregated in `provideWorldpayB2BOcc()`.
 */
@NgModule({
  providers: WORLDPAY_B2B_OCC_PROVIDERS
})
export class OccWorldpayB2bModule {
}
