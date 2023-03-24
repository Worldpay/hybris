import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayApmCreditcardComponent } from './worldpay-apm-creditcard-component.component';
import { MediaModule } from '@spartacus/storefront';

@NgModule({
  declarations: [WorldpayApmCreditcardComponent],
  exports: [WorldpayApmCreditcardComponent],
  imports: [CommonModule, MediaModule]
})
export class WorldpayApmCreditcardComponentModule {}
