import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { I18nModule } from '@spartacus/core';
import { FormErrorsModule, SpinnerModule } from '@spartacus/storefront';
import { WorldpayBillingAddressModule } from '../../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayApmSubmitButtonsModule } from '../worldpay-apm-submit-buttons/worldpay-apm-submit-buttons.module';
import { WorldpayApmAchComponent } from './worldpay-apm-ach.component';

@NgModule({
  declarations: [
    WorldpayApmAchComponent
  ],
  exports: [
    WorldpayApmAchComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormErrorsModule,
    I18nModule,
    WorldpayBillingAddressModule,
    NgSelectModule,
    SpinnerModule,
    WorldpayApmSubmitButtonsModule
  ]
})
export class WorldpayApmAchModule {
}
