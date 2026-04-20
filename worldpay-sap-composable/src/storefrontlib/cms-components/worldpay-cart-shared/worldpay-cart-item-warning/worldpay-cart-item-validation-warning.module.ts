import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { I18nModule, UrlModule } from '@spartacus/core';
import { IconModule } from '@spartacus/storefront';
import { WorldpayCartItemValidationWarningComponent } from './worldpay-cart-item-validation-warning.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    I18nModule,
    UrlModule,
    IconModule
  ],
  exports: [
    WorldpayCartItemValidationWarningComponent
  ],
  declarations: [
    WorldpayCartItemValidationWarningComponent
  ],
})
export class WorldpayCartItemValidationWarningModule {
}
