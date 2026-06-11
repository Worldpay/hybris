import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { Component, ViewEncapsulation } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { CheckoutPaymentFormModule } from '@spartacus/checkout/base/components';
import { FeaturesConfigModule, I18nModule, UrlModule } from '@spartacus/core';
import { CardModule, FormErrorsComponent, GenericLinkModule, IconModule, SpinnerModule } from '@spartacus/storefront';
import { WorldpayCheckoutPaymentFormComponent, WorldpayCheckoutPaymentMethodComponent } from '../../../storefrontlib';
import { WorldpayB2bApmComponent } from '../worldpay-b2b-apm-component';

@Component({
  selector: 'y-worldpay-b2b-payment-method',
  templateUrl: './worldpay-b2b-checkout-payment-method.component.html',
  encapsulation: ViewEncapsulation.None,
  imports: [
    I18nModule,
    GenericLinkModule,
    SpinnerModule,
    CardModule,
    IconModule,
    AsyncPipe,
    NgSelectModule,
    ReactiveFormsModule,
    RouterModule,
    UrlModule,
    FormErrorsComponent,
    NgTemplateOutlet,
    CheckoutPaymentFormModule,
    FeaturesConfigModule,
    WorldpayB2bApmComponent,
    WorldpayCheckoutPaymentFormComponent,
  ],
})
export class WorldpayB2bCheckoutPaymentMethodComponent extends WorldpayCheckoutPaymentMethodComponent {
}
