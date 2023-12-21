import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayCheckoutDeliveryAddressComponent } from './worldpay-checkout-delivery-address.component';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, FeaturesConfigModule, I18nModule, provideConfig } from '@spartacus/core';
import { CartValidationGuard } from '@spartacus/cart/base/core';
import { WorldpayAddressFormModule } from '../worldpay-address-form/worldpay-address-form.module';
import { CardModule, SpinnerModule } from '@spartacus/storefront';

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
