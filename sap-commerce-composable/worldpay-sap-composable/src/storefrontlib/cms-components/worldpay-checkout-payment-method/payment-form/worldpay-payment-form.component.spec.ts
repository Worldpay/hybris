import { ChangeDetectionStrategy } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NgSelectModule } from '@ng-select/ng-select';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressFacade, CheckoutPaymentFacade, } from '@spartacus/checkout/base/root';
import {
  Address,
  CardType,
  Country,
  FeatureDirective,
  GlobalMessageService,
  I18nTestingModule,
  LoggerService,
  PaymentDetails,
  UserAddressService,
  UserPaymentService,
} from '@spartacus/core';
import { FormErrorsModule, FormRequiredAsterisksComponent, FormRequiredLegendComponent, LaunchDialogService, NgSelectA11yModule, } from '@spartacus/storefront';
import { BehaviorSubject, EMPTY, of } from 'rxjs';
import {
  MockCxCardComponent,
  MockCxIconComponent,
  MockCxSpinnerComponent,
  MockGlobalMessageService,
  MockLaunchDialogService,
  MockUserAddressService,
  MockWorldpayBillingAddressComponent,
  MockWorldpayConnector,
  MockWorldpayFraudsightService
} from 'worldpay-sap-composable-tests';
import { WorldpayBillingAddressFormService, WorldpayConnector, WorldpayFraudsightService } from '../../../../core';
import { WorldpayPaymentFormComponent } from './worldpay-payment-form.component';
import createSpy = jasmine.createSpy;

const mockBillingCountries: Country[] = [
  {
    isocode: 'CA',
    name: 'Canada',
  },
];

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

const mockCardTypes: CardType[] = [
  {
    code: 'amex',
    name: 'American Express',
  },
  {
    code: 'maestro',
    name: 'Maestro',
  },
];

const mockPayment: any = {
  cardType: {
    code: mockCardTypes[0].code,
  },
  accountHolderName: 'Test Name',
  cardNumber: '1234123412341234',
  expiryMonth: '02',
  expiryYear: 2022,
  cvn: '123',
  defaultPayment: false,
  save: false,
  saved: false
};

class MockCheckoutPaymentService implements Partial<CheckoutPaymentFacade> {
  loadSupportedCardTypes = createSpy();
  getPaymentCardTypes = createSpy().and.returnValue(EMPTY);
  getSetPaymentDetailsResultProcess = createSpy().and.returnValue(
    of({ loading: false })
  );
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

let fsEnabled = new BehaviorSubject<boolean>(false);

describe('WorldpayPaymentFormComponent', () => {
  let component: WorldpayPaymentFormComponent;
  let fixture: ComponentFixture<WorldpayPaymentFormComponent>;
  let mockCheckoutDeliveryService: MockCheckoutDeliveryService;
  let mockCheckoutPaymentService: MockCheckoutPaymentService;
  let mockUserPaymentService: MockUserPaymentService;
  let mockGlobalMessageService: MockGlobalMessageService;
  let billingAddressService: WorldpayBillingAddressFormService;
  let mockWorldpayFraudsightService: MockWorldpayFraudsightService;

  let controls: {
    payment: UntypedFormGroup['controls'];
  };

  beforeEach(waitForAsync(() => {
    mockCheckoutDeliveryService = new MockCheckoutDeliveryService();
    mockCheckoutPaymentService = new MockCheckoutPaymentService();
    mockUserPaymentService = new MockUserPaymentService();
    mockGlobalMessageService = new MockGlobalMessageService();
    mockWorldpayFraudsightService = new MockWorldpayFraudsightService();

    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        NgSelectModule,
        NgSelectA11yModule,
        I18nTestingModule,
        FormErrorsModule,
        FormRequiredAsterisksComponent,
        FormRequiredLegendComponent
      ],
      declarations: [
        WorldpayPaymentFormComponent,
        MockCxCardComponent,
        MockCxIconComponent,
        MockCxSpinnerComponent,
        FeatureDirective,
        MockWorldpayBillingAddressComponent,
      ],
      providers: [
        {
          provide: LaunchDialogService,
          useClass: MockLaunchDialogService
        },
        {
          provide: CheckoutPaymentFacade,
          useValue: mockCheckoutPaymentService,
        },
        WorldpayBillingAddressFormService,
        {
          provide: CheckoutBillingAddressFormService,
          useExisting: WorldpayBillingAddressFormService
        },
        {
          provide: WorldpayConnector,
          useClass: MockWorldpayConnector,
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
        {
          provide: WorldpayFraudsightService,
          useValue: mockWorldpayFraudsightService
        },
        LoggerService
      ],
    }).overrideComponent(WorldpayPaymentFormComponent, {
      set: { changeDetection: ChangeDetectionStrategy.Default },
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayPaymentFormComponent);
    component = fixture.componentInstance;
    controls = {
      payment: component.paymentForm.controls,
    };
    component.loading = false;
    billingAddressService = TestBed.inject(WorldpayBillingAddressFormService);
    spyOn(component.setPaymentDetails, 'emit').and.callThrough();
    spyOn(component.closeForm, 'emit').and.callThrough();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('it should patch the form if the payment details is provided', () => {
    const mockPaymentDetails: PaymentDetails = {
      id: 'test',
    };
    component.paymentDetails = mockPaymentDetails;
    spyOn(component.paymentForm, 'patchValue').and.callThrough();

    component.ngOnInit();

    expect(component.paymentForm.patchValue).toHaveBeenCalledWith(
      mockPaymentDetails
    );
  });

  it('it should NOT patch the form if the payment details is NOT provided', () => {
    spyOn(component.paymentForm, 'patchValue').and.callThrough();

    component.ngOnInit();

    expect(component.paymentForm.patchValue).not.toHaveBeenCalled();
  });

  it('should call ngOnInit to get supported card types if they exist', () => {
    mockCheckoutPaymentService.getPaymentCardTypes =
      createSpy().and.returnValue(of(mockCardTypes));

    component.ngOnInit();
    component.cardTypes$.subscribe((cardTypes: CardType[]) => {
      expect(cardTypes).toBe(mockCardTypes);
    });
  });

  it('should call toggleDefaultPaymentMethod() with defaultPayment flag set to false and return true', () => {
    component.paymentForm.value.defaultPayment = false;
    component.toggleDefaultPaymentMethod();
    expect(component.paymentForm.value.defaultPayment).toBeTruthy();
  });

  it('should call toggleDefaultPaymentMethod() with defaultPayment flag set to true and return false', () => {
    component.paymentForm.value.defaultPayment = true;
    component.toggleDefaultPaymentMethod();
    expect(component.paymentForm.value.defaultPayment).toBeFalsy();
  });

  it('should call next()', () => {
    component.paymentForm.setValue(mockPayment);
    component.next();
    expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
      paymentDetails: component.paymentForm.value,
      billingAddress: null,
    });
  });

  it('should call close()', () => {
    component.close();
    expect(component.closeForm.emit).toHaveBeenCalled();
  });

  describe('UI continue button', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    const getContinueBtn = () =>
      fixture.debugElement.query(By.css('.btn-primary'));

    it('should call "next" function when being clicked and when form is valid - with billing address', () => {
      mockCheckoutPaymentService.getPaymentCardTypes =
        createSpy().and.returnValue(of(mockCardTypes));
      mockCheckoutDeliveryService.getDeliveryAddressState =
        createSpy().and.returnValue(
          of({
            loading: false,
            error: false,
            data: mockAddress
          })
        );
      mockUserPaymentService.getAllBillingCountries =
        createSpy().and.returnValue(of(mockBillingCountries));
      spyOn(component, 'next');
      component.paymentForm.setValue(mockPayment);
      fixture.detectChanges();
      getContinueBtn().nativeElement.click();
      expect(component.next).toHaveBeenCalledTimes(1);

      fixture.detectChanges();
      getContinueBtn().nativeElement.click();
      expect(component.next).toHaveBeenCalledTimes(2);
    });

    it('should call "next" function when being clicked and when form is valid - without billing address', () => {
      mockCheckoutPaymentService.getPaymentCardTypes = createSpy().and.returnValue(of(mockCardTypes));
      mockCheckoutDeliveryService.getDeliveryAddressState = createSpy().and.returnValue(of({
        loading: false,
        error: false,
        data: mockAddress
      }));
      mockUserPaymentService.getAllBillingCountries = createSpy().and.returnValue(of(mockBillingCountries));
      spyOn(component, 'next');
      fixture.detectChanges();
      getContinueBtn().nativeElement.click();
      expect(component.next).not.toHaveBeenCalled();

      // set values for payment form
      controls.payment['accountHolderName'].setValue('test accountHolderName');
      controls.payment['cardNumber'].setValue('test cardNumber');
      controls.payment['cardType']['controls']['code'].setValue(
        'test card type code'
      );
      controls.payment['expiryMonth'].setValue('test expiryMonth');
      controls.payment['expiryYear'].setValue('test expiryYear');
      controls.payment['cvn'].setValue('test cvn');

      fixture.detectChanges();
      getContinueBtn().nativeElement.click();
      expect(component.next).toHaveBeenCalledTimes(1);
    });

    it('should check setAsDefaultField to determine whether setAsDefault checkbox displayed or not', () => {
      component.setAsDefaultField = false;
      fixture.detectChanges();
      expect(
        fixture.debugElement.queryAll(By.css('#setAsDefaultField')).length
      ).toEqual(0);

      component.setAsDefaultField = true;
      fixture.detectChanges();
      expect(
        fixture.debugElement.queryAll(By.css('#setAsDefaultField')).length
      ).toEqual(1);
    });

    it('should show assitive message when form is submitted with errors', () => {
      component.paymentForm.setErrors({ required: true });
      component.next();
      expect(mockGlobalMessageService.add).toHaveBeenCalled();
    });
  });

  describe('UI close/back button', () => {
    const getBackBtn = () =>
      fixture.debugElement.query(By.css('.btn-secondary'));

    it('should call "back" function after being clicked', () => {
      component.paymentMethodsCount = 0;
      fixture.detectChanges();
      spyOn(component, 'back');
      getBackBtn().nativeElement.click();
      fixture.detectChanges();
      expect(component.back).toHaveBeenCalled();
    });

    it('should call back()', () => {
      spyOn(component.goBack, 'emit').and.callThrough();
      component.back();

      expect(component.goBack.emit).toHaveBeenCalledWith();
    });

    it('should call "close" function after being clicked', () => {
      component.paymentMethodsCount = 1;
      component.loading = false;
      fixture.detectChanges();
      spyOn(component, 'close');
      getBackBtn().nativeElement.click();
      fixture.detectChanges();
      expect(component.close).toHaveBeenCalled();
    });
  });

  describe('should populate the form', () => {
    beforeEach(() => {
      fsEnabled = new BehaviorSubject<boolean>(false);
      spyOn(mockWorldpayFraudsightService, 'isFraudSightEnabledFromState').and.returnValue(fsEnabled);
      fixture = TestBed.createComponent(WorldpayPaymentFormComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not include dateOfBirth field as FraudSight is disabled', () => {

      expect(component.paymentForm.value).toEqual({
        defaultPayment: false,
        save: false,
        saved: false,
        accountHolderName: '',
        cardNumber: '',
        cardType: { code: '' },
        expiryMonth: '',
        expiryYear: '',
        cvn: '',
      });
    });

  });

  describe('should populate the form with dateOfBirth', () => {
    beforeEach(() => {
      fsEnabled = new BehaviorSubject<boolean>(true);
      spyOn(mockWorldpayFraudsightService, 'isFraudSightEnabledFromState').and.returnValue(fsEnabled);
      fixture = TestBed.createComponent(WorldpayPaymentFormComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should add dateOfBirth field is FraudSight is enabled', (done) => {
      fsEnabled.next(true);

      fixture.whenStable().then(() => {
        expect(component.paymentForm.contains('dateOfBirth')).toBeTrue();
        done();
      }).catch(done.fail);
    });

    it('should toggle Default Payment Method with defaultPayment flag set to false', () => {
      component.ngOnInit();
      fixture.detectChanges();
      component.paymentForm.patchValue({ defaultPayment: true });
      fixture.detectChanges();
      expect(component.paymentForm.value.defaultPayment).toBeTrue();
      expect(component.paymentForm.value.save).toBeTrue();

      component.paymentForm.patchValue({ save: false });
      fixture.detectChanges();
      expect(component.paymentForm.value.defaultPayment).toBeFalse();
      expect(component.paymentForm.value.save).toBeFalse();
    });
  });

  describe('bindBillingAddressChanges', () => {
    it('should disable continue button when all conditions are false', (done) => {
      spyOn(billingAddressService, 'getSameAsDeliveryAddress').and.returnValue(of(false));
      spyOn(billingAddressService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
      spyOn(billingAddressService, 'isBillingAddressFormValid').and.returnValue(false);

      component['bindBillingAddressChanges']();

      component.continueButtonDisabled$.subscribe((disabled) => {
        expect(disabled).toBeTrue();
        done();
      });
    });

    it('should enable continue button when sameAsDeliveryAddress is true', (done) => {
      spyOn(billingAddressService, 'getSameAsDeliveryAddress').and.returnValue(of(false));
      spyOn(billingAddressService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
      spyOn(billingAddressService, 'isBillingAddressFormValid').and.returnValue(false);

      component['bindBillingAddressChanges']();

      component.continueButtonDisabled$.subscribe((disabled) => {
        expect(disabled).toBeFalse();
        done();
      });
    });

    it('should enable continue button when billingAddressFormValid is true', (done) => {
      spyOn(billingAddressService, 'getSameAsDeliveryAddress').and.returnValue(of(false));
      spyOn(billingAddressService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
      spyOn(billingAddressService, 'isBillingAddressFormValid').and.returnValue(true);

      component['bindBillingAddressChanges']();

      component.continueButtonDisabled$.subscribe((disabled) => {
        expect(disabled).toBeFalse();
        done();
      });
    });

    it('should enable continue button when getSameAsDeliveryAddress is true', (done) => {
      spyOn(billingAddressService, 'getSameAsDeliveryAddress').and.returnValue(of(true));
      spyOn(billingAddressService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
      spyOn(billingAddressService, 'isBillingAddressFormValid').and.returnValue(false);

      component['bindBillingAddressChanges']();

      component.continueButtonDisabled$.subscribe((disabled) => {
        expect(disabled).toBeFalse();
        done();
      });
    });
  });
});
