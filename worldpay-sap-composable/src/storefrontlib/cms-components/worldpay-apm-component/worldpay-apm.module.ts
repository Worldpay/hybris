import { NgModule } from '@angular/core';
import { WorldpayApmComponent } from './worldpay-apm.component';
import { WORLDPAY_APM_FEATURE_PROVIDERS } from './worldpay-apm.providers';

/**
 * Angular module for the Worldpay APM (Alternative Payment Methods) feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayApmComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayApm()`.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * CMS configuration and exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayApmComponent` is now a standalone component.
 * - All feature-specific providers and CMS mappings have been moved to `provideWorldpayApm()`.
 */
@NgModule({
  exports: [WorldpayApmComponent],
  imports: [WorldpayApmComponent],
  providers: WORLDPAY_APM_FEATURE_PROVIDERS
})
export class WorldpayApmModule {
}
