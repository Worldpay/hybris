import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WorldpayDdcIframePageComponent } from './worldpay-ddc-iframe-page/worldpay-ddc-iframe-page.component';

const routes: Routes = [
  {
    path: 'worldpay-3ds-device-detection',
    component: WorldpayDdcIframePageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class WorldpayDdcIframeRoutingModule {
}
