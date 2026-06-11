import { NgModule } from '@angular/core';
import { WorldpayCheckoutB2BCheckoutDeliveryAddressComponent } from './worldpay-b2b-checkout-delivery-address.component';
import { WORLDPAY_CHECKOUT_B2B_DELIVERY_ADDRESS_FEATURE_PROVIDERS } from './worldpay-b2b-checkout-delivery-address.providers';

@NgModule({
  imports: [WorldpayCheckoutB2BCheckoutDeliveryAddressComponent],
  exports: [WorldpayCheckoutB2BCheckoutDeliveryAddressComponent],
  providers: WORLDPAY_CHECKOUT_B2B_DELIVERY_ADDRESS_FEATURE_PROVIDERS,
})
export class  WorldpayB2BCheckoutDeliveryAddressModule {
}
