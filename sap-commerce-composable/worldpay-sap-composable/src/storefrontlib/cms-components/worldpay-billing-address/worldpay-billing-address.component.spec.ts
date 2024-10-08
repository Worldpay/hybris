import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { WorldpayBillingAddressComponent } from './worldpay-billing-address.component';
import { ReactiveFormsModule } from '@angular/forms';
import { Address, AddressValidation, Country, GlobalMessageService, I18nTestingModule, MockTranslatePipe, UserAddressService, UserPaymentService } from '@spartacus/core';
import { EMPTY, of } from 'rxjs';
import { FormErrorsModule, LaunchDialogService, NgSelectA11yModule } from '@spartacus/storefront';
import { NgSelectModule } from '@ng-select/ng-select';
import { CheckoutDeliveryAddressFacade } from '@spartacus/checkout/base/root';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CheckoutBillingAddressFormComponent, CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import createSpy = jasmine.createSpy;

const mockBillingCountries: Country[] = [
  {
    isocode: 'CA',
    name: 'Canada',
  },
];

const mockBillingCountriesEmpty: Country[] = [];

const mockAddress: Address = {
  firstName: 'John',
  lastName: 'Doe',
  titleCode: 'mr',
  line1: 'Toyosaki 2 create on cart',
  line2: 'line2',
  town: 'town',
  region: { isocode: 'JP-27' },
  postalCode: 'zip',
  country: { isocode: 'JP' },
};

@Component({
  selector: 'cx-card',
  template: '',
})
class MockCardComponent {
  @Input()
  content: any;
}

class MockCheckoutDeliveryService implements Partial<CheckoutDeliveryAddressFacade> {
  getDeliveryAddressState = createSpy().and.returnValue(
    of({
      loading: false,
      error: false,
      data: undefined
    })
  );
  getAddressVerificationResults = createSpy().and.returnValue(EMPTY);
  verifyAddress = createSpy();
  clearAddressVerificationResults = createSpy();
}

class MockUserPaymentService implements Partial<UserPaymentService> {
  loadBillingCountries = createSpy();
  getAllBillingCountries = createSpy().and.returnValue(
    of(mockBillingCountries)
  );
}

class MockGlobalMessageService implements Partial<GlobalMessageService> {
  add = createSpy();
}

class MockLaunchDialogService implements Partial<LaunchDialogService> {
  openDialogAndSubscribe() {
    return EMPTY;
  }
}

class MockUserAddressService implements Partial<UserAddressService> {
  getRegions = createSpy().and.returnValue(of([]));
  verifyAddress = createSpy().and.returnValue(of({}));
}

describe('WorldpayBillingAddressComponent', () => {
  let component: WorldpayBillingAddressComponent;
  let fixture: ComponentFixture<WorldpayBillingAddressComponent>;
  let mockCheckoutDeliveryService: MockCheckoutDeliveryService;
  let mockUserPaymentService: MockUserPaymentService;
  let mockGlobalMessageService: MockGlobalMessageService;
  let userAddressService: UserAddressService;

  beforeEach(waitForAsync(() => {
    mockCheckoutDeliveryService = new MockCheckoutDeliveryService();
    mockUserPaymentService = new MockUserPaymentService();
    mockGlobalMessageService = new MockGlobalMessageService();

    TestBed.configureTestingModule({
        imports: [
          ReactiveFormsModule,
          NgSelectModule,
          NgSelectA11yModule,
          I18nTestingModule,
          FormErrorsModule,
        ],
        providers: [
          {
            provide: LaunchDialogService,
            useClass: MockLaunchDialogService
          },
          {
            provide: CheckoutDeliveryAddressFacade,
            useValue: mockCheckoutDeliveryService,
          },
          {
            provide: UserPaymentService,
            useValue: mockUserPaymentService
          },
          {
            provide: GlobalMessageService,
            useValue: mockGlobalMessageService
          },
          {
            provide: UserAddressService,
            useClass: MockUserAddressService
          },
          CheckoutBillingAddressFormService,
        ],
        declarations: [
          WorldpayBillingAddressComponent,
          MockTranslatePipe,
          MockCardComponent
        ],
      })
      .overrideComponent(CheckoutBillingAddressFormComponent, {
        set: { changeDetection: ChangeDetectionStrategy.Default },
      })
      .compileComponents();
  }));

  beforeEach(() => {
    userAddressService = TestBed.inject(UserAddressService);
    fixture = TestBed.createComponent(WorldpayBillingAddressComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit()', () => {
    it('should call ngOnInit to get billing countries', (done) => {
      mockUserPaymentService.getAllBillingCountries = createSpy().and.returnValue(of(mockBillingCountries));
      component.ngOnInit();
      component.countries$.subscribe((countries: Country[]) => {
        expect(countries).toBe(mockBillingCountries);
        done();
      });
    });

    it('should call ngOnInit to get delivery address set in cart', (done) => {
      mockCheckoutDeliveryService.getDeliveryAddressState = createSpy().and.returnValue(
        of({
          loading: false,
          error: false,
          data: mockAddress
        })
      );
      component.ngOnInit();
      component.deliveryAddress$.subscribe((address) => {
        expect(address).toBe(mockAddress);
        done();
      });
    });

    it('should call ngOnInit to load billing countries', (done) => {
      mockUserPaymentService.getAllBillingCountries = createSpy().and.returnValue(of(mockBillingCountriesEmpty));

      component.ngOnInit();
      component.countries$.subscribe((countries: Country[]) => {
        expect(countries).toBe(mockBillingCountriesEmpty);
        expect(mockUserPaymentService.loadBillingCountries).toHaveBeenCalled();
        done();
      });
    });

    it('should add address with address verification result "accept"', () => {
      spyOn(component, 'openSuggestedAddress');
      const mockAddressVerificationResult = { decision: 'ACCEPT' };
      component.ngOnInit();
      component['handleAddressVerificationResults'](
        mockAddressVerificationResult
      );
      expect(mockGlobalMessageService.add).not.toHaveBeenCalled();
      expect(component.openSuggestedAddress).not.toHaveBeenCalled();
    });

    it('should display error message with address verification result "reject"', () => {
      const mockAddressVerificationResult: AddressValidation = {
        decision: 'REJECT',
      };
      component.ngOnInit();
      component['handleAddressVerificationResults'](
        mockAddressVerificationResult
      );
      expect(mockGlobalMessageService.add).toHaveBeenCalled();
    });

    it('should open suggested address with address verification result "review"', () => {
      const mockAddressVerificationResult: AddressValidation = {
        decision: 'REVIEW',
      };
      spyOn(component, 'openSuggestedAddress');
      component.ngOnInit();
      component['handleAddressVerificationResults'](
        mockAddressVerificationResult
      );
      expect(component.openSuggestedAddress).toHaveBeenCalled();
    });
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
    mockUserPaymentService.getAllBillingCountries = createSpy().and.returnValue(of([]));
    component.ngOnInit();

    component.countries$.subscribe((countries) => {
      expect(countries.length).toEqual(0);
      expect(mockUserPaymentService.loadBillingCountries).toHaveBeenCalled();
      done();
    }).unsubscribe();
  });

  it('should not load billing countries if billing countries already loaded', (done) => {
    component.ngOnInit();

    component.countries$.subscribe((countries) => {
      expect(countries).toEqual(mockBillingCountries);
      expect(mockUserPaymentService.loadBillingCountries).not.toHaveBeenCalled();
      done();
    }).unsubscribe();
  });
});
