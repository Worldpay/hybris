import { Component, ViewEncapsulation } from '@angular/core';
import { OrderConfirmationShippingComponent } from '@spartacus/order/components';

@Component({
  selector: 'y-worldpay-order-confirmation-shipping',
  templateUrl: './worldpay-order-confirmation-shipping.component.html',
  styleUrls: ['./worldpay-order-confirmation-shipping.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class WorldpayOrderConfirmationShippingComponent extends OrderConfirmationShippingComponent {
}

