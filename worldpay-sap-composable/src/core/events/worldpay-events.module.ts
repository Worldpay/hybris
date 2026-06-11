/* eslint-disable @typescript-eslint/no-unused-vars */
import { NgModule } from '@angular/core';
import { WorldpayBillingAddressFormEventsListener } from './billing-address-form.events.listener';
import { WorldpayEventsListener } from './worldpay-events.listener';

/**
 * Angular module for Worldpay event listeners.
 *
 * This module ensures that all Worldpay-related event listeners are instantiated
 * and available for dependency injection. The constructor intentionally injects
 * all event listener services to guarantee their initialization and side effects.
 *
 * @remarks
 * - The constructor parameters are not used directly, but their injection ensures
 *   that listeners are registered and active in the application lifecycle.
 * - No providers or imports are declared in the NgModule, as listeners are provided elsewhere.
 *
 * @since 221121.11.0
 */
@NgModule({})
export class WorldpayEventsModule {
  /**
   * Instantiates all Worldpay event listeners for the application.
   *
   * @param _worldpayEventsListener - Listener for generic Worldpay events
   * @param _worldpayBillingAddressFormEventsListener - Listener for billing address form events
   */
  constructor(
    _worldpayEventsListener: WorldpayEventsListener,
    _worldpayBillingAddressFormEventsListener: WorldpayBillingAddressFormEventsListener,
  ) {
    // Intentional empty constructor
  }
}
