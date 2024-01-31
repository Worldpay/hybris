import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayCheckoutReviewPaymentComponent } from './worldpay-checkout-review-payment.component';
import { CardModule, IconModule } from '@spartacus/storefront';
import { CmsConfig, I18nModule, provideConfig, UrlModule } from '@spartacus/core';
import { RouterModule } from '@angular/router';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { WorldpayCheckoutReviewPaymentGuard } from '../../../../core/guards/worldpay-checkout-review-payment.guard';

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
