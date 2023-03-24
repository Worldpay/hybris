import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayOrderDetailShippingComponent } from './worldpay-order-detail-shipping.component';
import { provideConfig } from '@spartacus/core';
import { WorldpayOrderOverviewModule } from '../worldpay-order-confirmation/components/worldpay-order-overview/worldpay-order-overview.module';

@NgModule({
  declarations: [
    WorldpayOrderDetailShippingComponent
  ],
  imports: [
    CommonModule,
    WorldpayOrderOverviewModule
  ],
  providers: [
    provideConfig({
      cmsComponents: {
        AccountOrderDetailsShippingComponent: {
          component: WorldpayOrderDetailShippingComponent
        }
      }
    })
  ]
})
export class WorldpayOrderDetailShippingModule {
}
