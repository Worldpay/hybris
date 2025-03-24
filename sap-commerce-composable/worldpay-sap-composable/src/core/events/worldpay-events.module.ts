import { NgModule } from '@angular/core';
import { WorldpayEventsListener } from './worldpay-events.listener';

@NgModule({})
export class WorldpayEventsModule {
  constructor(
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    _googlepayEventsListener: WorldpayEventsListener,
  ) {
    // Intentional empty constructor
  }
}
