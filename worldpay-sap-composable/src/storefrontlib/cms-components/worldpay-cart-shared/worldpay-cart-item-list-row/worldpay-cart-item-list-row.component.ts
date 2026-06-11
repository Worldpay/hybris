import { NgTemplateOutlet } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CartItemContextSource, CartItemListRowComponent } from '@spartacus/cart/base/components';
import { CartItemContext } from '@spartacus/cart/base/root';
import { TranslatePipe, UrlPipe } from '@spartacus/core';
import { AtMessageDirective, ItemCounterComponent, MediaComponent, OutletDirective, PromotionsComponent } from '@spartacus/storefront';
import { WorldpayCartItemValidationWarningComponent } from '../worldpay-cart-item-warning/worldpay-cart-item-validation-warning.component';

@Component({
  selector: '[y-worldpay-cart-item-list-row], y-worldpay-cart-item-list-row',
  templateUrl: './worldpay-cart-item-list-row.component.html',
  providers: [
    CartItemContextSource,
    {
      provide: CartItemContext,
      useExisting: CartItemContextSource
    }
  ],
  imports: [
    OutletDirective,
    WorldpayCartItemValidationWarningComponent,
    RouterLink,
    MediaComponent,
    PromotionsComponent,
    ItemCounterComponent,
    NgTemplateOutlet,
    AtMessageDirective,
    TranslatePipe,
    UrlPipe
  ]
})
export class WorldpayCartItemListRowComponent extends CartItemListRowComponent {
}
