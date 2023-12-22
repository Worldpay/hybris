import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayAddressFormComponent } from './worldpay-address-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { I18nModule } from '@spartacus/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormErrorsModule, NgSelectA11yModule } from '@spartacus/storefront';

@NgModule({
  declarations: [
    WorldpayAddressFormComponent
  ],
  exports: [
    WorldpayAddressFormComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    I18nModule,
    NgSelectModule,
    NgSelectA11yModule,
    FormErrorsModule
  ],
})
export class WorldpayAddressFormModule {
}
