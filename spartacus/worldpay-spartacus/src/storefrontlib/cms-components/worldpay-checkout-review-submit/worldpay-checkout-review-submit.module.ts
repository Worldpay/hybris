import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayCheckoutReviewSubmitComponent } from './worldpay-checkout-review-submit.component';
import { I18nModule, provideConfig, UrlModule } from '@spartacus/core';
import { CardModule, CartSharedModule, IconModule, PromotionsModule } from '@spartacus/storefront';
import { RouterModule } from '@angular/router';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/components';

@NgModule({
  declarations: [WorldpayCheckoutReviewSubmitComponent],
  imports: [
    CommonModule,
    CardModule,
    CartSharedModule,
    I18nModule,
    UrlModule,
    RouterModule,
    PromotionsModule,
    IconModule,
  ],
  providers: [
    provideConfig({
      cmsComponents: {
        CheckoutReviewOrder: {
          component: WorldpayCheckoutReviewSubmitComponent,
          guards: [
            CheckoutAuthGuard,
            CartNotEmptyGuard
          ],
        },
      },
    }),
  ]
})
export class WorldpayCheckoutReviewSubmitModule {
}
