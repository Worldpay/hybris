import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CartNotEmptyGuard, CheckoutAuthGuard, } from '@spartacus/checkout/base/components';
import { CmsConfig, I18nModule, provideConfig, UrlModule, } from '@spartacus/core';
import { AtMessageModule } from '@spartacus/storefront';
import { WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent } from './worldpay-checkout-place-order.component';

@NgModule({
  imports: [
    AtMessageModule,
    CommonModule,
    RouterModule,
    UrlModule,
    I18nModule,
    ReactiveFormsModule,
  ],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        CheckoutPlaceOrder: {
          component: WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent,
          guards: [CheckoutAuthGuard, CartNotEmptyGuard],
        },
      },
    }),
  ],
  declarations: [WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent],
  exports: [WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent],
})
export class WorldpayCheckoutScheduledReplenishmentPlaceOrderModule {
}
