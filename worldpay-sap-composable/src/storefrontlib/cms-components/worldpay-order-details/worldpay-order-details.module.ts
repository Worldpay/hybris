import { NgModule } from '@angular/core';
import { WORLDPAY_ORDER_DETAILS_FEATURE_PROVIDERS } from './worldpay-order-details.providers';
import { WorldpayOrderOverviewComponent } from './worldpay-order-overview/worldpay-order-overview.component';

/**
 * Angular module for the Worldpay Order Details feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayOrderOverviewComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayOrderDetails()`.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * CMS configuration and exports the standalone components.
 *
 * @since 221121.11.0
 * - `WorldpayOrderOverviewComponent` is now standalone components.
 * - All feature-specific providers and CMS mappings have been moved to `provideWorldpayOrderDetails()`.
 */
@NgModule({
  imports: [WorldpayOrderOverviewComponent,],
  exports: [WorldpayOrderOverviewComponent,],
  providers: WORLDPAY_ORDER_DETAILS_FEATURE_PROVIDERS
})
export class WorldpayOrderDetailsModule {
}
