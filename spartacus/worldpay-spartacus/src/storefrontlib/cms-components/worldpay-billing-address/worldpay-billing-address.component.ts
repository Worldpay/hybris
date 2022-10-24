import { ChangeDetectionStrategy, Component, Input, OnInit, Output } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Address, Country, Region, UserAddressService, UserPaymentService } from '@spartacus/core';
import { Card } from '@spartacus/storefront';
import { map, switchMap, tap } from 'rxjs/operators';
import { CheckoutDeliveryService } from '@spartacus/checkout/core';

@Component({
  selector: 'y-worldpay-billing-address',
  templateUrl: './worldpay-billing-address.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorldpayBillingAddressComponent implements OnInit {
  public sameAsShippingAddress$ = new BehaviorSubject<boolean>(true);
  public showSameAsShippingAddressCheckbox$: Observable<boolean>;
  public countries$: Observable<Country[]>;
  public regions$: Observable<Region[]>;
  public shippingAddress$: Observable<Address>;

  @Input() billingAddressForm: FormGroup = new FormGroup({});
  @Output() sameAsShippingAddressChange = this.sameAsShippingAddress$.asObservable();

  constructor(
    protected userAddressService: UserAddressService,
    protected userPaymentService: UserPaymentService,
    protected checkoutDeliveryService: CheckoutDeliveryService,
    protected fb: FormBuilder,
  ) {
  }

  ngOnInit(): void {
    this.buildForm();
    this.countries$ = this.userPaymentService.getAllBillingCountries().pipe(
      tap((countries) => {
        // If the store is empty fetch countries. This is also used when changing language.
        if (Object.keys(countries).length === 0) {
          this.userPaymentService.loadBillingCountries();
        }
      })
    );

    this.shippingAddress$ = this.checkoutDeliveryService.getDeliveryAddress();

    this.regions$ = this.billingAddressForm.get('country.isocode').valueChanges.pipe(
      switchMap((country) => this.userAddressService.getRegions(country)),
      tap((regions) => {
        const regionControl = this.billingAddressForm.get('region.isocode');
        if (regions.length > 0) {
          regionControl.enable();
        } else {
          regionControl.disable();
        }
      })
    );

    this.showSameAsShippingAddressCheckbox$ = this.shippingAddress$.pipe(map(address => !!address));
  }

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

  getAddressCardContent(address: Address): Card {
    let region = '';
    if (address.region && address.region.isocode) {
      region = address.region.isocode + ', ';
    }

    return {
      textBold: address.firstName + ' ' + address.lastName,
      text: [
        address.line1,
        address.line2,
        address.town + ', ' + region + address.country.isocode,
        address.postalCode,
        address.phone,
      ],
    };
  }

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
      this.billingAddressForm.setControl('firstName', new FormControl('', Validators.required));
      this.billingAddressForm.setControl('lastName', new FormControl('', Validators.required));
      this.billingAddressForm.setControl('line1', new FormControl('', Validators.required));
      this.billingAddressForm.setControl('line2', new FormControl(''));
      this.billingAddressForm.setControl('town', new FormControl('', Validators.required));
      this.billingAddressForm.setControl('region', this.fb.group({
        isocode: [null, Validators.required],
      }));
      this.billingAddressForm.setControl('country', this.fb.group({
        isocode: [null, Validators.required],
      }));
      this.billingAddressForm.setControl('postalCode', new FormControl('', Validators.required));
    }
  }

}
