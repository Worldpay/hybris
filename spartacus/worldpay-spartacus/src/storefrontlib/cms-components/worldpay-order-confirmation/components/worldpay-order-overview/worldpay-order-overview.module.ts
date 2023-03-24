import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { WorldpayOrderOverviewComponent } from './worldpay-order-overview.component';
import { CardModule } from '@spartacus/storefront';

@NgModule({
  imports: [CommonModule, I18nModule, CardModule],
  declarations: [WorldpayOrderOverviewComponent],
  exports: [WorldpayOrderOverviewComponent],
})
export class WorldpayOrderOverviewModule {
}
