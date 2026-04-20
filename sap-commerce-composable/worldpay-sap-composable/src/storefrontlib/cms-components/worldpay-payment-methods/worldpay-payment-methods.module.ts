/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AuthGuard, CmsConfig, FeaturesConfigModule, I18nModule, provideDefaultConfig, } from '@spartacus/core';
import { CardModule, FormRequiredLegendComponent, SpinnerModule } from '@spartacus/storefront';
import { WorldpayPaymentMethodsComponent } from './worldpay-payment-methods.component';

@NgModule({
  imports: [
    CommonModule,
    CardModule,
    SpinnerModule,
    I18nModule,
    FeaturesConfigModule,
    FormRequiredLegendComponent,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        AccountPaymentDetailsComponent: {
          component: WorldpayPaymentMethodsComponent,
          guards: [AuthGuard],
        },
      },
    }),
  ],
  declarations: [WorldpayPaymentMethodsComponent],
  exports: [WorldpayPaymentMethodsComponent],
})
export class WorldpayPaymentMethodsModule {
}
