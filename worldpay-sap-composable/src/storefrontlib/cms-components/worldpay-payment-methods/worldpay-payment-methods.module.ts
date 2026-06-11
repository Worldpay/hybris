import { NgModule } from '@angular/core';
import { WorldpayPaymentMethodsComponent } from './worldpay-payment-methods.component';
import { WORLDPAY_PAYMENT_METHODS_FEATURE_PROVIDERS } from './worldpay-payment-methods.providers';

/**
 * Angular module for the Worldpay Payment Methods feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayPaymentMethodsComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayPaymentMethods()`.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * CMS configuration and exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayPaymentMethodsComponent` is now a standalone component.
 * - All feature-specific providers and CMS mappings have been moved to `provideWorldpayPaymentMethods()`.
 */
@NgModule({
  imports: [WorldpayPaymentMethodsComponent],
  exports: [WorldpayPaymentMethodsComponent],
  providers: WORLDPAY_PAYMENT_METHODS_FEATURE_PROVIDERS
})
export class WorldpayPaymentMethodsModule {
}
