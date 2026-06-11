import { NgModule } from '@angular/core';
import { WorldpayOccModule } from '../core';
import { WorldpayCoreModule } from '../core/worldpay-core.module';
import { WorldpayComponentsModule } from '../storefrontlib';

@NgModule({
  imports: [
    WorldpayCoreModule,
    WorldpayOccModule,
    WorldpayComponentsModule
  ]
})
export class WorldpayFeatureModule {
}
