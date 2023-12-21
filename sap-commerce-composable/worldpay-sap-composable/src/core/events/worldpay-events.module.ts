import { NgModule } from '@angular/core';
import { WorldpayEventsListener } from './worldpay-events.listener';

@NgModule({})
export class WorldpayEventsModule {
  constructor(
    _googlepayEventsListener: WorldpayEventsListener,
  ) {
    // Intentional empty constructor
  }
}
