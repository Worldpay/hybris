import { Component } from '@angular/core';
import { CartItemContextSource, CartItemListRowComponent } from '@spartacus/cart/base/components';
import { CartItemContext } from '@spartacus/cart/base/root';
import { useFeatureStyles } from '@spartacus/core';

@Component({
  selector: '[y-worldpay-cart-item-list-row], y-worldpay-cart-item-list-row',
  templateUrl: './worldpay-cart-item-list-row.component.html',
  standalone: false,
  providers: [
    CartItemContextSource,
    {
      provide: CartItemContext,
      useExisting: CartItemContextSource
    },
  ],
})
export class WorldpayCartItemListRowComponent extends CartItemListRowComponent {
  constructor(cartItemContextSource: CartItemContextSource) {
    super(cartItemContextSource);
    useFeatureStyles('a11yQTY2Quantity');
  }
}
