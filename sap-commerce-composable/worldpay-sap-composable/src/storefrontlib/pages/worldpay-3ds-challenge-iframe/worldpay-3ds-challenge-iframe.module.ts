import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Worldpay3dsChallengeIframeRoutingModule } from './worldpay-3ds-challenge-iframe-routing.module';
import { WorldpayThreedsChallengeIframePageComponent } from './worldpay-threeds-challenge-iframe-page/worldpay-threeds-challenge-iframe-page.component';

@NgModule({
  declarations: [WorldpayThreedsChallengeIframePageComponent],
  imports: [CommonModule, Worldpay3dsChallengeIframeRoutingModule]
})
export class Worldpay3dsChallengeIframeModule {}
