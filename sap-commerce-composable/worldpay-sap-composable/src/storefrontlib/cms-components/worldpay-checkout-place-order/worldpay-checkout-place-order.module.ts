import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { I18nModule, provideConfig, UrlModule } from '@spartacus/core';
import { WorldpayCheckoutPlaceOrderComponent } from './worldpay-checkout-place-order.component';
import { FormErrorsModule, IconModule, SpinnerModule } from '@spartacus/storefront';
import { ReactiveFormsModule } from '@angular/forms';
import { WorldpayCheckoutPaymentConnector } from '../../../core/connectors/worldpay-payment-connector/worldpay-checkout-payment.connector';
import { OccWorldpayCheckoutPaymentAdapter } from '../../../core/occ/adapters/worldpay-checkout-payment-connector/occ-worldpay-checkout-payment.adapter';
import { WorldpayCheckoutPaymentAdapter } from '../../../core/connectors/worldpay-payment-connector/worldpay-checkout-payment.adapter';
import { OrderFacade } from '@spartacus/order/root';
import { WorldpayOrderService } from '../../../core/services';

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
