import { AsyncPipe } from '@angular/common';
import { Component, ViewEncapsulation } from '@angular/core';
import { AbstractOrderContextDirective } from '@spartacus/cart/base/components';
import { TranslatePipe } from '@spartacus/core';
import { OrderConfirmationItemsComponent } from '@spartacus/order/components';
import { OutletDirective, PromotionsComponent } from '@spartacus/storefront';

@Component({
  selector: 'y-worldpay-order-confirmation-items',
  templateUrl: './worldpay-order-confirmation-items.component.html',
  encapsulation: ViewEncapsulation.None,
  imports: [
    PromotionsComponent,
    AbstractOrderContextDirective,
    OutletDirective,
    AsyncPipe,
    TranslatePipe
  ]
})
export class WorldpayOrderConfirmationItemsComponent extends OrderConfirmationItemsComponent {
  // This component fixes the missing image from OOTB
}
