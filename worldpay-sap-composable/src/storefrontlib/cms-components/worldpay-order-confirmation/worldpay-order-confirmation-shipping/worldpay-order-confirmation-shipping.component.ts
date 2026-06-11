import { AsyncPipe } from '@angular/common';
import { Component, ViewEncapsulation } from '@angular/core';
import { AbstractOrderContextDirective } from '@spartacus/cart/base/components';
import { TranslatePipe } from '@spartacus/core';
import { OrderConfirmationShippingComponent } from '@spartacus/order/components';
import { CardComponent, OutletDirective } from '@spartacus/storefront';

@Component({
  selector: 'y-worldpay-order-confirmation-shipping',
  templateUrl: './worldpay-order-confirmation-shipping.component.html',
  encapsulation: ViewEncapsulation.None,
  imports: [
    CardComponent,
    AbstractOrderContextDirective,
    OutletDirective,
    AsyncPipe,
    TranslatePipe
  ]
})
export class WorldpayOrderConfirmationShippingComponent extends OrderConfirmationShippingComponent {
}

