import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { CartModification, CartValidationFacade } from '@spartacus/cart/base/root';
import { TranslatePipe } from '@spartacus/core';
import { ICON_TYPE, IconComponent } from '@spartacus/storefront';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'cx-cart-item-validation-warning',
  templateUrl: './worldpay-cart-item-validation-warning.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    IconComponent,
    AsyncPipe,
    TranslatePipe
  ],
})
export class WorldpayCartItemValidationWarningComponent {
  @Input() code: string;
  iconTypes: typeof ICON_TYPE = ICON_TYPE;
  isVisible: boolean = true;
  protected cartValidationFacade: CartValidationFacade = inject(CartValidationFacade);
  cartModification$: Observable<CartModification> = this.cartValidationFacade.getValidationResults()
    .pipe(
      map((modificationList: CartModification[]): CartModification =>
        modificationList.find(
          (modification: CartModification): boolean => modification.entry?.product?.code === this.code
        )
      )
    );
}
