import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, ViewEncapsulation } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AbstractControl, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgSelectComponent } from '@ng-select/ng-select';
import { CheckoutBillingAddressFormComponent } from '@spartacus/checkout/base/components';
import { Address, Country, LoggerService, QueryState, Region, TranslatePipe } from '@spartacus/core';
import { CardComponent, FormErrorsComponent, FormRequiredAsterisksComponent, FormRequiredLegendComponent, NgSelectA11yDirective } from '@spartacus/storefront';
import { combineLatest, Observable, of } from 'rxjs';
import { catchError, distinctUntilChanged, filter, map, switchMap, take, tap } from 'rxjs/operators';
import { WorldpayApmPaymentInfo, WorldpayBillingAddressFormService, WorldpayCheckoutPaymentFacade } from '../../../core';

@Component({
  selector: 'y-worldpay-billing-address',
  templateUrl: './worldpay-billing-address.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  imports: [
    CardComponent,
    ReactiveFormsModule,
    FormRequiredLegendComponent,
    FormRequiredAsterisksComponent,
    NgSelectComponent,
    NgSelectA11yDirective,
    FormErrorsComponent,
    AsyncPipe,
    TranslatePipe,
  ],
})
export class WorldpayBillingAddressComponent extends CheckoutBillingAddressFormComponent implements OnInit {
  public jpLabel: string = '';
  public processing$: Observable<boolean>;
  protected destroyRef: DestroyRef = inject(DestroyRef);
  protected override billingAddressFormService: WorldpayBillingAddressFormService = inject(WorldpayBillingAddressFormService);
  public override sameAsDeliveryAddress: boolean = this.billingAddressFormService.isBillingAddressSameAsDeliveryAddress();
  protected logger: LoggerService = inject(LoggerService);
  protected deliveryAddress: Address = this.billingAddressFormService.getBillingAddress();
  protected showSameAsDeliveryAddressCheckbox: boolean;
  protected billingAddress$: Observable<Address> = this.billingAddressFormService.billingAddress$.asObservable();
  protected worldpayPaymentFacade: WorldpayCheckoutPaymentFacade = inject(WorldpayCheckoutPaymentFacade);

  /**
   * Getter for the line2 field
   * @since 6.4.0
   * @returns FormControl - line2 field
   */
  get line2Field(): FormControl {
    return this.billingAddressForm.get('line2') as FormControl;
  }

  override ngOnInit(): void {
    // OOTB Functionality splited for better customization
    this.getAllBillingCountries();
    this.getDeliveryAddressState();
    this.bindDeliveryAddressCheckbox();
    this.bindRegionsChanges();
    this.bindLoadingState();
    this.buildForm();
    this.bindSameAsDeliveryAddressCheckbox();
    this.updateBillingAddressForm();
  }

  /**
   * Fetches all billing countries and assigns them to the `countries$` observable.
   * If the store is empty, it triggers the loading of billing countries.
   * This method is also used when changing the language.
   *
   * @return {void}
   * @since 2211.43.0
   */
  getAllBillingCountries(): void {
    this.countries$ = this.userPaymentService.getAllBillingCountries().pipe(
      tap((countries: Country[]): void => {
        // If the store is empty fetch countries. This is also used when changing language.
        if (Object.keys(countries).length === 0) {
          this.userPaymentService.loadBillingCountries();
        }
      })
    );
  }

  /**
   * Binds the visibility of the "same as delivery address" checkbox to the presence of a delivery address
   * and whether the delivery address country matches one of the billing countries.
   *
   * @return {void}
   * @since 2211.43.1
   */
  bindDeliveryAddressCheckbox(): void {
    this.showSameAsDeliveryAddressCheckbox$ = combineLatest([
      this.countries$,
      this.deliveryAddress$
    ]).pipe(
      map(([countries, address]: [Country[], Address]): boolean =>
        (address?.country && !!countries.filter((country: Country): boolean => country.isocode === address.country?.isocode).length) ?? false),
      tap((showCheckbox: boolean): void => {
        this.showSameAsDeliveryAddressCheckbox = showCheckbox;
      })
    );
  }

  /**
   * Binds changes to the regions based on the selected country in the billing address form.
   * Sets up an observable that listens for changes to the 'country.isocode' form control.
   * When the country changes, it fetches the corresponding regions and enables or disables the 'region.isocode' form control based on the presence of regions.
   *
   * @return {void}
   * @since 2211.43.0
   */
  bindRegionsChanges(): void {
    this.regions$ = this.selectedCountry$.pipe(
      switchMap((country: string): Observable<Region[]> => this.userAddressService.getRegions(country)),
      tap((regions: Region[]): void => {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        const regionControl: AbstractControl<any, any> = this.billingAddressForm.get(
          'region.isocodeShort'
        );
        if (regions.length > 0) {
          regionControl?.enable();
        } else {
          regionControl?.disable();
        }
      })
    );
  }

  /**
   * Binds the loading state of the payment details to the `processing$` observable.
   * This method listens for changes in the payment details state and updates the `processing$` observable
   * with the loading status.
   *
   * @return {void}
   * @since 2211.43.1
   */
  bindLoadingState(): void {
    this.processing$ = this.worldpayPaymentFacade.getPaymentDetailsState().pipe(
      map((state: QueryState<WorldpayApmPaymentInfo | undefined>): boolean => state?.loading ?? false),
      distinctUntilChanged()
    );
  }

  /**
   * Toggles the "same as delivery address" state and updates the billing address form service
   * with the current state of the "same as delivery address" checkbox and the delivery address.
   *
   * @override
   * @return {void}
   * @since 2211.43.1
   */
  override toggleSameAsDeliveryAddress(): void {
    this.sameAsDeliveryAddress = !this.sameAsDeliveryAddress;
    this.billingAddressFormService.setSameAsDeliveryAddress(this.sameAsDeliveryAddress, this.deliveryAddress);
  }

  /**
   * Returns the appropriate binding label for regions based on the available properties.
   * If the regions array is not empty, it checks the first region object for the presence of 'name' or 'isocodeShort' properties.
   * If 'name' is present, it returns 'name'. If 'isocodeShort' is present, it returns 'isocodeShort'.
   * If neither is present, or if the regions array is empty, it defaults to returning 'isocode'.
   *
   * @param {Region[]} regions - The array of region objects to check.
   * @return {string} - The binding label for the regions.
   * @since 4.2.7
   */
  getRegionBindingLabel(regions: Region[]): string {
    if (regions?.length) {
      if (regions[0].name) {
        return 'name';
      }
      if (regions[0].isocodeShort) {
        return 'isocodeShort';
      }
    }
    return 'isocode';
  }

  /**
   * Returns the appropriate binding value for regions based on the available properties.
   * If the regions array is not empty, it checks the first region object for the presence of 'isocode' or 'isocodeShort' properties.
   * If 'isocode' is present, it returns 'isocode'. If 'isocodeShort' is present, it returns 'isocodeShort'.
   * If neither is present, or if the regions array is empty, it defaults to returning 'isocode'.
   *
   * @param {Region[]} regions - The array of region objects to check.
   * @return {string} - The binding value for the regions.
   * @since 4.2.7
   */
  getRegionBindingValue(regions: Region[]): string {
    if (regions?.length) {
      if (regions[0].isocode) {
        return 'isocode';
      }
      if (regions[0].isocodeShort) {
        return 'isocodeShort';
      }
    }
    return 'isocode';
  }

  /**
   * Binds the "same as delivery address" checkbox state to the comparison of the delivery address and billing address.
   * If the billing address is not set, the checkbox will be checked by default.
   * If the billing address is set, the checkbox will be checked if the billing address ID matches the delivery address ID.
   *
   * @return {void}
   * @since 2211.43.1
   */
  bindSameAsDeliveryAddressCheckbox(): void {
    combineLatest([
      this.deliveryAddress$,
      this.billingAddress$
    ]).pipe(
      map(([deliveryAddress, billingAddress]: [Address, Address]): boolean => {
        if (!billingAddress) {
          return true;
        }
        return this.billingAddressFormService.compareAddresses(billingAddress, deliveryAddress);
      }),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe({
      next: (isSameAsDeliveryAddress: boolean): void => {
        this.sameAsDeliveryAddress = isSameAsDeliveryAddress;
      }
    });
  }

  /**
   * Fetches the delivery address state and assigns it to the `shippingAddress$` observable.
   * Maps the `QueryState<Address>` to the `Address` data.
   *
   * @return {void}
   * @since 2211.43.0
   */
  getDeliveryAddressState(): void {
    this.deliveryAddress$ = this.checkoutDeliveryAddressFacade.getDeliveryAddressState().pipe(
      filter((state: QueryState<Address>): boolean => !state.loading),
      map((state: QueryState<Address>): Address => {
        this.deliveryAddress = state.data;
        return this.deliveryAddress;
      }),
      catchError((error: unknown): Observable<unknown> => {
        this.logger.error('Error fetching delivery address', { error });
        return of(error);
      })
    );
  }

  /**
   * Updates the billing address form with the fetched regions based on the selected country.
   * It listens for changes in the billing address observable, fetches the regions for the selected country,
   * and updates the billing address form with the fetched regions.
   *
   * @return {void}
   * @since 2211.43.1
   */
  updateBillingAddressForm(): void {
    this.billingAddress$.pipe(
      filter((billingAddress: Address): boolean => !!billingAddress),
      switchMap((billingAddress: Address): Observable<Address> =>
        this.userAddressService.getRegions(billingAddress?.country?.isocode).pipe(
          map((regions: Region[]): Address => {
            if (regions.length > 0) {
              billingAddress.region = {
                isocodeShort: billingAddress?.region?.isocode || billingAddress?.region?.isocodeShort
              };
            }
            return billingAddress;
          }),
        )
      ),
      take(1),
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (billingAddress: Address): void => {
        if (billingAddress) {
          this.countrySelected(billingAddress?.country);
          this.billingAddressForm.patchValue(billingAddress);
        }
      }
    });
  }

  /**
   * Build Form
   * @since 4.3.6
   */
  private buildForm(): void {
    this.billingAddressForm = this.billingAddressFormService.getBillingAddressForm();
    this.billingAddressForm.get('country')?.valueChanges?.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      next: (value: any): void => {
        if (value?.isocode?.toLowerCase() === 'jp') {
          this.line2Field.setValidators(Validators.required);
          this.jpLabel = '.jp';
        } else {
          this.line2Field.clearValidators();
          this.jpLabel = '';
        }

        this.line2Field.updateValueAndValidity();
      }
    });
  }
}
