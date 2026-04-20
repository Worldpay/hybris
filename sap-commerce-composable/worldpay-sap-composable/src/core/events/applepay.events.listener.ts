import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EventService } from '@spartacus/core';
import { OrderPlacedEvent } from '@spartacus/order/root';
import { WorldpayApplepayService } from '../services';

/**
 * Worldpay Applepay Event Listener.
 */
@Injectable({
  providedIn: 'root',
})
export class WorldpayApplepayEventListener {
  protected destroyRef: DestroyRef = inject(DestroyRef);

  /**
   * Constructs the `WorldpayApplepayEventListener` and initializes event listeners.
   *
   * This constructor sets up the listener for the `OrderPlacedEvent` to reset the Apple Pay session.
   *
   * @param {EventService} eventService - The event service used to handle events.
   * @param {WorldpayApplepayService} worldpayApplepayService - Service for managing Apple Pay functionality.
   */
  constructor(
    protected eventService: EventService,
    protected worldpayApplepayService: WorldpayApplepayService
  ) {
    this.resetApplepaySessionEvent();
  }

  /**
   * Resets the Apple Pay session when the `OrderPlacedEvent` is triggered.
   *
   * This method listens for the `OrderPlacedEvent` and clears the Apple Pay
   * merchant session and payment authorization by setting them to `null` in their
   * respective observables.
   *
   * The `takeUntilDestroyed` operator ensures that the subscription is automatically
   * unsubscribed when the `DestroyRef` is destroyed, preventing memory leaks.
   */
  resetApplepaySessionEvent(): void {
    this.eventService.get(OrderPlacedEvent).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (): void => {
        this.worldpayApplepayService.merchantSession$.next(null);
        this.worldpayApplepayService.paymentAuthorization$.next(null);
      }
    });
  }
}
