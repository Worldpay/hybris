import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayBillingAddressComponent } from './worldpay-billing-address.component';
import { CardModule, FormErrorsModule } from '@spartacus/storefront';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { I18nModule } from '@spartacus/core';

@NgModule({
  declarations: [WorldpayBillingAddressComponent],
  exports: [
    WorldpayBillingAddressComponent
  ],
  imports: [
    CommonModule,
    CardModule,
    ReactiveFormsModule,
    NgSelectModule,
    FormErrorsModule,
    I18nModule,
  ]
})
export class WorldpayBillingAddressModule { }
