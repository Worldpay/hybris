/* eslint-disable @typescript-eslint/no-unused-vars */
import { NgModule } from '@angular/core';
import { WorldpayBillingAddressFormEventsListener } from './billing-address-form.events.listener';
import { WorldpayEventsListener } from './worldpay-events.listener';

@NgModule({})
export class WorldpayEventsModule {
  constructor(
    _worldpayEventsListener: WorldpayEventsListener,
    _worldpayBillingAddressFormEventsListener: WorldpayBillingAddressFormEventsListener,
  ) {
    // Intentional empty constructor
  }
}
