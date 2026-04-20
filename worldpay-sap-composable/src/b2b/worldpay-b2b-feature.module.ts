import { NgModule } from '@angular/core';
import { WorldpayFeatureModule } from '../features';
import { WorldpayCheckoutScheduledReplenishmentModule } from '../scheduled-replenishment';
import { WorldpayB2bCheckoutPaymentMethodModule, WorldpayB2BCheckoutReviewSubmitModule } from './cms-components';
import { OccWorldpayB2bModule } from './core';

@NgModule({
  declarations: [],
  imports: [
    WorldpayFeatureModule,
    OccWorldpayB2bModule,
    WorldpayB2bCheckoutPaymentMethodModule,
    WorldpayB2BCheckoutReviewSubmitModule,
    WorldpayCheckoutScheduledReplenishmentModule
  ],
})

export class WorldpayB2bFeatureModule {
}
