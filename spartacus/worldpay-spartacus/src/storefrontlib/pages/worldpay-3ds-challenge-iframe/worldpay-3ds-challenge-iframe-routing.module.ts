import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WorldpayThreedsChallengeIframePageComponent } from './worldpay-threeds-challenge-iframe-page/worldpay-threeds-challenge-iframe-page.component';

const routes: Routes = [
  {
    path: 'worldpay-3ds-challenge/:action/:md/:jwt',
    component: WorldpayThreedsChallengeIframePageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class Worldpay3dsChallengeIframeRoutingModule {}
