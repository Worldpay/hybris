import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { WorldpayApplepayAdapter, WorldpayApplepayConnector } from 'worldpay-sap-composable-connectors';
import { OccWorldpayApplepayAdapter } from 'worldpay-sap-composable-occ';
import { WorldpayBillingAddressModule } from '../../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayApplepayComponent } from './worldpay-applepay.component';

@NgModule({
  declarations: [
    WorldpayApplepayComponent
  ],
  exports: [
    WorldpayApplepayComponent
  ],
  imports: [
    CommonModule,
    I18nModule,
    WorldpayBillingAddressModule,
  ],
  providers: [
    WorldpayApplepayConnector,
    {
      provide: WorldpayApplepayAdapter,
      useClass: OccWorldpayApplepayAdapter,
    }
  ],
})
export class WorldpayApplepayModule {
}
