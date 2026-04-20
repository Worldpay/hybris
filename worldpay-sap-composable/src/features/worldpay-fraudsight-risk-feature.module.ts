import { NgModule } from '@angular/core';
import { WorldpayFraudsightRiskOccModule } from '../core/occ/worldpay-fraudsight-risk-occ.module';
import { WorldpayFraudsightRiskCoreModule } from '../core/worldpay-fraudsight-risk-core.module';

@NgModule({
  imports: [
    WorldpayFraudsightRiskCoreModule,
    WorldpayFraudsightRiskOccModule,
  ]
})
export class WorldpayFraudsightRiskFeatureModule {
}

