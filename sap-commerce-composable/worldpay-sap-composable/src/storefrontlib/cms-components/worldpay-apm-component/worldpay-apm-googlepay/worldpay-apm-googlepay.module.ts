import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { WorldpayGooglepayAdapter } from '../../../../core/connectors/worldpay-googlepay/worldpay-googlepay.adapter';
import { WorldpayGooglePayConnector } from '../../../../core/connectors/worldpay-googlepay/worldpay-googlepay.connector';
import { OccWorldpayGooglepayAdapter } from '../../../../core/occ/adapters/worldpay-googlepay/occ-worldpay-googlepay.adapter';
import { WorldpayBillingAddressModule } from '../../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayApmGooglepayComponent } from './worldpay-apm-googlepay.component';

@NgModule({
  declarations: [WorldpayApmGooglepayComponent],
  exports: [
    WorldpayApmGooglepayComponent
  ],
  imports: [
    CommonModule,
    WorldpayBillingAddressModule,
  ],
  providers: [
    WorldpayGooglePayConnector,
    {
      provide: WorldpayGooglepayAdapter,
      useClass: OccWorldpayGooglepayAdapter
    }
  ]
})
export class WorldpayApmGooglepayModule {
}
