import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayApmAchComponent } from './worldpay-apm-ach.component';
import { ReactiveFormsModule } from '@angular/forms';
import { WorldpayBillingAddressModule } from '../../worldpay-billing-address/worldpay-billing-address.module';
import { I18nModule } from '@spartacus/core';
import { FormErrorsModule, SpinnerModule } from '@spartacus/storefront';
import { NgSelectModule } from '@ng-select/ng-select';

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
    SpinnerModule
  ]
})
export class WorldpayApmAchModule {
}
