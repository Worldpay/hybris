import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, I18nModule, provideConfig, UrlModule } from '@spartacus/core';
import { CardModule, IconModule } from '@spartacus/storefront';
import { WorldpayCheckoutReviewPaymentGuard } from '../../../../core';
import { WorldpayCheckoutReviewPaymentComponent } from './worldpay-checkout-review-payment.component';

@NgModule({
  declarations: [
    WorldpayCheckoutReviewPaymentComponent
  ],
  exports: [
    WorldpayCheckoutReviewPaymentComponent
  ],
  imports: [
    CommonModule,
    CardModule,
    I18nModule,
    UrlModule,
    RouterModule,
    IconModule,
  ],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        CheckoutReviewPayment: {
          component: WorldpayCheckoutReviewPaymentComponent,
          guards: [CheckoutAuthGuard, CartNotEmptyGuard, WorldpayCheckoutReviewPaymentGuard],
        },
      },
    }),
  ],
})
export class WorldpayCheckoutReviewPaymentModule {
}
