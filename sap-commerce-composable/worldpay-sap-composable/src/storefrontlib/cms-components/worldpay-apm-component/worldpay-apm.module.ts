import { CdkAccordionModule } from '@angular/cdk/accordion';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { I18nModule, provideConfig } from '@spartacus/core';
import { OrderAdapter, OrderConnector } from '@spartacus/order/core';
import { OccOrderAdapter } from '@spartacus/order/occ';
import { IconModule, MediaModule, SpinnerModule } from '@spartacus/storefront';
import { OccWorldpayApmAdapter, WorldpayApmAdapter, WorldpayApmService } from '../../../core';
import { WorldpayBillingAddressModule } from '../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayApmAchModule } from './worldpay-apm-ach/worldpay-apm-ach.module';
import { WorldpayApmGooglepayModule } from './worldpay-apm-googlepay/worldpay-apm-googlepay.module';
import { WorldpayApmIdealModule } from './worldpay-apm-ideal/worldpay-apm-ideal.module';
import { WorldpayApmSepaModule } from './worldpay-apm-sepa/worldpay-apm-sepa.module';
import { WorldpayApmSubmitButtonsModule } from './worldpay-apm-submit-buttons/worldpay-apm-submit-buttons.module';
import { WorldpayApmTileModule } from './worldpay-apm-tile/worldpay-apm-tile.module';
import { WorldpayApmComponent } from './worldpay-apm.component';
import { WorldpayApplepayModule } from './worldpay-applepay/worldpay-applepay.module';

@NgModule({
  declarations: [
    WorldpayApmComponent
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
    CdkAccordionModule,
    IconModule,
    WorldpayApmIdealModule,
    WorldpayApmGooglepayModule,
    WorldpayApplepayModule,
    WorldpayApmAchModule,
    WorldpayApmSubmitButtonsModule,
    WorldpayApmSepaModule,
    WorldpayApmTileModule,
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
