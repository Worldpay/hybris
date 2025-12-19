/* eslint-disable @typescript-eslint/no-unused-vars */
import { NgModule } from '@angular/core';
import { WorldpayApplepayEventListener } from './applepay.events.listener';
import { WorldpayBillingAddressFormEventsListener } from './billing-address-form.events.listener';
import { WorldpayGooglepayEventListener } from './googlepay.events.listener';
import { WorldpayEventsListener } from './worldpay-events.listener';

@NgModule({})
export class WorldpayEventsModule {
  constructor(
    _worldpayEventsListener: WorldpayEventsListener,
    _googlepayEventsListener: WorldpayGooglepayEventListener,
    _applepayEventsListener: WorldpayApplepayEventListener,
    _worldpayBillingAddressFormEventsListener: WorldpayBillingAddressFormEventsListener,
  ) {
    // Intentional empty constructor
  }
}
