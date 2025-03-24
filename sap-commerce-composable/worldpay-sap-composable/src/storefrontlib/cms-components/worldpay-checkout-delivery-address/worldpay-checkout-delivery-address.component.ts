import { Component, ViewEncapsulation } from '@angular/core';
import { CheckoutDeliveryAddressComponent } from '@spartacus/checkout/base/components';

@Component({
  selector: 'y-worldpay-checkout-delivery-address',
  templateUrl: './worldpay-checkout-delivery-address.component.html',
  encapsulation: ViewEncapsulation.None
})
export class WorldpayCheckoutDeliveryAddressComponent extends CheckoutDeliveryAddressComponent {

}
