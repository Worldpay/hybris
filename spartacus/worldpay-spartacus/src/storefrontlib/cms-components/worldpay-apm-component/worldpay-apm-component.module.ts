import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayApmComponent } from './worldpay-apm-component.component';
import { MediaModule } from '@spartacus/storefront';

@NgModule({
  declarations: [WorldpayApmComponent],
  exports: [WorldpayApmComponent],
  imports: [CommonModule, MediaModule]
})
export class WorldpayApmComponentModule {}
