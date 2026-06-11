import { NgModule } from '@angular/core';
import { WorldpayFraudsightRiskComponent } from './worldpay-fraudsight-risk.component';
import { WORLDPAY_FRAUDSIGHT_FEATURE_PROVIDERS } from './worldpay-fraudsight-risk.providers';

/**
 * @since 221121.11.0. Use the `WorldpayFraudsightRiskComponent` (standalone) instead.
 */
@NgModule({
  exports: [WorldpayFraudsightRiskComponent],
  imports: [WorldpayFraudsightRiskComponent],
  providers: WORLDPAY_FRAUDSIGHT_FEATURE_PROVIDERS
})
export class WorldpayFraudsightRiskComponentModule {
}
