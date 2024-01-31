import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayApmGooglepayComponent } from './worldpay-apm-googlepay.component';
import { WorldpayBillingAddressModule } from '../../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayGooglePayConnector } from '../../../../core/connectors/worldpay-googlepay/worldpay-googlepay.connector';
import { WorldpayGooglepayAdapter } from '../../../../core/connectors/worldpay-googlepay/worldpay-googlepay.adapter';
import { OccWorldpayGooglepayAdapter } from '../../../../core/occ/adapters/worldpay-googlepay/occ-worldpay-googlepay.adapter';

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
