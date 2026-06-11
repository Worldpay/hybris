import { NgClass, NgTemplateOutlet } from '@angular/common';
import { Component, OnChanges } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CartItemComponent, CartItemContextSource } from '@spartacus/cart/base/components';
import { CartItemContext } from '@spartacus/cart/base/root';
import { TranslatePipe, UrlPipe } from '@spartacus/core';
import { AtMessageDirective, ItemCounterComponent, MediaComponent, OutletDirective, PromotionsComponent } from '@spartacus/storefront';
import { WorldpayCartItemValidationWarningComponent } from '../worldpay-cart-item-warning/worldpay-cart-item-validation-warning.component';

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
  imports: [
    OutletDirective,
    WorldpayCartItemValidationWarningComponent,
    NgClass,
    RouterLink,
    MediaComponent,
    ItemCounterComponent,
    PromotionsComponent,
    NgTemplateOutlet,
    AtMessageDirective,
    TranslatePipe,
    UrlPipe,
  ],
})
export class WorldpayCartItemComponent extends CartItemComponent implements OnChanges {

}
