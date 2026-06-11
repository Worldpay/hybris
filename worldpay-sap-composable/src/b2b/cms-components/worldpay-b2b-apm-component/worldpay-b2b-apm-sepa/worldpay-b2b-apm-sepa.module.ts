import { NgModule } from '@angular/core';
import { WorldpayB2BApmSepaComponent } from './worldpay-b2b-apm-sepa.component';
import { WORLDPAY_B2B_APM_SEPA_PROVIDERS } from './worldpay-b2b-apm-sepa.providers';

/**
 * Angular module for the Worldpay B2B APM SEPA feature.
 *
 * This module enables integration of the WorldpayB2BApmSepaComponent and its providers
 * for B2B SEPA APM functionality. For new implementations, it is recommended to use
 * the standalone WorldpayB2BApmSepaComponent and register providers via `provideWorldpayB2BApmSepa()`
 * in your app configuration.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayB2BApmSepaComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayB2BApmSepa()`.
 *
 * ### Module Usage:
 * Import this module to automatically register the required providers for the WorldpayAPMComponent
 * SEPA variant and export the standalone component.
 *
 * @since 221121.11.0
 */
@NgModule({
  exports: [WorldpayB2BApmSepaComponent],
  imports: [WorldpayB2BApmSepaComponent],
  providers: WORLDPAY_B2B_APM_SEPA_PROVIDERS
})
export class WorldpayB2bApmSepaModule {
}
