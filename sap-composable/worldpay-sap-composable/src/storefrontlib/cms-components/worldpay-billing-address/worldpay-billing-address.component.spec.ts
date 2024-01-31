import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldpayBillingAddressComponent } from './worldpay-billing-address.component';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Address, Country, I18nModule, I18nTestingModule, MockTranslatePipe, QueryState, UserAddressService, UserPaymentService } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { StoreModule } from '@ngrx/store';
import { FormErrorsModule } from '@spartacus/storefront';
import { NgSelectModule } from '@ng-select/ng-select';
import { CheckoutDeliveryAddressFacade } from '@spartacus/checkout/base/root';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'cx-card',
  template: '',
})
class MockCardComponent {
  @Input() content;
}

const unitedStates: Country = {
  isocode: 'US',
  name: 'United States of Murica'
};

const deliveryAddress: Address = {
  firstName: 'John',
  lastName: 'Doe',
  country: unitedStates
};

class MockUserAddressService {
  getRegions(country: Country) {
    return of([
      {
        isocode: 'ca',
        countryIso: country.isocode
      },
      {
        isocode: 'or',
        countryIso: country.isocode
      },
    ]);
  }
}

class MockUserPaymentService {
  getAllBillingCountries() {
    return of([unitedStates]);
  }

  loadBillingCountries() {

  }
}

class MockCheckoutDeliveryAddressFacade implements Partial<CheckoutDeliveryAddressFacade> {
  getDeliveryAddressState(): Observable<QueryState<Address>> {
    return of({
      loading: false,
      error: false,
      data: deliveryAddress
    });
  }
}

describe('WorldpayBillingAddressComponent', () => {
  let component: WorldpayBillingAddressComponent;
  let fixture: ComponentFixture<WorldpayBillingAddressComponent>;
  let userAddressService: UserAddressService;
  let userPaymentService: UserPaymentService;
  let billingAddressForm: UntypedFormGroup;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        imports: [
          StoreModule.forRoot({}),
          I18nTestingModule,
          ReactiveFormsModule,
          FormErrorsModule,
          I18nModule,
          NgSelectModule,
        ],
        providers: [
          UntypedFormBuilder,
          {
            provide: UserAddressService,
            useClass: MockUserAddressService
          },
          {
            provide: UserPaymentService,
            useClass: MockUserPaymentService
          },
          {
            provide: CheckoutDeliveryAddressFacade,
            useClass: MockCheckoutDeliveryAddressFacade
          },
        ],
        declarations: [
          WorldpayBillingAddressComponent,
          MockTranslatePipe,
          MockCardComponent
        ],
      })
      .compileComponents();
  });

  beforeEach(() => {
    billingAddressForm = new UntypedFormGroup({});

    fixture = TestBed.createComponent(WorldpayBillingAddressComponent);
    component = fixture.componentInstance;
    component.billingAddressForm = billingAddressForm;
    fixture.detectChanges();

    userAddressService = TestBed.inject(UserAddressService);
    userPaymentService = TestBed.inject(UserPaymentService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate billing address form if non given', () => {
    component.billingAddressForm = null;

    component.ngOnInit();

    const form = component.billingAddressForm;
    expect(form.value.hasOwnProperty('firstName')).toBeTruthy();
    expect(form.value.hasOwnProperty('lastName')).toBeTruthy();
    expect(form.value.hasOwnProperty('line1')).toBeTruthy();
    expect(form.value.hasOwnProperty('line2')).toBeTruthy();
    expect(form.value.hasOwnProperty('town')).toBeTruthy();
    expect(form.value.hasOwnProperty('region')).toBeTruthy();
    expect(form.value.hasOwnProperty('country')).toBeTruthy();
    expect(form.value.hasOwnProperty('postalCode')).toBeTruthy();
  });

  it('should call load billing countries if no countries defined', (done) => {
    spyOn(userPaymentService, 'getAllBillingCountries').and.returnValue(of([]));
    spyOn(userPaymentService, 'loadBillingCountries').and.callThrough();

    component.ngOnInit();

    component.countries$.subscribe((countries) => {
      expect(countries.length).toEqual(0);
      expect(userPaymentService.loadBillingCountries).toHaveBeenCalled();
      done();
    }).unsubscribe();
  });

  it('should not load billing countries if billing countries already loaded', (done) => {
    spyOn(userPaymentService, 'loadBillingCountries').and.callThrough();

    component.ngOnInit();

    component.countries$.subscribe((countries) => {
      expect(countries).toEqual([unitedStates]);
      expect(userPaymentService.loadBillingCountries).not.toHaveBeenCalled();
      done();
    }).unsubscribe();
  });

});
