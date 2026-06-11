import { AsyncPipe } from '@angular/common';
import { Component, ViewEncapsulation } from '@angular/core';
import { CheckoutDeliveryAddressComponent } from '@spartacus/checkout/base/components';
import { TranslatePipe } from '@spartacus/core';
import { CardComponent, SpinnerComponent } from '@spartacus/storefront';
import { WorldpayAddressFormComponent } from '../worldpay-address-form/worldpay-address-form.component';

@Component({
  selector: 'y-worldpay-checkout-delivery-address',
  templateUrl: './worldpay-checkout-delivery-address.component.html',
  encapsulation: ViewEncapsulation.None,
  imports: [
    CardComponent,
    SpinnerComponent,
    AsyncPipe,
    TranslatePipe,
    WorldpayAddressFormComponent,
  ]
})
export class WorldpayCheckoutDeliveryAddressComponent extends CheckoutDeliveryAddressComponent {
  // Extended to include Japan address validation
}
