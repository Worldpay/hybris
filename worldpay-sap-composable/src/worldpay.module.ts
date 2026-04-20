import { NgModule } from '@angular/core';
import { WorldpayFeatureModule, WorldpayGuaranteedPaymentsFeatureModule } from './features';

@NgModule({
  imports: [
    WorldpayFeatureModule,
    WorldpayGuaranteedPaymentsFeatureModule
  ],
})

export class WorldpayModule {
}
