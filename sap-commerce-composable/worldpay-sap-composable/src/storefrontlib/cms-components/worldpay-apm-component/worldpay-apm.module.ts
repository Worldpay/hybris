import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayApmComponent } from './worldpay-apm.component';
import { MediaModule, SpinnerModule } from '@spartacus/storefront';
import { WorldpayApmTileComponent } from './worldpay-apm-tile/worldpay-apm-tile.component';
import { I18nModule, provideConfig } from '@spartacus/core';
import { WorldpayBillingAddressModule } from '../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayApmIdealModule } from './worldpay-apm-ideal/worldpay-apm-ideal.module';
import { WorldpayApmGooglepayModule } from './worldpay-apm-googlepay/worldpay-apm-googlepay.module';
import { WorldpayApplepayModule } from './worldpay-applepay/worldpay-applepay.module';
import { WorldpayApmService } from '../../../core/services/worldpay-apm/worldpay-apm.service';
import { WorldpayApmAdapter } from '../../../core/connectors/worldpay-apm/worldpay-apm.adapter';
import { OccWorldpayApmAdapter } from '../../../core/occ/adapters/worldpay-apm/occ-worldpay-apm.adapter';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { OrderAdapter, OrderConnector } from '@spartacus/order/core';
import { OccOrderAdapter } from '@spartacus/order/occ';
import { WorldpayApmAchModule } from './worldpay-apm-ach/worldpay-apm-ach.module';

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
