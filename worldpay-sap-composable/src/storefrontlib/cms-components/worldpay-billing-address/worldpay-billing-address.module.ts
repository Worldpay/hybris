import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { FeaturesConfigModule, I18nModule } from '@spartacus/core';
import { CardModule, FormErrorsModule, FormRequiredAsterisksComponent, FormRequiredLegendComponent, IconModule, NgSelectA11yModule, SpinnerModule } from '@spartacus/storefront';
import { WorldpayBillingAddressComponent } from './worldpay-billing-address.component';

@NgModule({
  declarations: [WorldpayBillingAddressComponent],
  exports: [
    WorldpayBillingAddressComponent
  ],
  imports: [
    NgSelectA11yModule,
    CommonModule,
    ReactiveFormsModule,
    NgSelectModule,
    CardModule,
    I18nModule,
    IconModule,
    SpinnerModule,
    FormErrorsModule,
    FeaturesConfigModule,
    FormRequiredAsterisksComponent,
    FormRequiredLegendComponent,
  ],
})
export class WorldpayBillingAddressModule {
}
