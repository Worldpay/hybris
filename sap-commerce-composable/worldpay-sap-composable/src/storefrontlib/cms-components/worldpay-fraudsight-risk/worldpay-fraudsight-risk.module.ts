import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { EventService } from '@spartacus/core';
import { WorldpayFraudsightAdapter } from '../../../core/connectors/worldpay-fraudsight/worldpay-fraudsight.adapter';
import { WorldpayFraudsightConnector } from '../../../core/connectors/worldpay-fraudsight/worldpay-fraudsight.connector';
import { OccWorldpayFraudsightAdapter } from '../../../core/occ/adapters/worldpay-fraudsight/occ-worldpay-fraudsight.adapter';
import { WorldpayFraudsightService } from '../../../core/services/worldpay-fraudsight/worldpay-fraudsight.service';
import { WorldpayFraudsightRiskComponent } from './worldpay-fraudsight-risk.component';

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
