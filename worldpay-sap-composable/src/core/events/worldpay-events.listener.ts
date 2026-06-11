import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EventService } from '@spartacus/core';
import { OrderPlacedEvent } from '@spartacus/order/root';
import { WorldpayClearBillingAddressFormEvent } from './billing-address-form.events';
import { ClearGooglepayEvent } from './googlepay.events';
import { ClearWorldpayPaymentDetailsEvent, ClearWorldpayPaymentStateEvent, SetWorldpaySaveAsDefaultCreditCardEvent, SetWorldpaySavedCreditCardEvent } from './worldpay.events';

/**
 * Checkout payment event listener.
 */
@Injectable({
  providedIn: 'root',
})
export class WorldpayEventsListener {
  protected destroyRef: DestroyRef = inject(DestroyRef);
  protected eventService: EventService = inject(EventService);
  /**
   * Constructor for WorldpayEventsListener.
   * @since 6.4.0
   */
  constructor() {
    this.onOrderPlacedEvent();
    this.onClearWorldpayPaymentStateEvent();
  }


  /**
   * Handles the order placed event by subscribing to the ClearWorldpayPaymentStateEvent.
   * Dispatches events to clear Google Pay, saved credit card, save as default credit card,
   * and Worldpay payment details.
   * @protected
   * @since 6.4.0
   */
  protected onOrderPlacedEvent(): void {
    this.eventService.get(OrderPlacedEvent).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (): void => {
        this.eventService.dispatch({}, ClearWorldpayPaymentStateEvent);
      }
    });
  }

  protected onClearWorldpayPaymentStateEvent(): void {
    this.eventService.get(ClearWorldpayPaymentStateEvent).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (): void => {
        this.eventService.dispatch({}, ClearGooglepayEvent);
        this.eventService.dispatch({ saved: false }, SetWorldpaySavedCreditCardEvent);
        this.eventService.dispatch({ saved: false }, SetWorldpaySaveAsDefaultCreditCardEvent);
        this.eventService.dispatch({},  ClearWorldpayPaymentDetailsEvent);
        this.eventService.dispatch({}, WorldpayClearBillingAddressFormEvent);
      }
    });
  }
}
