import { Component } from '@angular/core';
import { CartItemComponent, CartItemContextSource } from '@spartacus/cart/base/components';
import { CartItemContext } from '@spartacus/cart/base/root';

@Component({
  selector: 'cx-cart-item',
  templateUrl: './worldpay-cart-item.component.html',
  providers: [
    CartItemContextSource,
    {
      provide: CartItemContext,
      useExisting: CartItemContextSource
    },
  ],
})
export class WorldpayCartItemComponent extends CartItemComponent {

}
