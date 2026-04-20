import { NgModule } from '@angular/core';
import { WorldpayB2bFeatureModule } from './b2b';
import { WorldpayGuaranteedPaymentsFeatureModule } from './features';

@NgModule({
  imports: [
    WorldpayB2bFeatureModule,
    WorldpayGuaranteedPaymentsFeatureModule
  ],
})

export class WorldpayB2bModule {
}
