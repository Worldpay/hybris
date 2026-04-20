import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { I18nModule } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { WorldpayBillingAddressModule } from '../../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayApmSubmitButtonsModule } from '../worldpay-apm-submit-buttons/worldpay-apm-submit-buttons.module';
import { WorldpayApmSepaComponent } from './worldpay-apm-sepa.component';

@NgModule({
  declarations: [WorldpayApmSepaComponent],
  exports: [
    WorldpayApmSepaComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    I18nModule,
    FormErrorsModule,
    NgSelectModule,
    WorldpayBillingAddressModule,
    WorldpayApmSubmitButtonsModule,
    FormsModule
  ]
})
export class WorldpayApmSepaModule { }
