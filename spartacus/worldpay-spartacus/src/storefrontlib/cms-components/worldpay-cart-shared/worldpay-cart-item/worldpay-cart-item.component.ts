import { Component } from '@angular/core';
import { CartItemComponent, CartItemContext, CartItemContextSource } from '@spartacus/storefront';

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
