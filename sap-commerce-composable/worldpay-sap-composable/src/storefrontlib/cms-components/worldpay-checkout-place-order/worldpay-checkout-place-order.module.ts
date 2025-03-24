import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { I18nModule, provideConfig, UrlModule } from '@spartacus/core';
import { OrderFacade } from '@spartacus/order/root';
import { FormErrorsModule, IconModule, SpinnerModule } from '@spartacus/storefront';
import { WorldpayCheckoutPaymentAdapter } from '@worldpay-connectors/worldpay-payment-connector/worldpay-checkout-payment.adapter';
import { WorldpayCheckoutPaymentConnector } from '@worldpay-connectors/worldpay-payment-connector/worldpay-checkout-payment.connector';
import { OccWorldpayCheckoutPaymentAdapter } from '@worldpay-occ/adapters/worldpay-checkout-payment-connector/occ-worldpay-checkout-payment.adapter';
import { WorldpayOrderService } from '../../../core/services';
import { WorldpayCheckoutPlaceOrderComponent } from './worldpay-checkout-place-order.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    UrlModule,
    I18nModule,
    ReactiveFormsModule,
    FormErrorsModule,
    SpinnerModule,
    IconModule
  ],
  declarations: [WorldpayCheckoutPlaceOrderComponent],
  exports: [WorldpayCheckoutPlaceOrderComponent],
  providers: [
    provideConfig({
      cmsComponents: {
        CheckoutPlaceOrder: {
          component: WorldpayCheckoutPlaceOrderComponent,
        },
      },
    }),
    {
      provide: OrderFacade,
      useExisting: WorldpayOrderService
    },
    WorldpayCheckoutPaymentConnector,
    {
      provide: WorldpayCheckoutPaymentAdapter,
      useClass: OccWorldpayCheckoutPaymentAdapter
    }
  ],
})
export class WorldpayCheckoutPlaceOrderModule {
}
