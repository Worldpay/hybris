import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { WorldpayApplepayComponent } from './worldpay-applepay.component';
import { WorldpayBillingAddressModule } from '../../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayApplepayAdapter } from '../../../../core/connectors/worldpay-applepay/worldpay-applepay.adapter';
import { OccWorldpayApplepayAdapter } from '../../../../core/occ/adapters/worldpay-applepay/occ-worldpay-applepay.adapter';
import { WorldpayApplepayConnector } from '../../../../core/connectors/worldpay-applepay/worldpay-applepay.connector';

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
