import { NgModule } from '@angular/core';
import { WorldpayCheckoutReviewPaymentComponent } from './worldpay-checkout-review-payment.component';
import { WORLDPAY_CHECKOUT_REVIEW_PAYMENT_FEATURE_PROVIDERS } from './worldpay-checkout-review-payment.providers';

/**
 * Angular module for the Worldpay Checkout Review Payment feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayCheckoutReviewPaymentComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayCheckoutReviewPayment()`.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * CMS configuration and exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayCheckoutReviewPaymentComponent` is now a standalone component.
 * - All feature-specific providers and CMS mappings have been moved to `provideWorldpayCheckoutReviewPayment()`.
 */
@NgModule({
  exports: [WorldpayCheckoutReviewPaymentComponent],
  imports: [WorldpayCheckoutReviewPaymentComponent],
  providers: WORLDPAY_CHECKOUT_REVIEW_PAYMENT_FEATURE_PROVIDERS,
})
export class WorldpayCheckoutReviewPaymentModule {
}
