import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthGuard, CmsConfig, FeaturesConfigModule, I18nModule, provideConfig, UrlModule } from '@spartacus/core';
import { OrderDetailActionsComponent, OrderDetailItemsComponent, OrderDetailReorderComponent, OrderDetailsModule, OrderDetailTotalsComponent } from '@spartacus/order/components';
import { CardModule, OutletModule, SpinnerModule } from '@spartacus/storefront';
import { WorldpayOrderDetailsBillingComponent } from './worldpay-order-details-billing/worldpay-order-details-billing.component';
import { WorldpayOrderOverviewComponent } from './worldpay-order-overview/worldpay-order-overview.component';

@NgModule({
  declarations: [
    WorldpayOrderOverviewComponent,
    WorldpayOrderDetailsBillingComponent
  ],
  imports: [
    CommonModule,
    OutletModule,
    CardModule,
    I18nModule,
    OrderDetailsModule,
    FeaturesConfigModule,
    SpinnerModule,
    RouterLink,
    UrlModule
  ],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        AccountOrderDetailsActionsComponent: {
          component: OrderDetailActionsComponent,
          guards: [AuthGuard],
        },
        AccountOrderDetailsItemsComponent: {
          component: OrderDetailItemsComponent,
          guards: [AuthGuard],
          data: {
            enableAddToCart: true,
          },
        },
        AccountOrderDetailsGroupedItemsComponent: {
          component: OrderDetailItemsComponent,
          guards: [AuthGuard],
          data: {
            enableAddToCart: true,
            groupCartItems: true,
          },
        },
        AccountOrderDetailsTotalsComponent: {
          component: OrderDetailTotalsComponent,
          guards: [AuthGuard],
        },
        AccountOrderDetailsOverviewComponent: {
          component: WorldpayOrderOverviewComponent,
          guards: [AuthGuard],
        },
        AccountOrderDetailsSimpleOverviewComponent: {
          component: WorldpayOrderOverviewComponent,
          guards: [AuthGuard],
          data: {
            simple: true,
          },
        },
        AccountOrderDetailsReorderComponent: {
          component: OrderDetailReorderComponent,
          guards: [AuthGuard],
        },
      },
    }),
  ],
})
export class WorldpayOrderDetailsModule {
}
