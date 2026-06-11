import { NgModule } from '@angular/core';
import { WorldpayCheckoutPaymentFormComponent } from './worldpay-checkout-payment-form.component';

/**
 * Angular module for the Worldpay Checkout Payment Form feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayCheckoutPaymentFormComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) if needed.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * configuration and exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayCheckoutPaymentFormComponent` is now a standalone component.
 */
@NgModule({
  exports: [WorldpayCheckoutPaymentFormComponent],
  imports: [WorldpayCheckoutPaymentFormComponent]
})
export class WorldpayPaymentFormModule {
}
