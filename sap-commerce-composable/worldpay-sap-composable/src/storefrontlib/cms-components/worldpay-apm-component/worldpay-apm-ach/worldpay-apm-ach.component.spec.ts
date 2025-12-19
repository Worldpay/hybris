import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { Address, CommandService, EventService, I18nTestingModule, LoggerService, QueryService, QueryState, UserIdService } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { of } from 'rxjs';
import { WorldpayConnector } from 'worldpay-sap-composable-connectors';
import { WorldpayACHFacade } from 'worldpay-sap-composable-facade';
import { WorldpayBillingAddressFormService } from 'worldpay-sap-composable-services';
import {
  MockActiveCartFacade,
  MockUserIdService,
  MockWorldpayApmSubmitButtonsComponent,
  MockWorldpayBillingAddressComponent,
  MockWorldpayConnector
} from 'worldpay-sap-composable-tests';
import { AccountTypes, PaymentMethod } from 'worldpay-sap-core';
import { WorldpayApmAchComponent } from './worldpay-apm-ach.component';
import SpyObj = jasmine.SpyObj;

const mockBillingAddress: Address = {
  'firstName': 'Test',
  'lastName': 'User',
  'line1': '2022 Miami Street',
  'line2': '',
  'town': 'South Bend',
  region: {
    isocodeShort: 'Region'
  },
  'country': {
    'isocode': 'BE'
  },
  'postalCode': '46613'
};

const mockACHBankAccountTypesState = of({
  loading: false,
  error: false,
  data: {
    checking: 'Checking',
    corporate: 'Corporate',
    corporateSavings: 'Corporate Savings',
    savings: 'Savings',
  }
} as QueryState<AccountTypes>);

const mockACHPaymentFormValue = {
  accountType: 'type1',
  accountNumber: '123456789',
  routingNumber: '987654321',
  checkNumber: '11111',
  companyName: 'Test Company',
  customIdentifier: '12345'
};

describe('WorldpayApmAchComponent', () => {
  let component: WorldpayApmAchComponent;
  let fixture: ComponentFixture<WorldpayApmAchComponent>;
  let worldpayACHFacade: SpyObj<WorldpayACHFacade>;
  let billingAddressFormService: WorldpayBillingAddressFormService;

  beforeEach(async () => {
    worldpayACHFacade = jasmine.createSpyObj('WorldpayACHFacade', ['getACHBankAccountTypesState', 'getACHPaymentFormValue', 'setACHPaymentFormValue']);

    await TestBed.configureTestingModule({
      declarations: [
        WorldpayApmAchComponent,
        MockWorldpayBillingAddressComponent,
        MockWorldpayApmSubmitButtonsComponent
      ],
      imports: [
        I18nTestingModule,
        FormErrorsModule,
        ReactiveFormsModule,
        NgSelectModule,
      ],
      providers: [
        EventService,
        LoggerService,
        QueryService,
        CommandService,
        {
          provide: WorldpayACHFacade,
          useValue: worldpayACHFacade
        },
        {
          provide: UserIdService,
          useClass: MockUserIdService
        },
        {
          provide: ActiveCartFacade,
          useClass: MockActiveCartFacade
        },
        WorldpayBillingAddressFormService,
        {
          provide: WorldpayConnector,
          useClass: MockWorldpayConnector
        },
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(WorldpayApmAchComponent);
    billingAddressFormService = TestBed.inject(WorldpayBillingAddressFormService);
    worldpayACHFacade = TestBed.inject(WorldpayACHFacade) as SpyObj<WorldpayACHFacade>;
    component = fixture.componentInstance;
    worldpayACHFacade.getACHPaymentFormValue.and.returnValue(of(null));
    worldpayACHFacade.getACHBankAccountTypesState.and.returnValue(mockACHBankAccountTypesState);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize billing address form and sameAsShippingAddress flag', () => {
    component.ngOnInit();

    expect(component['billingAddressForm']).toBe(billingAddressFormService.getBillingAddressForm());
    component['sameAsDeliveryAddress$'].subscribe(res => {
      expect(res).toBe(billingAddressFormService.isBillingAddressSameAsDeliveryAddress());
    });
  });

  it('should initialize ACH bank account types', () => {
    component.ngOnInit();

    expect(worldpayACHFacade.getACHBankAccountTypesState).toHaveBeenCalled();
  });

  it('should set ACH bank account types state', () => {
    component.ngOnInit();

    component.achBankAccountTypesState$.subscribe(state => {
      expect(state.data).toEqual([
        {
          code: 'checking',
          name: 'Checking'
        },
        {
          code: 'corporate',
          name: 'Corporate'
        },
        {
          code: 'corporateSavings',
          name: 'Corporate Savings',
        },
        {
          code: 'savings',
          name: 'Savings',
        }
      ]);
    });

    expect(worldpayACHFacade.getACHBankAccountTypesState).toHaveBeenCalled();
  });

  it('should set ACH payment form value on initialization', () => {

    worldpayACHFacade.getACHPaymentFormValue.and.returnValue(of(mockACHPaymentFormValue));

    component.ngOnInit();

    fixture.detectChanges();

    expect(worldpayACHFacade.getACHPaymentFormValue).toHaveBeenCalled();
    expect(component.achForm.value).toEqual({
      ...mockACHPaymentFormValue,
      accountType: { code: mockACHPaymentFormValue.accountType }
    });
  });

  it('should emit payment details and billing address when form is valid and sameAsShippingAddress is false', () => {
    spyOn(billingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
    spyOn(billingAddressFormService, 'isBillingAddressFormValid').and.returnValue(true);
    spyOn(billingAddressFormService, 'getBillingAddress').and.returnValue(mockBillingAddress);
    spyOn(component.setPaymentDetails, 'emit');
    billingAddressFormService.getBillingAddressForm().patchValue(mockBillingAddress);
    billingAddressFormService.setBillingAddress(mockBillingAddress);

    component.achForm.setValue({
      accountType: { code: 'type1' },
      accountNumber: '123456789',
      routingNumber: '987654321',
      checkNumber: '11111',
      companyName: 'Test Company',
      customIdentifier: '12345'
    });
    fixture.detectChanges();
    component.next();

    expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
      paymentDetails: {
        code: PaymentMethod.ACH,
        achPaymentForm: {
          accountType: 'type1',
          accountNumber: '123456789',
          routingNumber: '987654321',
          checkNumber: '11111',
          companyName: 'Test Company',
          customIdentifier: '12345'
        }
      },
      billingAddress: mockBillingAddress
    });
  });

  it('should emit payment details without billing address when form is valid and sameAsShippingAddress is true', () => {
    spyOn(billingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
    spyOn(billingAddressFormService, 'getBillingAddressForm').and.returnValue(undefined);
    spyOn(component.setPaymentDetails, 'emit');

    component.achForm.setValue({
      accountType: { code: 'type1' },
      accountNumber: '123456789',
      routingNumber: '987654321',
      checkNumber: '11111',
      companyName: 'Test Company',
      customIdentifier: '12345'
    });

    component.next();

    expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
      paymentDetails: {
        code: PaymentMethod.ACH,
        achPaymentForm: {
          accountType: 'type1',
          accountNumber: '123456789',
          routingNumber: '987654321',
          checkNumber: '11111',
          companyName: 'Test Company',
          customIdentifier: '12345'
        }
      },
      billingAddress: undefined
    });
  });

  it('should not emit payment details when form is invalid', () => {
    spyOn(component.setPaymentDetails, 'emit');
    component.achForm.setValue({
      accountType: { code: null },
      accountNumber: null,
      routingNumber: null,
      checkNumber: null,
      companyName: null,
      customIdentifier: null
    });

    component.next();

    expect(component.setPaymentDetails.emit).not.toHaveBeenCalled();
  });

  describe('allowOnlyNumbers', () => {
    it('should not allow non-numeric characters in routingNumber field', () => {
      component.allowNumbersOnly({ target: { value: 'abc123' } } as any, 'routingNumber');
      expect(component.achForm.get('routingNumber').value).toEqual('123');
    });

    it('should not submit payment details if billing address validation fails', () => {
      spyOn(component.setPaymentDetails, 'emit');
      // @ts-ignore
      spyOn(component, 'validateAndGetBillingAddress').and.returnValue({
        isValid: false,
        billingAddress: null
      });

      component.achForm.setValue({
        accountType: { code: 'type1' },
        accountNumber: '123456789',
        routingNumber: '987654321',
        checkNumber: '11111',
        companyName: 'Test Company',
        customIdentifier: '12345'
      });

      component.next();

      expect(component.setPaymentDetails.emit).not.toHaveBeenCalled();
    });

    it('should handle empty ACH bank account types gracefully', () => {
      worldpayACHFacade.getACHBankAccountTypesState.and.returnValue(of({
        loading: false,
        error: false,
        data: {}
      }) as any);

      component.ngOnInit();

      component.achBankAccountTypesState$.subscribe(state => {
        expect(state.data).toEqual([]);
      });
    });

    it('should reset form values when reset is called', () => {
      component.achForm.setValue({
        accountType: { code: 'type1' },
        accountNumber: '123456789',
        routingNumber: '987654321',
        checkNumber: '11111',
        companyName: 'Test Company',
        customIdentifier: '12345'
      });

      component.achForm.reset();

      expect(component.achForm.value).toEqual({
        accountType: {
          code: null
        },
        accountNumber: null,
        routingNumber: null,
        checkNumber: null,
        companyName: null,
        customIdentifier: null
      });
    });
  });

  it('should emit back event on return', () => {
    spyOn(component.back, 'emit');

    component['return']();

    expect(component.back.emit).toHaveBeenCalled();
  });

  describe('next', () => {
    it('should not proceed if billing address validation fails', () => {
      // @ts-ignore
      spyOn(component, 'validateAndGetBillingAddress').and.returnValue({
        isValid: false,
        billingAddress: null
      });
      // @ts-ignore
      spyOn(component, 'createPaymentDetails');

      component.next();

      expect(worldpayACHFacade.setACHPaymentFormValue).not.toHaveBeenCalled();
      expect(component['createPaymentDetails']).not.toHaveBeenCalled();
    });
  });
});
