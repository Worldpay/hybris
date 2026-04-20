import { Component, ViewEncapsulation } from '@angular/core';
import { CheckoutDeliveryAddressComponent } from '@spartacus/checkout/base/components';

@Component({
  selector: 'y-worldpay-checkout-delivery-address',
  templateUrl: './worldpay-checkout-delivery-address.component.html',
  styleUrls: ['./worldpay-checkout-delivery-address.component.scss'],
  encapsulation: ViewEncapsulation.None,
  standalone: false
})
export class WorldpayCheckoutDeliveryAddressComponent extends CheckoutDeliveryAddressComponent {
// Extended to include Japan address validation
}
