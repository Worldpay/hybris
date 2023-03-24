import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsConfig, I18nModule, provideConfig } from '@spartacus/core';
import { OrderConfirmationItemsComponent, OrderConfirmationThankYouMessageComponent, OrderConfirmationTotalsComponent } from '@spartacus/checkout/components';
import { WorldpayOrderConfirmationItemsComponent } from './components/worldpay-order-confirmation-items/worldpay-order-confirmation-items.component';
import { CartComponentModule, PromotionsModule } from '@spartacus/storefront';
import { WorldpayCartSharedModule } from '../worldpay-cart-shared/worldpay-cart-shared.module';
import { WorldpayOrderConfirmationOverviewComponent } from './components/worldpay-order-confirmation-overview/worldpay-order-confirmation-overview.component';
import { WorldpayOrderOverviewModule } from './components/worldpay-order-overview/worldpay-order-overview.module';
import { WorldpayCheckoutPaymentRedirectGuard } from '../../../core/guards/worldpay-checkout-payment-redirect.guard';

@NgModule({

  declarations: [
    WorldpayOrderConfirmationItemsComponent,
    WorldpayOrderConfirmationOverviewComponent,
  ],
  imports: [
    CommonModule,
    CartComponentModule,
    PromotionsModule,
    I18nModule,
    WorldpayCartSharedModule,
    WorldpayOrderOverviewModule,
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
          component: WorldpayOrderConfirmationOverviewComponent,
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },
      }
    } as CmsConfig),
  ],
})
export class WorldpayOrderConfirmationModule {
}
