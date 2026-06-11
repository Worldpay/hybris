import { NgModule } from '@angular/core';
import { WorldpayB2bApmComponent } from './worldpay-b2b-apm.component';
import { WORLDPAY_B2B_APM_PROVIDERS } from './worldpay-b2b-apm.providers';

/**
 * Angular module for the Worldpay B2B APM feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayB2bApmComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayB2BApm()`.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * CMS configuration for the WorldpayAPMComponent and exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayB2bApmComponent` is now a standalone component.
 * - All feature-specific providers and CMS mappings are available via `provideWorldpayB2BApm()`.
 */
@NgModule({
  exports: [WorldpayB2bApmComponent],
  imports: [WorldpayB2bApmComponent],
  providers: WORLDPAY_B2B_APM_PROVIDERS
})
export class WorldpayB2bApmModule {
}
