import { NgModule } from '@angular/core';
import { CheckoutOccModule } from '@spartacus/checkout/base/occ';
import { UserOccModule } from '@spartacus/core';
import { OrderOccModule } from '@spartacus/order/occ';
import { worldpayOccConfigProvider } from '../../providers/worldpay-occ-config.provider';
import { provideWorldpayAdapters } from './worldpay-adapters.providers';

@NgModule({
  imports: [
    OrderOccModule,
    CheckoutOccModule,
    UserOccModule
  ],
  providers: [
    worldpayOccConfigProvider(),
    ...provideWorldpayAdapters()
  ]
})
export class WorldpayOccModule {
}
