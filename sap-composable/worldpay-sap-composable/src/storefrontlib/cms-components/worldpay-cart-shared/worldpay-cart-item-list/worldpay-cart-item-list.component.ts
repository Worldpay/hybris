import { ChangeDetectionStrategy, Component, ViewEncapsulation } from '@angular/core';
import { CartItemListComponent } from '@spartacus/cart/base/components';
import { AbstractControl, FormControl } from '@angular/forms';

@Component({
  selector: 'y-worldpay-cart-item-list',
  templateUrl: './worldpay-cart-item-list.component.html',
  styleUrls: ['./worldpay-cart-item-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class WorldpayCartItemListComponent extends CartItemListComponent {
  /**
   * Cast qty control
   * @since 6.4.0
   * @param control
   */
  castQtyControl(control: AbstractControl): FormControl {
    return control as FormControl;
  }
}
