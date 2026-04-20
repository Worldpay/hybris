import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { CartNotEmptyGuard, CheckoutAuthGuard, CheckoutPaymentFormModule } from '@spartacus/checkout/base/components';
import { FeaturesConfigModule, I18nModule, provideConfig, UrlModule } from '@spartacus/core';
import {
  CardModule,
  FormErrorsModule,
  FormRequiredAsterisksComponent,
  FormRequiredLegendComponent,
  GenericLinkModule,
  IconModule,
  NgSelectA11yModule,
  SpinnerModule
} from '@spartacus/storefront';
import { WorldpayCheckoutPaymentRedirectFailureGuard } from '../../../core';
import { WorldpayApmModule } from '../worldpay-apm-component/worldpay-apm.module';
import { WorldpayBillingAddressModule } from '../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayPaymentFormModule } from './payment-form/worldpay-payment-form.module';
import { WorldpayCheckoutPaymentMethodComponent } from './worldpay-checkout-payment-method.component';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    CheckoutPaymentFormModule,
    GenericLinkModule,
    SpinnerModule,
    CardModule,
    IconModule,
    NgSelectModule,
    ReactiveFormsModule,
    RouterModule,
    UrlModule,
    WorldpayApmModule,
    FormErrorsModule,
    WorldpayBillingAddressModule,
    NgSelectA11yModule,
    FeaturesConfigModule,
    FormRequiredLegendComponent,
    FormRequiredAsterisksComponent,
    WorldpayPaymentFormModule,
  ],
  declarations: [
    WorldpayCheckoutPaymentMethodComponent
  ],
  exports: [
    WorldpayCheckoutPaymentMethodComponent
  ],
  providers: [
    provideConfig({
      cmsComponents: {
        CheckoutPaymentDetails: {
          component: WorldpayCheckoutPaymentMethodComponent,
          guards: [
            CheckoutAuthGuard,
            CartNotEmptyGuard,
            WorldpayCheckoutPaymentRedirectFailureGuard,
          ],
        },
      }
    }),
  ],
})
export class WorldpayCheckoutPaymentMethodModule {
}
