import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { WorldpayCheckoutScheduledReplenishmentPlaceOrderModule } from './checkout-place-order/worldpay-checkout-place-order.module';
import { WorldpayCheckoutScheduleReplenishmentOrderModule } from './checkout-schedule-replenishment-order';

@NgModule({
  imports: [
    CommonModule,
    WorldpayCheckoutScheduledReplenishmentPlaceOrderModule,
    WorldpayCheckoutScheduleReplenishmentOrderModule
  ],
})
export class WorldpayCheckoutScheduledReplenishmentComponentsModule {
}
