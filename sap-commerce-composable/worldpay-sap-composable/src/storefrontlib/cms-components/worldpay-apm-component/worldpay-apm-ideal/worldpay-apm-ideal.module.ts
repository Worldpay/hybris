import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayApmIdealComponent } from './worldpay-apm-ideal.component';
import { ReactiveFormsModule } from '@angular/forms';
import { I18nModule } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { NgSelectModule } from '@ng-select/ng-select';
import { WorldpayBillingAddressModule } from '../../worldpay-billing-address/worldpay-billing-address.module';

@NgModule({
  declarations: [WorldpayApmIdealComponent],
  exports: [
    WorldpayApmIdealComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    I18nModule,
    FormErrorsModule,
    NgSelectModule,
    WorldpayBillingAddressModule
  ]
})
export class WorldpayApmIdealModule { }
