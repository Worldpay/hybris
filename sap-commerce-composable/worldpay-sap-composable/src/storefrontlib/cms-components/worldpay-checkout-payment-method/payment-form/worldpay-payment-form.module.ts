import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectComponent } from '@ng-select/ng-select';
import { I18nModule } from '@spartacus/core';
import { FormErrorsModule, FormRequiredAsterisksComponent, FormRequiredLegendComponent, IconModule, NgSelectA11yModule, SpinnerModule } from '@spartacus/storefront';
import { WorldpayBillingAddressModule } from '../../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayPaymentFormComponent } from './worldpay-payment-form.component';

@NgModule({
  declarations: [WorldpayPaymentFormComponent],
  exports: [WorldpayPaymentFormComponent],
  imports: [
    CommonModule,
    SpinnerModule,
    I18nModule,
    WorldpayBillingAddressModule,
    FormErrorsModule,
    FormRequiredAsterisksComponent,
    NgSelectComponent,
    FormRequiredLegendComponent,
    ReactiveFormsModule,
    NgSelectA11yModule,
    IconModule
  ]
})
export class WorldpayPaymentFormModule {
}
