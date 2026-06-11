import { NgModule } from '@angular/core';
import { WorldpayEventsModule } from '../../core/events';
import { Worldpay3dsChallengeIframeModule, WorldpayDdcIframeModule, WorldpayDdcIframeRoutingModule } from '../pages';
import { WorldpayCartSharedModule } from './worldpay-cart-shared/worldpay-cart-shared.module';
import { WorldpayCheckoutDeliveryAddressModule } from './worldpay-checkout-delivery-address/worldpay-checkout-delivery-address.module';
import { WorldpayCheckoutPaymentMethodModule } from './worldpay-checkout-payment-method/worldpay-checkout-payment-method.module';
import { WorldpayCheckoutPlaceOrderModule } from './worldpay-checkout-place-order/worldpay-checkout-place-order.module';
import { WorldpayCheckoutReviewPaymentModule } from './worldpay-checkout-review/worldpay-checkout-review-payment/worldpay-checkout-review-payment.module';
import { WorldpayOrderConfirmationModule } from './worldpay-order-confirmation/worldpay-order-confirmation.module';
import { WorldpayOrderDetailsModule } from './worldpay-order-details/worldpay-order-details.module';
import { WorldpayPaymentMethodsModule } from './worldpay-payment-methods/worldpay-payment-methods.module';

@NgModule({
  imports: [
    WorldpayCheckoutPaymentMethodModule,
    WorldpayCheckoutDeliveryAddressModule,
    WorldpayDdcIframeModule,
    WorldpayDdcIframeRoutingModule,
    Worldpay3dsChallengeIframeModule,
    WorldpayCheckoutPlaceOrderModule,
    WorldpayCheckoutReviewPaymentModule,
    WorldpayCartSharedModule,
    WorldpayOrderConfirmationModule,
    WorldpayOrderDetailsModule,
    WorldpayEventsModule,
    WorldpayPaymentMethodsModule
  ]
})

export class WorldpayComponentsModule {
}
