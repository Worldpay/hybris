import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nModule } from '@spartacus/core';
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
})
export class WorldpayApplepayModule {
}
