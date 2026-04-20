import { Component, ViewEncapsulation } from '@angular/core';
import { OrderConfirmationItemsComponent } from '@spartacus/order/components';

@Component({
  selector: 'y-worldpay-order-confirmation-items',
  templateUrl: './worldpay-order-confirmation-items.component.html',
  styleUrls: ['./worldpay-order-confirmation-items.component.scss'],
  encapsulation: ViewEncapsulation.None,
  standalone: false
})
export class WorldpayOrderConfirmationItemsComponent extends OrderConfirmationItemsComponent {
// This component fixes the missing image from OOTB
}
