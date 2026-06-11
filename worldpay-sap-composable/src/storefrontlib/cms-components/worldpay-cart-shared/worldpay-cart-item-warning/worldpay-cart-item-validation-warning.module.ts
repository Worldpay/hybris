import { NgModule } from '@angular/core';
import { WorldpayCartItemValidationWarningComponent } from './worldpay-cart-item-validation-warning.component';

/**
 * Angular module for the Worldpay Cart Item Validation Warning feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayCartItemValidationWarningComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) if needed.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * configuration and exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayCartItemValidationWarningComponent` is now a standalone component.
 */
@NgModule({
  imports: [WorldpayCartItemValidationWarningComponent],
  exports: [WorldpayCartItemValidationWarningComponent],
})
export class WorldpayCartItemValidationWarningModule {
}
