import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayApmComponent } from './worldpay-apm-component.component';
import { MediaModule, SpinnerModule } from '@spartacus/storefront';
import { WorldpayApmTileComponent } from './worldpay-apm-tile/worldpay-apm-tile.component';
import { I18nModule, provideConfig } from '@spartacus/core';
import { WorldpayBillingAddressModule } from '../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayApmIdealModule } from './worldpay-apm-ideal/worldpay-apm-ideal.module';
import { WorldpayApmGooglepayModule } from './worldpay-apm-googlepay/worldpay-apm-googlepay.module';
import { WorldpayApplepayModule } from './worldpay-applepay/worldpay-applepay.module';

@NgModule({
  declarations: [
    WorldpayApmComponent,
    WorldpayApmTileComponent
  ],
  exports: [
    WorldpayApmComponent
  ],
  imports: [
    CommonModule,
    MediaModule,
    SpinnerModule,
    WorldpayBillingAddressModule,
    I18nModule,
    WorldpayApmIdealModule,
    WorldpayApmGooglepayModule,
    WorldpayApplepayModule,
  ],
  providers: [
    provideConfig({
      cmsComponents: {
        WorldpayAPMComponent: {
          component: WorldpayApmComponent
        }
      }
    }),
  ]
})
export class WorldpayApmComponentModule {
}
