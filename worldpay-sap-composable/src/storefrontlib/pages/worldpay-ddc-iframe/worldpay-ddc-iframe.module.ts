import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { WorldpayDdcIframePageComponent } from './worldpay-ddc-iframe-page/worldpay-ddc-iframe-page.component';
import { WorldpayDdcIframeRoutingModule } from './worldpay-ddc-iframe-routing.module';

@NgModule({
  declarations: [WorldpayDdcIframePageComponent],
  imports: [CommonModule, WorldpayDdcIframeRoutingModule]
})
export class WorldpayDdcIframeModule {
}
