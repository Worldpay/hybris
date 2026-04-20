import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CartNotEmptyGuard, CheckoutAuthGuard, } from '@spartacus/checkout/base/components';
import { CmsConfig, I18nModule, provideConfig, UrlModule, } from '@spartacus/core';
import { CardModule, IconModule, OutletModule, PromotionsModule, } from '@spartacus/storefront';
import { WorldpayB2BCheckoutReviewSubmitComponent } from './worldpay-b2b-checkout-review-submit.component';

@NgModule({
  imports: [
    CommonModule,
    CardModule,
    I18nModule,
    UrlModule,
    RouterModule,
    PromotionsModule,
    IconModule,
    OutletModule,
  ],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        CheckoutReviewOrder: {
          component: WorldpayB2BCheckoutReviewSubmitComponent,
          guards: [CheckoutAuthGuard, CartNotEmptyGuard],
        },
      },
    }),
  ],
  declarations: [WorldpayB2BCheckoutReviewSubmitComponent],
  exports: [WorldpayB2BCheckoutReviewSubmitComponent],
})
export class WorldpayB2BCheckoutReviewSubmitModule {
}
