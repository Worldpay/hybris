/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CartNotEmptyGuard, CheckoutAuthGuard, } from '@spartacus/checkout/base/components';
import { CmsConfig, I18nModule, provideConfig } from '@spartacus/core';
import { IconModule } from '@spartacus/storefront';
import { WorldpayCheckoutScheduleReplenishmentOrderComponent } from './worldpay-checkout-schedule-replenishment-order.component';

@NgModule({
  imports: [CommonModule, RouterModule, I18nModule, IconModule],
  providers: [
    provideConfig(<CmsConfig>{
      cmsComponents: {
        CheckoutScheduleReplenishmentOrder: {
          component: WorldpayCheckoutScheduleReplenishmentOrderComponent,
          guards: [CheckoutAuthGuard, CartNotEmptyGuard],
        },
      },
    }),
  ],
  declarations: [WorldpayCheckoutScheduleReplenishmentOrderComponent],
  exports: [WorldpayCheckoutScheduleReplenishmentOrderComponent],
})
export class WorldpayCheckoutScheduleReplenishmentOrderModule {
}
