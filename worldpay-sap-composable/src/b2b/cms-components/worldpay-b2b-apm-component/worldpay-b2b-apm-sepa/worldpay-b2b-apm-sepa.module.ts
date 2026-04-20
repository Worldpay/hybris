import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { I18nModule } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { WorldpayApmSubmitButtonsModule, WorldpayBillingAddressModule } from '../../../../storefrontlib';
import { WorldpayB2BApmSepaComponent } from './worldpay-b2b-apm-sepa.component';

@NgModule({
  declarations: [WorldpayB2BApmSepaComponent],
  exports: [WorldpayB2BApmSepaComponent],
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
export class WorldpayB2bApmSepaModule {
}
