import { Component, ViewEncapsulation } from '@angular/core';
import { WorldpayCheckoutPaymentMethodComponent } from '../../../storefrontlib';

@Component({
  selector: 'y-worldpay-b2b-payment-method',
  templateUrl: './worldpay-b2b-checkout-payment-method.component.html',
  encapsulation: ViewEncapsulation.None,
  standalone: false
})
export class WorldpayB2bCheckoutPaymentMethodComponent extends WorldpayCheckoutPaymentMethodComponent {
}
