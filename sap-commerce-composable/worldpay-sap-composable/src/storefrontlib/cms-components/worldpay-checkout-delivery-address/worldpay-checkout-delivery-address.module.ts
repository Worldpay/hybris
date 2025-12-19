import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CartValidationGuard } from '@spartacus/cart/base/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, FeaturesConfigModule, I18nModule, provideConfig } from '@spartacus/core';
import { CardModule, SpinnerModule } from '@spartacus/storefront';
import { WorldpayAddressFormModule } from '../worldpay-address-form/worldpay-address-form.module';
import { WorldpayCheckoutDeliveryAddressComponent } from './worldpay-checkout-delivery-address.component';

@NgModule({
  declarations: [
    WorldpayCheckoutDeliveryAddressComponent
  ],
  imports: [
    CommonModule,
    WorldpayAddressFormModule,
    SpinnerModule,
    CardModule,
    I18nModule,
    FeaturesConfigModule
  ],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        CheckoutDeliveryAddress: {
          component: WorldpayCheckoutDeliveryAddressComponent,
          guards: [CheckoutAuthGuard, CartNotEmptyGuard, CartValidationGuard],
        },
      },
    }),
  ]
})
export class WorldpayCheckoutDeliveryAddressModule {
}
