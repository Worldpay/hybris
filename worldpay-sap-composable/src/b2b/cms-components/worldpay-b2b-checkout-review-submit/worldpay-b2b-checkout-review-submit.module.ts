import { NgModule } from '@angular/core';
import { WorldpayB2BCheckoutReviewSubmitComponent } from './worldpay-b2b-checkout-review-submit.component';
import { WORLDPAY_B2B_CHECKOUT_REVIEW_SUBMIT_PROVIDERS } from './worldpay-b2b-checkout-review-submit.providers';

/**
 * Angular module for the Worldpay B2B Checkout Review Submit feature.
 *
 * This module enables integration of the WorldpayB2BCheckoutReviewSubmitComponent and its providers
 * for B2B checkout review and submit functionality. For new implementations, it is recommended to use
 * the standalone WorldpayB2BCheckoutReviewSubmitComponent and register providers via `provideWorldpayB2BCheckoutReviewSubmit()`
 * in your app configuration.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayB2BCheckoutReviewSubmitComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayB2BCheckoutReviewSubmit()`.
 *
 * ### Module Usage:
 * Import this module to automatically register the required providers for the CheckoutReviewOrder
 * CMS component and export the standalone component.
 *
 * @since 221121.11.0
 */
@NgModule({
  imports: [WorldpayB2BCheckoutReviewSubmitComponent],
  exports: [WorldpayB2BCheckoutReviewSubmitComponent],
  providers: WORLDPAY_B2B_CHECKOUT_REVIEW_SUBMIT_PROVIDERS
})
export class WorldpayB2BCheckoutReviewSubmitModule {
}
