import { CommonModule, PathLocationStrategy } from '@angular/common';
import { NgModule } from '@angular/core';
import { CardModule, FormErrorsModule, GenericLinkModule, IconModule, NgSelectA11yModule, SpinnerModule } from '@spartacus/storefront';
import { NgSelectModule } from '@ng-select/ng-select';
import { ReactiveFormsModule } from '@angular/forms';
import { FeaturesConfigModule, I18nModule, provideConfig, UrlModule } from '@spartacus/core';
import { RouterModule } from '@angular/router';
import { WorldpayCheckoutPaymentMethodComponent } from './worldpay-checkout-payment-method.component';
import { WorldpayPaymentFormComponent } from './payment-form/worldpay-payment-form.component';
import { WorldpayBillingAddressModule } from '../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayApmModule } from '../worldpay-apm-component/worldpay-apm.module';
import { WorldpayCheckoutPaymentRedirectFailureGuard } from '../../../core/guards/worldpay-checkout-payment-redirect-failure.guard';
import { CartNotEmptyGuard, CheckoutAuthGuard, CheckoutPaymentFormModule } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressService, CheckoutPaymentService } from '@spartacus/checkout/base/core';
import { CheckoutDeliveryAddressFacade, CheckoutPaymentFacade } from '@spartacus/checkout/base/root';
import { WorldpayCheckoutPaymentService } from '../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayCheckoutPaymentAdapter } from '../../../core/connectors/worldpay-payment-connector/worldpay-checkout-payment.adapter';
import { OccWorldpayCheckoutPaymentAdapter } from '../../../core/occ/adapters/worldpay-checkout-payment-connector/occ-worldpay-checkout-payment.adapter';

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
  ],
  declarations: [
    WorldpayCheckoutPaymentMethodComponent,
    WorldpayPaymentFormComponent,
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
    PathLocationStrategy
  ],
})
export class WorldpayCheckoutPaymentMethodModule {
}
