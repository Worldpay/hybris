import { NgModule } from '@angular/core';
import { Worldpay3dsChallengeIframeRoutingModule } from './worldpay-3ds-challenge-iframe-routing.module';
import { WorldpayThreedsChallengeIframePageComponent } from './worldpay-threeds-challenge-iframe-page/worldpay-threeds-challenge-iframe-page.component';

/**
 * Angular module for the Worldpay 3DS Challenge Iframe page feature.
 *
 * This module imports the routing module and the standalone page component for the 3DS Challenge Iframe.
 * It exports the page component for use in other modules.
 *
 * @since 221121.11.0
 */
@NgModule({
  imports: [
    Worldpay3dsChallengeIframeRoutingModule,
    WorldpayThreedsChallengeIframePageComponent
  ],
  exports: [WorldpayThreedsChallengeIframePageComponent],
})
export class Worldpay3dsChallengeIframeModule {
}
