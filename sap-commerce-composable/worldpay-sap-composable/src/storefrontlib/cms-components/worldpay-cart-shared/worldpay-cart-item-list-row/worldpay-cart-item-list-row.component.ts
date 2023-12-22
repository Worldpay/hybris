import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';
import { WorldpayCartItemComponent } from '../worldpay-cart-item/worldpay-cart-item.component';

@Component({
  selector: '[y-worldpay-cart-item-list-row], y-worldpay-cart-item-list-row',
  templateUrl: './worldpay-cart-item-list-row.component.html',
})
export class WorldpayCartItemListRowComponent extends WorldpayCartItemComponent {
  /**
   * Qty control
   * @since 6.4.0
   */
  @Input() qtyControl: FormControl;
}
