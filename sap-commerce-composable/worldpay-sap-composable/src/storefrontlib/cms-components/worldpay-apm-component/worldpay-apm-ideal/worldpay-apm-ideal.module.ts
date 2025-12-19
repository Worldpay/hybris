import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { I18nModule } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { WorldpayBillingAddressModule } from '../../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayApmSubmitButtonsModule } from '../worldpay-apm-submit-buttons/worldpay-apm-submit-buttons.module';
import { WorldpayApmIdealComponent } from './worldpay-apm-ideal.component';

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
    WorldpayBillingAddressModule,
    WorldpayApmSubmitButtonsModule
  ]
})
export class WorldpayApmIdealModule { }
