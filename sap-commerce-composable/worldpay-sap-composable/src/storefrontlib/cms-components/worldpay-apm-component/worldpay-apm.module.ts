import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { I18nModule, provideConfig } from '@spartacus/core';
import { OrderAdapter, OrderConnector } from '@spartacus/order/core';
import { OccOrderAdapter } from '@spartacus/order/occ';
import { MediaModule, SpinnerModule } from '@spartacus/storefront';
import { WorldpayApmAdapter } from 'worldpay-sap-composable-connectors';
import { OccWorldpayApmAdapter } from 'worldpay-sap-composable-occ';
import { WorldpayApmService } from 'worldpay-sap-composable-services';
import { WorldpayBillingAddressModule } from '../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayApmAchModule } from './worldpay-apm-ach/worldpay-apm-ach.module';
import { WorldpayApmGooglepayModule } from './worldpay-apm-googlepay/worldpay-apm-googlepay.module';
import { WorldpayApmIdealModule } from './worldpay-apm-ideal/worldpay-apm-ideal.module';
import { WorldpayApmSubmitButtonsModule } from './worldpay-apm-submit-buttons/worldpay-apm-submit-buttons.module';
import { WorldpayApmTileComponent } from './worldpay-apm-tile/worldpay-apm-tile.component';
import { WorldpayApmComponent } from './worldpay-apm.component';
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
    WorldpayApmAchModule,
    WorldpayApmSubmitButtonsModule,
  ],
  providers: [
    provideConfig({
      cmsComponents: {
        WorldpayAPMComponent: {
          component: WorldpayApmComponent,
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
export class WorldpayApmModule {
}
