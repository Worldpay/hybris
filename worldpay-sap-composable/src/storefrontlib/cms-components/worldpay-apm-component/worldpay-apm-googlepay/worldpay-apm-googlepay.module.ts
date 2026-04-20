import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nModule } from '@spartacus/core';
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
    I18nModule,
  ]
})
export class WorldpayApmGooglepayModule {
}
