import { ChangeDetectionStrategy } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressFacade } from '@spartacus/checkout/base/root';
import { Address, AddressValidation, Country, GlobalMessageService, I18nTestingModule, MockTranslatePipe, UserAddressService, UserPaymentService } from '@spartacus/core';
import { FormErrorsModule, LaunchDialogService, NgSelectA11yModule } from '@spartacus/storefront';
import { MockCxCardComponent } from '@worldpay-tests/components';
import { EMPTY, of } from 'rxjs';

import { WorldpayBillingAddressComponent } from './worldpay-billing-address.component';
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
  let checkoutBillingAddressFormService: CheckoutBillingAddressFormService;

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
          MockCxCardComponent
        ],
      })
      .overrideComponent(WorldpayBillingAddressComponent, {
        set: { changeDetection: ChangeDetectionStrategy.Default },
      })
      .compileComponents();
  }));

  beforeEach(() => {
    userAddressService = TestBed.inject(UserAddressService);
    fixture = TestBed.createComponent(WorldpayBillingAddressComponent);
    component = fixture.componentInstance;
    checkoutBillingAddressFormService = TestBed.inject(CheckoutBillingAddressFormService);
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

  describe('line2Field', () => {
    it('should return FormControl for line2 field', () => {
      component.billingAddressForm = checkoutBillingAddressFormService.getBillingAddressForm();
      const line2Field = component.line2Field;
      expect(line2Field).toBeInstanceOf(FormControl);
      expect(line2Field.value).toBe('');
    });

    it('should return FormControl with correct initial value', () => {
      component.billingAddressForm = checkoutBillingAddressFormService.getBillingAddressForm();
      component.billingAddressForm.get('line2').setValue('Initial Value');
      const line2Field = component.line2Field;
      expect(line2Field.value).toBe('Initial Value');
    });

    it('should return FormControl with updated value', () => {
      component.billingAddressForm = checkoutBillingAddressFormService.getBillingAddressForm();
      component.billingAddressForm.get('line2').setValue('Updated Value');
      const line2Field = component.line2Field;
      expect(line2Field.value).toBe('Updated Value');
    });
  });

  describe('getRegionBindingLabel', () => {
    it('should return "name" when the first region has a name', () => {
      const regions = [{
        name: 'RegionName',
        isocode: 'ISO',
        isocodeShort: 'ISOShort'
      }];
      const result = component.getRegionBindingLabel(regions);
      expect(result).toBe('name');
    });

    it('should return "isocodeShort" when the first region has no name but has isocodeShort', () => {
      const regions = [{
        name: null,
        isocode: 'ISO',
        isocodeShort: 'ISOShort'
      }];
      const result = component.getRegionBindingLabel(regions);
      expect(result).toBe('isocodeShort');
    });

    it('should return "isocode" when the first region has no name and no isocodeShort', () => {
      const regions = [{
        name: null,
        isocode: 'ISO',
        isocodeShort: null
      }];
      const result = component.getRegionBindingLabel(regions);
      expect(result).toBe('isocode');
    });

    it('should return "isocode" when regions array is empty', () => {
      const regions = [];
      const result = component.getRegionBindingLabel(regions);
      expect(result).toBe('isocode');
    });

    it('should return "isocode" when regions is null', () => {
      const result = component.getRegionBindingLabel(null);
      expect(result).toBe('isocode');
    });
  });

  describe('getRegionBindingValue', () => {
    it('should return "isocode" when the first region has an isocode', () => {
      const regions = [{
        isocode: 'ISO',
        isocodeShort: 'ISOShort'
      }];
      const result = component.getRegionBindingValue(regions);
      expect(result).toBe('isocode');
    });

    it('should return "isocodeShort" when the first region has no isocode but has isocodeShort', () => {
      const regions = [{
        isocode: null,
        isocodeShort: 'ISOShort'
      }];
      const result = component.getRegionBindingValue(regions);
      expect(result).toBe('isocodeShort');
    });

    it('should return "isocode" when the first region has no isocode and no isocodeShort', () => {
      const regions = [{
        isocode: null,
        isocodeShort: null
      }];
      const result = component.getRegionBindingValue(regions);
      expect(result).toBe('isocode');
    });

    it('should return "isocode" when regions array is empty', () => {
      const regions = [];
      const result = component.getRegionBindingValue(regions);
      expect(result).toBe('isocode');
    });

    it('should return "isocode" when regions is null', () => {
      const result = component.getRegionBindingValue(null);
      expect(result).toBe('isocode');
    });
  });

  describe('buildForm', () => {
    it('should set line2 field as required when country is Japan', () => {
      component.billingAddressForm = checkoutBillingAddressFormService.getBillingAddressForm();
      component['buildForm']();
      component.billingAddressForm.get('country').setValue({ isocode: 'JP' });
      expect(component.line2Field.hasValidator(Validators.required)).toBeTrue();
      expect(component.jpLabel).toBe('.jp');
    });

    it('should clear line2 field validators when country is not Japan', () => {
      component.billingAddressForm = checkoutBillingAddressFormService.getBillingAddressForm();
      component['buildForm']();
      component.billingAddressForm.get('country').setValue({ isocode: 'US' });
      expect(component.line2Field.hasValidator(Validators.required)).toBeFalse();
      expect(component.jpLabel).toBe('');
    });

    it('should update line2 field validity when country changes', () => {
      component.billingAddressForm = checkoutBillingAddressFormService.getBillingAddressForm();
      spyOn(component.line2Field, 'updateValueAndValidity');
      component['buildForm']();
      component.billingAddressForm.get('country').setValue({ isocode: 'JP' });
      expect(component.line2Field.updateValueAndValidity).toHaveBeenCalled();
    });
  });

  describe('toggleSameAsDeliveryAddress', () => {
    it('should set sameAsDeliveryAddress to true and set delivery address as billing address', () => {
      mockCheckoutDeliveryService.getDeliveryAddressState = createSpy().and.returnValue(
        of({
          loading: false,
          error: false,
          data: mockAddress
        })
      );

      component.sameAsDeliveryAddress = false;
      fixture.detectChanges();
      spyOn(checkoutBillingAddressFormService, 'setDeliveryAddressAsBillingAddress');
      spyOn(component.emitSameAsDeliveryAddress, 'emit');

      component.toggleSameAsDeliveryAddress();

      expect(component.sameAsDeliveryAddress).toBeTrue();
      expect(checkoutBillingAddressFormService.setDeliveryAddressAsBillingAddress).toHaveBeenCalledWith(mockAddress);
      expect(component.emitSameAsDeliveryAddress.emit).toHaveBeenCalledWith(true);
    });

    it('should set sameAsDeliveryAddress to false and clear billing address', () => {
      component.sameAsDeliveryAddress = true;
      spyOn(checkoutBillingAddressFormService, 'setDeliveryAddressAsBillingAddress');
      spyOn(component.emitSameAsDeliveryAddress, 'emit');

      component.toggleSameAsDeliveryAddress();

      expect(component.sameAsDeliveryAddress).toBeFalse();
      expect(checkoutBillingAddressFormService.setDeliveryAddressAsBillingAddress).toHaveBeenCalledWith(undefined);
      expect(component.emitSameAsDeliveryAddress.emit).toHaveBeenCalledWith(false);
    });
  });
});
