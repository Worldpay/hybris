import { Injectable, OnDestroy } from '@angular/core';
import { EventService } from '@spartacus/core';
import { merge, Subscription } from 'rxjs';
import { ClearWorldpayPaymentDetailsEvent, ClearWorldpayPaymentStateEvent, SetWorldpaySaveAsDefaultCreditCardEvent, SetWorldpaySavedCreditCardEvent } from './worldpay.events';
import { ClearGooglepayEvent } from './googlepay.events';

/**
 * Checkout payment event listener.
 */
@Injectable({
  providedIn: 'root',
})
export class WorldpayEventsListener implements OnDestroy {
  protected subscriptions: Subscription = new Subscription();

  /**
   * Constructor for WorldpayEventsListener.
   * @param {EventService} eventService - The event service used to handle events.
   * @since 6.4.0
   */
  constructor(
    protected eventService: EventService,
  ) {
    this.onOrderPlacedEvent();
  }

  /**
   * Handles the order placed event by subscribing to the ClearWorldpayPaymentStateEvent.
   * Dispatches events to clear Google Pay, saved credit card, save as default credit card,
   * and Worldpay payment details.
   * @protected
   * @since 6.4.0
   */
  protected onOrderPlacedEvent(): void {
    this.subscriptions.add(
      merge(this.eventService.get(ClearWorldpayPaymentStateEvent)).subscribe({
        next: (): void => {
          this.eventService.dispatch(ClearGooglepayEvent);
          this.eventService.dispatch({ saved: false }, SetWorldpaySavedCreditCardEvent);
          this.eventService.dispatch({ saved: false }, SetWorldpaySaveAsDefaultCreditCardEvent);
          this.eventService.dispatch(ClearWorldpayPaymentDetailsEvent);
        }
      }),
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
