import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CartValidationStateService } from '@spartacus/cart/base/core';
import { CartModification } from '@spartacus/cart/base/root';
import { ICON_TYPE } from '@spartacus/storefront';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'cx-cart-item-validation-warning',
  templateUrl: './worldpay-cart-item-validation-warning.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false
})
export class WorldpayCartItemValidationWarningComponent {
  @Input()
    code: string | undefined;

  iconTypes: typeof ICON_TYPE = ICON_TYPE;
  isVisible: boolean = true;

  cartModification$: Observable<CartModification> =
    this.cartValidationStateService.cartValidationResult$.pipe(
      map((modificationList: CartModification[]): CartModification =>
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        modificationList.find(
          (modification: CartModification): boolean => modification?.entry?.product?.code === this.code
        )
      )
    );

  constructor(
    protected cartValidationStateService: CartValidationStateService
  ) {
  }
}
