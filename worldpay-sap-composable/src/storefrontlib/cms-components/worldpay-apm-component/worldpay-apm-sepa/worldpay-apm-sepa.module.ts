import { NgModule } from '@angular/core';
import { WorldpayApmSepaComponent } from './worldpay-apm-sepa.component';

/**
 * Angular module for the Worldpay APM SEPA feature.
 *
 * since 221121.11, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayApmSepaComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) if needed.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayApmSepaComponent` is now a standalone component.
 */
@NgModule({
  exports: [WorldpayApmSepaComponent],
  imports: [WorldpayApmSepaComponent]
})
export class WorldpayApmSepaModule {
}
