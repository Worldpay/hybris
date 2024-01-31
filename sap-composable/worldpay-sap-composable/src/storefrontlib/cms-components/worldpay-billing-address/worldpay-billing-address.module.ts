import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayBillingAddressComponent } from './worldpay-billing-address.component';
import { CardModule, FormErrorsModule } from '@spartacus/storefront';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { I18nModule } from '@spartacus/core';
import { CheckoutDeliveryAddressAdapter, CheckoutDeliveryAddressConnector } from '@spartacus/checkout/base/core';
import { OccCheckoutDeliveryAddressAdapter } from '@spartacus/checkout/base/occ';

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
  ],
  providers: [
    CheckoutDeliveryAddressConnector,
    {
      provide: CheckoutDeliveryAddressAdapter,
      useClass: OccCheckoutDeliveryAddressAdapter
    }
  ]
})
export class WorldpayBillingAddressModule {
}
