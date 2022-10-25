import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, I18nModule, provideConfig } from '@spartacus/core';
import {
  OrderConfirmationItemsComponent,
  OrderConfirmationOverviewComponent,
  OrderConfirmationThankYouMessageComponent,
  OrderConfirmationTotalsComponent
} from '@spartacus/checkout/components';
import { WorldpayCheckoutPaymentRedirectGuard } from '../../../core/guards/worldpay-checkout-payment-redirect.guard';
import { WorldpayOrderConfirmationItemsComponent } from './components/worldpay-order-confirmation-items/worldpay-order-confirmation-items.component';
import { CartComponentModule, PromotionsModule } from '@spartacus/storefront';
import { WorldpayCartSharedModule } from '../worldpay-cart-shared/worldpay-cart-shared.module';

@NgModule({

  declarations: [
    WorldpayOrderConfirmationItemsComponent,
  ],
  imports: [
    CommonModule,
    CartComponentModule,
    PromotionsModule,
    I18nModule,
    WorldpayCartSharedModule,
  ],
  providers: [
    provideConfig({
      cmsComponents: {
        OrderConfirmationThankMessageComponent: {
          component: OrderConfirmationThankYouMessageComponent,
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },
        OrderConfirmationItemsComponent: {
          component: OrderConfirmationItemsComponent,
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },
        OrderConfirmationTotalsComponent: {
          component: OrderConfirmationTotalsComponent,
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },
        OrderConfirmationOverviewComponent: {
          component: OrderConfirmationOverviewComponent,
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },
      }
    } as CmsConfig),
  ],
})
export class WorldpayOrderConfirmationModule {
}
