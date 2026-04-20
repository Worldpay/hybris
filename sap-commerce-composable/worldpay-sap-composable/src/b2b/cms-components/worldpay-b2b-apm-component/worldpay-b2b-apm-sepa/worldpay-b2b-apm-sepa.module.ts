import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { I18nModule, provideConfig } from '@spartacus/core';
import { OrderAdapter, OrderConnector } from '@spartacus/order/core';
import { OccOrderAdapter } from '@spartacus/order/occ';
import { FormErrorsModule } from '@spartacus/storefront';
import { OccWorldpayApmAdapter, WorldpayApmAdapter, WorldpayApmService } from '../../../../core';
import { WorldpayApmSubmitButtonsModule, WorldpayBillingAddressModule } from '../../../../storefrontlib';
import { WorldpayB2bApmComponent } from '../worldpay-b2b-apm.component';
import { WorldpayB2BApmSepaComponent } from './worldpay-b2b-apm-sepa.component';

@NgModule({
  declarations: [WorldpayB2BApmSepaComponent],
  exports: [WorldpayB2BApmSepaComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    I18nModule,
    FormErrorsModule,
    NgSelectModule,
    WorldpayBillingAddressModule,
    WorldpayApmSubmitButtonsModule
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
export class WorldpayB2bApmSepaModule {
}
