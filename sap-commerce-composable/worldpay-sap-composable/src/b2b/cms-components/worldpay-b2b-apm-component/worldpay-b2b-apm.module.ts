import { CdkAccordionModule } from '@angular/cdk/accordion';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { I18nModule, provideConfig } from '@spartacus/core';
import { OrderAdapter, OrderConnector } from '@spartacus/order/core';
import { OccOrderAdapter } from '@spartacus/order/occ';
import { IconModule, MediaModule, SpinnerModule } from '@spartacus/storefront';
import { OccWorldpayApmAdapter, WorldpayApmAdapter, WorldpayApmService } from '../../../core';
import {
  WorldpayApmAchModule,
  WorldpayApmGooglepayModule,
  WorldpayApmIdealModule,
  WorldpayApmSubmitButtonsModule,
  WorldpayApplepayModule,
  WorldpayBillingAddressModule
} from '../../../storefrontlib';
import { WorldpayApmTileModule } from '../../../storefrontlib/cms-components/worldpay-apm-component/worldpay-apm-tile/worldpay-apm-tile.module';
import { WorldpayB2bApmSepaModule } from './worldpay-b2b-apm-sepa';
import { WorldpayB2bApmComponent } from './worldpay-b2b-apm.component';

@NgModule({
  declarations: [
    WorldpayB2bApmComponent,
  ],
  exports: [
    WorldpayB2bApmComponent
  ],
  imports: [
    CommonModule,
    MediaModule,
    SpinnerModule,
    I18nModule,
    WorldpayApmIdealModule,
    WorldpayApmGooglepayModule,
    WorldpayApmAchModule,
    WorldpayBillingAddressModule,
    WorldpayApmSubmitButtonsModule,
    WorldpayApplepayModule,
    WorldpayApmTileModule,
    WorldpayB2bApmSepaModule,
    CdkAccordionModule,
    IconModule,
  ],
  providers: [
    provideConfig({
      cmsComponents: {
        WorldpayAPMComponent: {
          component: WorldpayB2bApmComponent,
          guards: [CheckoutAuthGuard, CartNotEmptyGuard]
        },
      }
    }),
    WorldpayApmService,
    {
      provide: WorldpayApmAdapter,
      useClass: OccWorldpayApmAdapter
    },
    OrderConnector,
    {
      provide: OrderAdapter,
      useClass: OccOrderAdapter,
    },
  ]
})
export class WorldpayB2bApmModule {
}
