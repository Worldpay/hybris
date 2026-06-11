import { NgModule } from '@angular/core';
import { WorldpayBillingAddressComponent } from './worldpay-billing-address.component';

/**
 * Angular module for the Worldpay Billing Address feature.
 *
 * Since version 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayBillingAddressComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) if needed.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayBillingAddressComponent` is now a standalone component.
 */
@NgModule({
  exports: [WorldpayBillingAddressComponent],
  imports: [WorldpayBillingAddressComponent]
})
export class WorldpayBillingAddressModule {
}
