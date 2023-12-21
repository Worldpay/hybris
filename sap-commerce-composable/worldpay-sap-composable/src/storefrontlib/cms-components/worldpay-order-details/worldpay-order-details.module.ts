import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayOrderOverviewComponent } from './worldpay-order-overview/worldpay-order-overview.component';
import { AuthGuard, CmsConfig, FeaturesConfig, FeaturesConfigModule, I18nModule, provideConfig } from '@spartacus/core';
import { OrderDetailActionsComponent, OrderDetailItemsComponent, OrderDetailReorderComponent, OrderDetailsModule, OrderDetailTotalsComponent } from '@spartacus/order/components';
import { CardModule, OutletModule, SpinnerModule } from '@spartacus/storefront';
import { WorldpayOrderDetailsBillingComponent } from './worldpay-order-details-billing/worldpay-order-details-billing.component';

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
    SpinnerModule
  ],
  providers: [
    provideConfig(<CmsConfig | FeaturesConfig>{
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
      features: {
        consignmentTracking: '1.2',
      },
    }),
  ],
})
export class WorldpayOrderDetailsModule {
}
