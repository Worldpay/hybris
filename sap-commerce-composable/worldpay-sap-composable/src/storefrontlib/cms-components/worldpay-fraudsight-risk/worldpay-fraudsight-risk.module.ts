import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { EventService } from '@spartacus/core';
import { OccWorldpayFraudsightAdapter, WorldpayFraudsightAdapter, WorldpayFraudsightConnector, WorldpayFraudsightService } from '../../../core';
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
