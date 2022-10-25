import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { map } from 'rxjs/operators';
import { CartValidationStateService, ICON_TYPE } from '@spartacus/storefront';

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
