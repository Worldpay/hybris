import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CheckoutPaymentDetailsCreatedEvent } from '@spartacus/checkout/base/root';
import { CurrencySetEvent, EventService, LoginEvent, LogoutEvent } from '@spartacus/core';
import { OrderPlacedEvent } from '@spartacus/order/root';
import { merge } from 'rxjs';
import { WorldpayBillingAddressFormService } from 'worldpay-sap-composable-services';
import { WorldpayBillingAddressSameAsDeliveryAddressSetEvent, WorldpayClearBillingAddressFormEvent } from './billing-address-form.events';
import { WorldpayBillingAddressCreatedEvent, WorldpayBillingAddressUpdatedEvent } from './billing-address.events';
import { SetPaymentAddressEvent } from './checkout-payment.events';

/**
 * Checkout payment event listener.
 */
@Injectable({
  providedIn: 'root',
})
export class WorldpayBillingAddressFormEventsListener {
  protected destroyRef: DestroyRef = inject(DestroyRef);

  /**
   * Initializes the `WorldpayBillingAddressFormEventsListener` by setting up event listeners.
   *
   * This constructor sets up listeners for the following events:
   * - `WorldpayBillingAddressSameAsDeliveryAddressSetEvent`: Handles changes to the "Same as Billing Address" option.
   * - `WorldpayBillingAddressUpdatedEvent`: Handles updates to the Worldpay billing address.
   * - `WorldpayClearBillingAddressFormEvent`: Handles clearing the billing address form.
   * - `CurrencySetEvent`: Handles changes to the currency.
   *
   * @param {EventService} eventService - The event service used to handle events.
   * @param {WorldpayBillingAddressFormService} worldpayBillingAddressFormService - Service for managing the billing address form.
   * @since 2211.43.0
   */
  constructor(
    protected eventService: EventService,
    protected worldpayBillingAddressFormService: WorldpayBillingAddressFormService
  ) {
    this.onSameAsBillingAddressChange();
    this.onWorldpayBillingAddressUpdatedEvent();
    this.onWorldpayClearBillingAddressFormEvent();
    this.onCurrencyChangeEvent();
    this.onWorldpayBillingChangeEvent();
  }

  /**
   * Handles the event when the "Same as Billing Address" option changes.
   *
   * This method listens for the `WorldpayBillingAddressSameAsDeliveryAddressSetEvent`
   * and updates the billing address form state based on the presence of the delivery address.
   *
   * - If the `deliveryAddress` is `undefined`, the edit toggle state is disabled.
   * - If the `deliveryAddress` is defined, the delivery address is updated using the
   *   `WorldpayBillingAddressFormService`.
   *
   * The `takeUntilDestroyed` operator ensures that the subscription is automatically
   * unsubscribed when the `DestroyRef` is destroyed, preventing memory leaks.
   *
   * @protected
   * @since 2211.43.0
   */
  protected onSameAsBillingAddressChange(): void {
    this.eventService.get(WorldpayBillingAddressSameAsDeliveryAddressSetEvent).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: ({
        billingAddress,
        deliveryAddress
      }: WorldpayBillingAddressSameAsDeliveryAddressSetEvent): void => {
        if (deliveryAddress === undefined) {
          this.worldpayBillingAddressFormService.setEditToggleState(false);
        } else {
          this.worldpayBillingAddressFormService.updateDeliveryAddress(billingAddress, deliveryAddress);
        }
      }
    });
  }

  /**
   * Handles the event when the Worldpay billing address is updated.
   *
   * This method listens for the `WorldpayBillingAddressUpdatedEvent` and updates
   * the billing address in the `WorldpayBillingAddressFormService` with the provided
   * `billingAddress` and `deliveryAddress`.
   *
   * The `takeUntilDestroyed` operator ensures that the subscription is automatically
   * unsubscribed when the `DestroyRef` is destroyed, preventing memory leaks.
   *
   * @protected
   * @since 2211.43.0
   */
  protected onWorldpayBillingAddressUpdatedEvent(): void {
    this.eventService.get(WorldpayBillingAddressUpdatedEvent).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: ({
        billingAddress,
        deliveryAddress
      }: WorldpayBillingAddressUpdatedEvent): void => {
        this.worldpayBillingAddressFormService.setBillingAddress(billingAddress, deliveryAddress);
      }
    });
  }

  /**
   * Handles the event when the Worldpay billing address form needs to be cleared.
   *
   * This method listens for the `WorldpayClearBillingAddressFormEvent` and performs the following actions:
   * - Resets the billing address form using the `WorldpayBillingAddressFormService`.
   * - Disables the edit toggle state in the `WorldpayBillingAddressFormService`.
   *
   * The `takeUntilDestroyed` operator ensures that the subscription is automatically
   * unsubscribed when the `DestroyRef` is destroyed, preventing memory leaks.
   *
   * @protected
   * @since 2211.43.0
   */
  protected onWorldpayClearBillingAddressFormEvent(): void {
    merge(
      this.eventService.get(WorldpayClearBillingAddressFormEvent),
      this.eventService.get(LoginEvent),
      this.eventService.get(LogoutEvent),
      this.eventService.get(OrderPlacedEvent),
    )
      .pipe(
        takeUntilDestroyed(this.destroyRef)
      ).subscribe({
        next: (): void => {
          this.worldpayBillingAddressFormService.resetBillingAddressForm();
          this.worldpayBillingAddressFormService.setEditToggleState(false);
        }
      });
  }

  /**
   * Handles the event when the currency is changed.
   *
   * This method listens for the `CurrencySetEvent` and disables the edit toggle state
   * in the `WorldpayBillingAddressFormService` when the event is triggered.
   *
   * The `takeUntilDestroyed` operator ensures that the subscription is automatically
   * unsubscribed when the `DestroyRef` is destroyed, preventing memory leaks.
   *
   * @protected
   * @since 2211.43.0
   */
  protected onCurrencyChangeEvent(): void {
    this.eventService.get(CurrencySetEvent).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (): void => {
        this.worldpayBillingAddressFormService.setEditToggleState(false);
      }
    });
  }

  /**
   * Handles changes to the Worldpay billing address triggered by various events.
   *
   * This method listens for the following events:
   * - `CheckoutPaymentDetailsCreatedEvent`: Triggered when payment details are created during checkout.
   * - `WorldpayBillingAddressCreatedEvent`: Triggered when a new Worldpay billing address is created.
   * - `SetPaymentAddressEvent`: Triggered when the payment address is set.
   * - `WorldpayBillingAddressUpdatedEvent`: Triggered when the Worldpay billing address is updated.
   *
   * When any of these events occur, the billing address is updated in the
   * `WorldpayBillingAddressFormService` using the current billing address.
   *
   * The `takeUntilDestroyed` operator ensures that the subscription is automatically
   * unsubscribed when the `DestroyRef` is destroyed, preventing memory leaks.
   *
   * @protected
   * @since 2211.43.0
   */
  protected onWorldpayBillingChangeEvent(): void {
    merge(
      this.eventService.get(CheckoutPaymentDetailsCreatedEvent),
      this.eventService.get(WorldpayBillingAddressCreatedEvent),
      this.eventService.get(SetPaymentAddressEvent),
      this.eventService.get(WorldpayBillingAddressUpdatedEvent)
    ).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (): void => {
        this.worldpayBillingAddressFormService.setBillingAddress(this.worldpayBillingAddressFormService.getBillingAddress());
      }
    });
  }
}
