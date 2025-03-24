import { Component, ViewEncapsulation } from '@angular/core';
import { OrderConfirmationItemsComponent } from '@spartacus/order/components';

@Component({
  selector: 'y-worldpay-order-confirmation-items',
  templateUrl: './worldpay-order-confirmation-items.component.html',
  encapsulation: ViewEncapsulation.None
})
export class WorldpayOrderConfirmationItemsComponent extends OrderConfirmationItemsComponent {

}
