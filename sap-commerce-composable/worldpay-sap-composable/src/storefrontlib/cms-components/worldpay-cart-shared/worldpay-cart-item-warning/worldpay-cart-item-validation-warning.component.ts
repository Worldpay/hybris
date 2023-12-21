import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { map } from 'rxjs/operators';
import { ICON_TYPE } from '@spartacus/storefront';
import { CartValidationStateService } from '@spartacus/cart/base/core';

@Component({
  selector: 'cx-cart-item-validation-warning',
  templateUrl: './worldpay-cart-item-validation-warning.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorldpayCartItemValidationWarningComponent {
  @Input()
    code: string | undefined;

  iconTypes = ICON_TYPE;
  isVisible = true;

  cartModification$ =
    this.cartValidationStateService.cartValidationResult$.pipe(
      map((modificationList) =>
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        modificationList.find(
          (modification) => modification?.entry?.product?.code === this.code
        )
      )
    );

  constructor(
    protected cartValidationStateService: CartValidationStateService
  ) {
  }
}
