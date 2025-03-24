import { Component, ViewEncapsulation } from '@angular/core';
import { OrderConfirmationShippingComponent } from '@spartacus/order/components';

@Component({
  selector: 'y-worldpay-order-confirmation-shipping',
  templateUrl: './worldpay-order-confirmation-shipping.component.html',
  encapsulation: ViewEncapsulation.None
})
export class WorldpayOrderConfirmationShippingComponent extends OrderConfirmationShippingComponent {
}

