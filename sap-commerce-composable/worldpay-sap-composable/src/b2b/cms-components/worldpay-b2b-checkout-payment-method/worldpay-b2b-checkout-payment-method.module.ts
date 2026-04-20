import { CommonModule, PathLocationStrategy } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { CartNotEmptyGuard, CheckoutAuthGuard, CheckoutPaymentFormModule } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressService, CheckoutPaymentService } from '@spartacus/checkout/base/core';
import { CheckoutDeliveryAddressFacade, CheckoutPaymentFacade } from '@spartacus/checkout/base/root';
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
import {
  OccWorldpayCheckoutPaymentAdapter,
  OccWorldpayUserPaymentAdapter,
  WorldpayCheckoutPaymentAdapter,
  WorldpayCheckoutPaymentRedirectFailureGuard,
  WorldpayCheckoutPaymentService,
  WorldpayUserPaymentAdapter
} from '../../../core';
import { WorldpayApmModule, WorldpayBillingAddressModule } from '../../../storefrontlib';
import { WorldpayPaymentFormModule } from '../../../storefrontlib/cms-components/worldpay-checkout-payment-method/payment-form/worldpay-payment-form.module';
import { WorldpayB2bApmModule } from '../worldpay-b2b-apm-component';
import { WorldpayB2bCheckoutPaymentMethodComponent } from './worldpay-b2b-checkout-payment-method.component';

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
    WorldpayB2bApmModule,
    FormErrorsModule,
    WorldpayBillingAddressModule,
    NgSelectA11yModule,
    FeaturesConfigModule,
    FormRequiredLegendComponent,
    FormRequiredAsterisksComponent,
    WorldpayPaymentFormModule,
    WorldpayApmModule,
  ],
  declarations: [WorldpayB2bCheckoutPaymentMethodComponent],
  exports: [
    WorldpayB2bCheckoutPaymentMethodComponent
  ],
  providers: [
    provideConfig({
      cmsComponents: {
        CheckoutPaymentDetails: {
          component: WorldpayB2bCheckoutPaymentMethodComponent,
          guards: [
            CheckoutAuthGuard,
            CartNotEmptyGuard,
            WorldpayCheckoutPaymentRedirectFailureGuard,
          ],
        },
      }
    }),
    CheckoutPaymentService,
    {
      provide: CheckoutPaymentFacade,
      useExisting: WorldpayCheckoutPaymentService,
    },
    CheckoutDeliveryAddressService,
    {
      provide: CheckoutDeliveryAddressFacade,
      useExisting: CheckoutDeliveryAddressService,
    },
    {
      provide: WorldpayCheckoutPaymentAdapter,
      useClass: OccWorldpayCheckoutPaymentAdapter,
    },
    {
      provide: WorldpayUserPaymentAdapter,
      useClass: OccWorldpayUserPaymentAdapter
    },
    PathLocationStrategy
  ],
})
export class WorldpayB2bCheckoutPaymentMethodModule {
}
