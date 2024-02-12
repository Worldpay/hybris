import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayFraudsightRiskComponent } from './worldpay-fraudsight-risk.component';
import { WorldpayFraudsightConnector } from '../../../core/connectors/worldpay-fraudsight/worldpay-fraudsight.connector';
import { OccWorldpayFraudsightAdapter } from '../../../core/occ/adapters/worldpay-fraudsight/occ-worldpay-fraudsight.adapter';
import { WorldpayFraudsightAdapter } from '../../../core/connectors/worldpay-fraudsight/worldpay-fraudsight.adapter';
import { EventService } from '@spartacus/core';
import { WorldpayFraudsightService } from '../../../core/services/worldpay-fraudsight/worldpay-fraudsight.service';

@NgModule({
  declarations: [WorldpayFraudsightRiskComponent],
  exports: [WorldpayFraudsightRiskComponent],
  imports: [
    CommonModule
  ],
  providers: [
    EventService,
    WorldpayFraudsightService,
    WorldpayFraudsightConnector,
    {
      provide: WorldpayFraudsightAdapter,
      useClass: OccWorldpayFraudsightAdapter
    }
  ]
})
export class WorldpayFraudsightRiskModule {
}
