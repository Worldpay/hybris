import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CardModule, FormErrorsModule, GenericLinkModule, IconModule, SpinnerModule } from '@spartacus/storefront';
import { NgSelectModule } from '@ng-select/ng-select';
import { ReactiveFormsModule } from '@angular/forms';
import { I18nModule, provideConfig, UrlModule } from '@spartacus/core';
import { RouterModule } from '@angular/router';
import { CartNotEmptyGuard, CheckoutAuthGuard, PaymentFormModule } from '@spartacus/checkout/components';
import { WorldpayPaymentComponent } from './worldpay-payment-component.component';
import { WorldpayPaymentFormComponent } from './payment-form/worldpay-payment-form.component';
import { CheckoutDeliveryService, CheckoutService } from '@spartacus/checkout/core';
import { CheckoutDeliveryFacade, CheckoutFacade } from '@spartacus/checkout/root';
import { WorldpayBillingAddressModule } from '../worldpay-billing-address/worldpay-billing-address.module';
import { WorldpayApmComponentModule } from '../worldpay-apm-component/worldpay-apm-component.module';
import { WorldpayCheckoutPaymentRedirectFailureGuard } from '../../../core/guards/worldpay-checkout-payment-redirect-failure.guard';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    PaymentFormModule,
    GenericLinkModule,
    SpinnerModule,
    CardModule,
    IconModule,
    NgSelectModule,
    ReactiveFormsModule,
    RouterModule,
    UrlModule,
    WorldpayApmComponentModule,
    FormErrorsModule,
    WorldpayBillingAddressModule,
  ],
  declarations: [
    WorldpayPaymentComponent,
    WorldpayPaymentFormComponent,
  ],
  exports: [
    WorldpayPaymentComponent
  ],
  providers: [
    provideConfig({
      cmsComponents: {
        CheckoutPaymentDetails: {
          component: WorldpayPaymentComponent,
          guards: [
            CheckoutAuthGuard,
            CartNotEmptyGuard,
            WorldpayCheckoutPaymentRedirectFailureGuard,
          ],
        },
      }
    }),
    CheckoutService,
    {
      provide: CheckoutFacade,
      useExisting: CheckoutService,
    },
    CheckoutDeliveryService,
    {
      provide: CheckoutDeliveryFacade,
      useExisting: CheckoutDeliveryService,
    },
  ],
})
export class WorldpayPaymentComponentModule {
}
