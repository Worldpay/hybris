import { inject, Injectable } from '@angular/core';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { Address, Command, CommandService, CommandStrategy, EventService, LoggerService, OCC_USER_ID_ANONYMOUS, QueryService, Region, UserIdService } from '@spartacus/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { distinctUntilChanged, map, switchMap, take, tap } from 'rxjs/operators';
import { WorldpayConnector } from '../../connectors';
import { WorldpayBillingAddressSameAsDeliveryAddressSetEvent, WorldpayBillingAddressUpdatedEvent } from '../../events';
import { WorldpayBillingAddressFormFacade } from '../../facade';

@Injectable({
  providedIn: 'root'
})
export class WorldpayBillingAddressFormService extends CheckoutBillingAddressFormService implements WorldpayBillingAddressFormFacade {
  /** Observable to hold the current billing address */
  public billingAddress$: BehaviorSubject<Address> = new BehaviorSubject<Address>(undefined);
  public editModeStatus$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  /** Service to handle billing address operations */
  protected eventService: EventService = inject(EventService);
  protected logger: LoggerService = inject(LoggerService);
  protected queryService: QueryService = inject(QueryService);
  protected commandService: CommandService = inject(CommandService);
  protected userIdService: UserIdService = inject(UserIdService);
  protected activeCartFacade: ActiveCartFacade = inject(ActiveCartFacade);
  protected worldpayConnector: WorldpayConnector = inject(WorldpayConnector);

  /**
   * Command to update the delivery address.
   *
   * This command uses the `CommandService` to create a command that updates the delivery address
   * for the current user and cart. It dispatches an event upon successful update and logs any errors.
   * Function to execute the command.
   *
   * @param params - The parameters for the command.
   * @param params.addressId - The ID of the address to update.
   * @param params.deliveryAddress - The new delivery address.
   * @returns An observable that emits the updated address.
   * @since 2211.43.1
   */
  protected setDeliveryAddressAsBillingAddressCommand: Command<{ addressId: string, deliveryAddress: Address }, Address> =
    this.commandService.create<{ addressId: string, deliveryAddress: Address }, Address>(
      ({
        addressId,
        deliveryAddress
      }: { addressId: string, deliveryAddress: Address }): Observable<Address> =>
        this.checkoutPreconditions().pipe(
          switchMap(([userId, cartId]: [string, string]): Observable<Address> =>
            this.worldpayConnector.setDeliveryAddressAsBillingAddress(userId, cartId, addressId).pipe(
              tap((address: Address): void => {
                this.eventService.dispatch({
                  billingAddress: address,
                  deliveryAddress,
                }, WorldpayBillingAddressUpdatedEvent);
              }),
            )
          )
        ),
      {
        strategy: CommandStrategy.CancelPrevious,
      }
    );
  private _sameAsDeliveryAddress: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  constructor() {
    super();
    this.billingAddress$.pipe(
      distinctUntilChanged(),
      tap((address: Address): void => {
        this.billingAddress = address;
      }),
    ).subscribe();
  }

  /**
   * Retrieves the "same as delivery address" state as an observable.
   *
   * This method returns an observable that emits the current state of whether
   * the billing address is the same as the delivery address.
   *
   * @returns An observable that emits a boolean value indicating the state.
   * @since 2211.43.0
   */
  public getSameAsDeliveryAddress(): Observable<boolean> {
    return this._sameAsDeliveryAddress.asObservable();
  }

  /**
   * Retrieves the current billing address.
   *
   * This method returns the current billing address stored in the `billingAddress` property.
   *
   * @returns The current billing address.
   * @since 2211.43.0
   */
  override getBillingAddress(): Address {
    if (this._sameAsDeliveryAddress.value) {
      return this.billingAddress;
    }
    return this.getBillingAddressForm().value;
  }

  /**
   * Sets the billing address and updates the "same as delivery address" state.
   *
   * This method updates the `sameAsDeliveryAddress` state based on whether the billing
   * address matches the delivery address. It also sets the billing address in the form.
   *
   * @param billingAddress - The billing address to be set.
   * @param deliveryAddress - The delivery address to compare with the billing address (optional).
   * @since 2211.43.0
   */
  public setBillingAddress(billingAddress: Address, deliveryAddress?: Address): void {
    this.setSameAsDeliveryAddress(this.isBillingAddressSameAsDeliveryAddress(), deliveryAddress);
    this.setDeliveryAddressAsBillingAddress(billingAddress);
  }

  /**
   * Compares two addresses to determine if they are the same.
   *
   * This method checks if the provided billing address and delivery address are identical.
   * It first verifies that both addresses are defined. If the `id` properties of the addresses
   * match, it returns `true`. Otherwise, it compares all properties of the addresses, including
   * nested `region` and `country` objects, to ensure they are equal.
   *
   * @param {Address} billingAddress - The billing address to compare.
   * @param {Address} deliveryAddress - The delivery address to compare.
   * @returns {boolean} - A boolean indicating whether the two addresses are the same.
   * @since 2211.43.0
   */
  public compareAddresses(billingAddress: Address, deliveryAddress: Address): boolean {
    if (!billingAddress || !deliveryAddress) return false;

    if (billingAddress?.id === deliveryAddress?.id) {
      return true;
    }

    // Create copies to avoid mutating original objects
    const fieldsToExclude: Set<string> = new Set([
      'billingAddress',
      'defaultAddress',
      'email',
      'formattedAddress',
      'id',
      'shippingAddress',
      'title',
      'titleCode',
      'visibleInAddressBook'
    ]);

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const cleanBillingAddress: Record<string, any> = Object.keys(billingAddress)
      .filter((key: string): boolean => !fieldsToExclude.has(key))
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      .reduce((acc: Record<string, any>, key: string): Record<string, any> => {
        acc[key] = billingAddress[key as keyof typeof billingAddress];
        return acc;
      }, {});

    return Object.keys(cleanBillingAddress).every((key: string): boolean => {
      if (!(key in deliveryAddress)) return false;

      // Compare region objects
      if (key === 'region') {
        const billingRegion: Region = cleanBillingAddress[key];
        const deliveryRegion: Region = deliveryAddress[key];

        if (billingRegion == null && deliveryRegion == null) {
          return true;
        }

        if (billingRegion == null || deliveryRegion == null ||
            typeof billingRegion !== 'object' ||
            typeof deliveryRegion !== 'object') {
          return false;
        }

        const {
          isocode = '',
          isocodeShort = ''
        }: Region = cleanBillingAddress[key];
        const {
          isocode: iso2,
          isocodeShort: isoShort2
        }: Region = deliveryAddress[key];

        return (
          isocode === iso2 ||
          isocode === isoShort2 ||
          isocodeShort === iso2 ||
          isocodeShort === isoShort2
        );
      }

      // Compare country objects
      if (key === 'country' && typeof cleanBillingAddress[key] === 'object') {
        const {
          isocode,
          name
        }: Region = cleanBillingAddress[key];
        const {
          isocode: iso2,
          name: name2
        }: Region = deliveryAddress[key];

        return isocode === iso2 && name === name2;
      }

      // Compare primitive values
      return cleanBillingAddress[key as keyof typeof billingAddress] === deliveryAddress[key as keyof typeof deliveryAddress];
    });
  }

  /**
   * Checks if the billing address is the same as the delivery address.
   *
   * This method returns the current value of the `_sameAsDeliveryAddress` observable,
   * indicating whether the billing address matches the delivery address.
   *
   * @returns A boolean value indicating if the billing address is the same as the delivery address.
   * @since 2211.43.0
   */
  public override isBillingAddressSameAsDeliveryAddress(): boolean {
    return this._sameAsDeliveryAddress.value;
  }

  /**
   * Updates the "same as delivery address" state.
   *
   * This method sets the `_sameAsDeliveryAddress` observable to the provided value.
   * If the value is `true`, it dispatches an event to indicate that the billing address
   * is the same as the delivery address.
   *
   * @param value - A boolean indicating whether the billing address is the same as the delivery address.
   * @param deliveryAddress - The delivery address to associate with the billing address (optional).
   * @since 2211.43.0
   */
  public setSameAsDeliveryAddress(value: boolean, deliveryAddress?: Address): void {
    this._sameAsDeliveryAddress.next(value);

    if (value) {
      this.setDeliveryAddressAsBillingAddress(deliveryAddress);
      this.eventService.dispatch(
        {
          billingAddress: deliveryAddress,
          deliveryAddress: undefined
        },
        WorldpayBillingAddressSameAsDeliveryAddressSetEvent
      );
    }
  }

  /**
   * Sets the delivery address as the billing address.
   *
   * This method updates the `billingAddress$` observable with the provided address.
   * If the address is defined, it patches the billing address form with the new address.
   * Otherwise, it resets the billing address form.
   *
   * @param address - The address to set as the billing address, or `undefined` to reset the form.
   * @since 2211.43.0
   */
  override setDeliveryAddressAsBillingAddress(address: Address | undefined): void {
    const billingAddress: Address = this.isBillingAddressSameAsDeliveryAddress() ? undefined : address;
    this.billingAddress$.next(billingAddress);

    if (this.billingAddress) {
      this.getBillingAddressForm()?.patchValue(this.billingAddress);
    } else {
      this.resetBillingAddressForm();
    }
  }

  /**
   * Updates the delivery address.
   *
   * Executes the `updateDeliveryAddressCommand` with the provided address and delivery address.
   * Subscribes to the command's observable to ensure the command is executed.
   *
   * @param address - The address to be updated.
   * @param deliveryAddress - The new delivery address.
   * @since 2211.43.0
   */
  public updateDeliveryAddress(address: Address, deliveryAddress: Address): void {
    this.setDeliveryAddressAsBillingAddressCommand.execute({
      addressId: address.id,
      deliveryAddress
    }).subscribe();
  }

  /**
   * Get the current toggle state as an observable
   */
  public isEditModeEnabled(): Observable<boolean> {
    return this.editModeStatus$.asObservable();
  }

  /**
   * Get the current toggle state as an observable
   */
  public isEditModeEnabledValue(): boolean {
    return this.editModeStatus$.value;
  }

  /**
   * Toggle the state (switch between true and false)
   */
  public toggleEditMode(): void {
    this.editModeStatus$.next(!this.editModeStatus$.value);
  }

  /**
   * Sets the edit mode toggle state.
   *
   * This method updates the `editModeStatus$` observable with the provided state,
   * which determines whether the edit mode is enabled or disabled.
   *
   * @param state - A boolean value indicating the desired edit mode state.
   * @since 2211.43.0
   */
  public setEditToggleState(state: boolean): void {
    this.editModeStatus$.next(state);
  }

  /**
   * Checks if the conditions for checkout are met
   * @since 2211.43.0
   * @returns Observable<[string, string]> - Observable with userId and cartId
   */
  checkoutPreconditions(): Observable<[string, string]> {
    return combineLatest([
      this.userIdService.takeUserId(),
      this.activeCartFacade.takeActiveCartId(),
      this.activeCartFacade.isGuestCart(),
    ]).pipe(
      take(1),
      map(([userId, cartId, isGuestCart]: [string, string, boolean]): [string, string] => {
        if (
          !userId ||
          !cartId ||
          (userId === OCC_USER_ID_ANONYMOUS && !isGuestCart)
        ) {
          throw new Error('Checkout conditions not met');
        }
        return [userId, cartId];
      })
    );
  }

  /**
   * Resets the billing address form.
   *
   * This method clears the current values in the billing address form
   * and sets the `billingAddress$` observable to `undefined`.
   * @since 2211.43.0
   */
  resetBillingAddressForm(): void {
    this.getBillingAddressForm()?.reset();
    this.billingAddress$.next(undefined);
  }

  updateSameAsDeliveryAddressFormData(billingAddress: Address, deliveryAddress: Address): void {
    const areAddressesSame: boolean = this.compareAddresses(billingAddress, deliveryAddress);
    this.setSameAsDeliveryAddress(areAddressesSame);
    this.setBillingAddress(billingAddress, deliveryAddress);
  }
}
