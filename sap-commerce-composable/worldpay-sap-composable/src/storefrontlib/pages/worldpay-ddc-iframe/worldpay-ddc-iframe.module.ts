import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { WorldpayDdcIframeRoutingModule } from './worldpay-ddc-iframe-routing.module';
import { WorldpayDdcIframePageComponent } from './worldpay-ddc-iframe-page/worldpay-ddc-iframe-page.component';

@NgModule({
  declarations: [WorldpayDdcIframePageComponent],
  imports: [CommonModule, WorldpayDdcIframeRoutingModule]
})
export class WorldpayDdcIframeModule {}
