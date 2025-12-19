import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EventService } from '@spartacus/core';
import { WorldpayGooglepayService } from 'worldpay-sap-composable-services';
import { ClearGooglepayEvent, GooglePayMerchantConfigurationSetEvent } from './googlepay.events';

/**
 * Checkout payment event listener.
 */
@Injectable({
  providedIn: 'root',
})
export class WorldpayGooglepayEventListener {
  protected destroyRef: DestroyRef = inject(DestroyRef);

  /**
   * Initializes the `WorldpayGooglepayEventListener` by setting up event listeners.
   *
   * This constructor sets up listeners for the following events:
   * - `onSameAsBillingAddressChange`: Handles changes to the "Same as Billing Address" option.
   * - `resetGooglePaySessionEvent`: Resets the Google Pay session when the corresponding event is triggered.
   * - `onWorldpayClearBillingAddressFormEvent`: Handles clearing the billing address form.
   * - `onCurrencyChangeEvent`: Handles changes to the currency.
   *
   * @param {EventService} eventService - The event service used to handle events.
   * @param {WorldpayGooglepayService} worldpayGooglepayService - Service for managing Google Pay functionality.
   */
  constructor(
    protected eventService: EventService,
    protected worldpayGooglepayService: WorldpayGooglepayService
  ) {
    this.onGooglePayMerchantConfigurationSetEvent();
    this.resetGooglePaySessionEvent();
  }

  /**
   * Resets the Google Pay session when the `ClearGooglepayEvent` is triggered.
   *
   * This method listens for the `ClearGooglepayEvent` and clears the Google Pay
   * merchant configuration by setting it to `null` in the `googlePayMerchantConfiguration$` observable.
   *
   * The `takeUntilDestroyed` operator ensures that the subscription is automatically
   * unsubscribed when the `DestroyRef` is destroyed, preventing memory leaks.
   */
  resetGooglePaySessionEvent(): void {
    this.eventService.get(ClearGooglepayEvent).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (): void => {
        this.worldpayGooglepayService.setGooglepayMerchantConfiguration(null);
      }
    });
  }

  /**
   * Listen to GooglePayMerchantConfigurationSetEvent
   * @since 6.4.0
   */
  protected onGooglePayMerchantConfigurationSetEvent(): void {
    this.eventService.get(GooglePayMerchantConfigurationSetEvent).subscribe({
      next: (event: GooglePayMerchantConfigurationSetEvent): void => {
        this.worldpayGooglepayService.setGooglepayMerchantConfiguration(event.googlePayMerchantConfiguration);
      }
    });
  }
}
