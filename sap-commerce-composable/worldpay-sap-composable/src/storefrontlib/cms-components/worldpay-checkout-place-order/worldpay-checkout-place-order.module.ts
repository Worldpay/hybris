import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { I18nModule, provideConfig, UrlModule } from '@spartacus/core';
import { OrderFacade } from '@spartacus/order/root';
import { FormErrorsModule, IconModule, SpinnerModule } from '@spartacus/storefront';
import { OccWorldpayCheckoutPaymentAdapter, WorldpayCheckoutPaymentAdapter, WorldpayCheckoutPaymentConnector, WorldpayOrderService } from '../../../core';
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
          guards: [CheckoutAuthGuard, CartNotEmptyGuard],
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
