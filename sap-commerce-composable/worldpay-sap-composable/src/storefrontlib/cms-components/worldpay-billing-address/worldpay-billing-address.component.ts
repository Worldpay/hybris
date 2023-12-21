import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { FormControl, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { Address, Country, QueryState, Region, UserAddressService, UserPaymentService } from '@spartacus/core';
import { Card } from '@spartacus/storefront';
import { map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { CheckoutDeliveryAddressFacade } from '@spartacus/checkout/base/root';
import { generateAddressCard } from '../../../core/utils/format-address';

@Component({
  selector: 'y-worldpay-billing-address',
  templateUrl: './worldpay-billing-address.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorldpayBillingAddressComponent implements OnInit, OnDestroy {
  public showSameAsDeliveryAddressCheckbox$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  public showSameAsShippingAddressCheckbox$: Observable<boolean>;
  public countries$: Observable<Country[]>;
  public regions$: Observable<Region[]>;
  public deliveryAddress$: Observable<QueryState<Address | undefined>>;
  private drop: Subject<void> = new Subject<void>();
  jpLabel: string = '';
  @Input() billingAddressForm: UntypedFormGroup = new UntypedFormGroup({});
  @Output() sameAsShippingAddressChange: Observable<boolean> = this.showSameAsDeliveryAddressCheckbox$.asObservable();

  /**
   * Constructor
   * @param userAddressService UserAddressService
   * @param userPaymentService UserPaymentService
   * @param checkoutDeliveryAddressFacade CheckoutDeliveryAddressFacade
   * @param fb UntypedFormBuilder
   */
  constructor(
    protected userAddressService: UserAddressService,
    protected userPaymentService: UserPaymentService,
    protected checkoutDeliveryAddressFacade: CheckoutDeliveryAddressFacade,
    protected fb: UntypedFormBuilder,
  ) {
  }

  /**
   * Getter for the line2 field
   * @since 6.4.0
   * @returns FormControl - line2 field
   */
  get line2Field() {
    return this.billingAddressForm.get('line2') as FormControl;
  }

  ngOnInit(): void {
    this.buildForm();
    this.countries$ = this.userPaymentService.getAllBillingCountries().pipe(
      tap((countries: Country[]): void => {
        // If the store is empty fetch countries. This is also used when changing language.
        if (Object.keys(countries).length === 0) {
          this.userPaymentService.loadBillingCountries();
        }
      })
    );

    this.deliveryAddress$ = this.checkoutDeliveryAddressFacade.getDeliveryAddressState();

    this.regions$ = this.billingAddressForm.get('country.isocode').valueChanges.pipe(
      switchMap((country) => this.userAddressService.getRegions(country)),
      tap((regions: Region[]): void => {
        const regionControl = this.billingAddressForm.get('region.isocode');
        if (regions.length > 0) {
          regionControl.enable();
        } else {
          regionControl.disable();
        }
      })
    );

    this.showSameAsShippingAddressCheckbox$ = this.deliveryAddress$.pipe(map((address: QueryState<Address>) => !!address?.data));
  }

  /**
   * Get region binding label
   * @since 6.4.0
   * @param regions - Region[]
   * @returns string - region binding label
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
   * Get region binding value
   * @param regions - Region[]
   * @returns string - region binding value
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
   * Get address card content
   * @param address - Address
   * @returns Card - Card
   */
  getAddressCardContent(address: Address): Card {
    return generateAddressCard(address);
  }

  /**
   * Build Form
   * @since 4.3.6
   */
  private buildForm(): void {
    if (!this.billingAddressForm) {
      this.billingAddressForm = this.fb.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        line1: ['', Validators.required],
        line2: [''],
        town: ['', Validators.required],
        region: this.fb.group({
          isocode: [null, Validators.required],
        }),
        country: this.fb.group({
          isocode: [null, Validators.required],
        }),
        postalCode: ['', Validators.required],
      });
    } else {
      this.billingAddressForm.setControl('firstName', new UntypedFormControl('', Validators.required));
      this.billingAddressForm.setControl('lastName', new UntypedFormControl('', Validators.required));
      this.billingAddressForm.setControl('line1', new UntypedFormControl('', Validators.required));
      this.billingAddressForm.setControl('line2', new UntypedFormControl(''));
      this.billingAddressForm.setControl('town', new UntypedFormControl('', Validators.required));
      this.billingAddressForm.setControl('region', this.fb.group({
        isocode: [null, Validators.required],
      }));
      this.billingAddressForm.setControl('country', this.fb.group({
        isocode: [null, Validators.required],
      }));
      this.billingAddressForm.setControl('postalCode', new UntypedFormControl('', Validators.required));
    }
    this.billingAddressForm.get('country').valueChanges.pipe(
      takeUntil(this.drop)
    ).subscribe({
      next: (value): void => {
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

  ngOnDestroy(): void {
    this.drop.next();
    this.drop.complete();
  }
}
