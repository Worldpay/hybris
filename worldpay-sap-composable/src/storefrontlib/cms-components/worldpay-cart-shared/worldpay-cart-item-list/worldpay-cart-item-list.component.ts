import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, ViewEncapsulation } from '@angular/core';
import { AbstractControl, FormControl } from '@angular/forms';
import { CartItemListComponent } from '@spartacus/cart/base/components';
import { TranslatePipe } from '@spartacus/core';
import { OutletDirective } from '@spartacus/storefront';
import { WorldpayCartItemListRowComponent } from '../worldpay-cart-item-list-row/worldpay-cart-item-list-row.component';

@Component({
  selector: 'y-worldpay-cart-item-list',
  templateUrl: './worldpay-cart-item-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  imports: [
    OutletDirective,
    WorldpayCartItemListRowComponent,
    AsyncPipe,
    TranslatePipe
  ]
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
