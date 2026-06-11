import { NgModule } from '@angular/core';
import { WorldpayApmGooglepayComponent } from './worldpay-apm-googlepay.component';

/**
 * Angular module for the Worldpay APM Google Pay feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayApmGooglepayComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) if needed.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayApmGooglepayComponent` is now a standalone component.
 */
@NgModule({
  exports: [WorldpayApmGooglepayComponent],
  imports: [WorldpayApmGooglepayComponent]
})
export class WorldpayApmGooglepayModule {
}
