import { ChangeDetectionStrategy, Component } from '@angular/core';
import { CartItemListComponent } from '@spartacus/storefront';

@Component({
  selector: 'y-worldpay-cart-item-list',
  templateUrl: './worldpay-cart-item-list.component.html',
  styleUrls: ['worldpay-cart-item-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorldpayCartItemListComponent extends CartItemListComponent {

}
