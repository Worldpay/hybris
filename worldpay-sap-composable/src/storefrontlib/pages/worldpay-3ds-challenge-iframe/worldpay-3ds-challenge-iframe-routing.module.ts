import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WorldpayThreedsChallengeIframePageComponent } from './worldpay-threeds-challenge-iframe-page/worldpay-threeds-challenge-iframe-page.component';

/**
 * Route configuration for the Worldpay 3DS Challenge Iframe page.
 *
 * Defines the route for displaying the WorldpayThreedsChallengeIframePageComponent
 * at the path 'worldpay-3ds-challenge'.
 */
export const worldpayThreedsChallengeIframePageRoutes: Routes = [
  {
    path: 'worldpay-3ds-challenge',
    component: WorldpayThreedsChallengeIframePageComponent
  }
];

/**
 * Angular routing module for the Worldpay 3DS Challenge Iframe page.
 *
 * Imports the RouterModule configured with the Worldpay 3DS Challenge Iframe page routes
 * and exports the RouterModule for use in other modules.
 */
@NgModule({
  imports: [RouterModule.forChild(worldpayThreedsChallengeIframePageRoutes)],
  exports: [RouterModule]
})
export class Worldpay3dsChallengeIframeRoutingModule {
}
