import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { I18nModule } from '@spartacus/core';
import { FormErrorsModule, FormRequiredAsterisksComponent, FormRequiredLegendComponent, NgSelectA11yModule } from '@spartacus/storefront';
import { WorldpayAddressFormComponent } from './worldpay-address-form.component';

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
    FormErrorsModule,
    FormRequiredLegendComponent,
    FormRequiredAsterisksComponent
  ],
})
export class WorldpayAddressFormModule {
}
