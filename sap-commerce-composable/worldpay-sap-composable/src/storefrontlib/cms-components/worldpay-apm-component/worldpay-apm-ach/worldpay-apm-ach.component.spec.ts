import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { Address, I18nTestingModule } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { WorldpayApmAchComponent } from './worldpay-apm-ach.component';
import { WorldpayACHFacade } from '../../../../core/facade/worldpay-ach.facade';
import { ACHPaymentForm, PaymentMethod } from '../../../../core/interfaces';
import { Component, Input } from '@angular/core';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';

const mockBillingAddress: Address = {
  'firstName': 'Test',
  'lastName': 'User',
  'line1': '2022 Miami Street',
  'line2': '',
  'town': 'South Bend',
  'country': {
    'isocode': 'BE'
  },
  'postalCode': '46613'
};

const mockACHBankAccountTypesState = of({
  loading: false,
  error: false,
  data: {
    'type1': 'Type 1',
    'type2': 'Type 2'
  }
});

const mockACHPaymentFormValue = {
  accountType: 'type1',
  accountNumber: '123456789',
  routingNumber: '987654321',
  checkNumber: '11111',
  companyName: 'Test Company',
  customIdentifier: '12345'
};

@Component({
  selector: 'y-worldpay-billing-address',
  template: ''
})
class MockWorldpayBillingAddressComponent {
  @Input() billingAddressForm;
}

describe('WorldpayApmAchComponent', () => {
  let component: WorldpayApmAchComponent;
  let fixture: ComponentFixture<WorldpayApmAchComponent>;
  const worldpayACHFacade = jasmine.createSpyObj('WorldpayACHFacade', ['getACHBankAccountTypesState', 'getACHPaymentFormValue', 'setACHPaymentFormValue']);
  let checkoutBillingAddressFormService: CheckoutBillingAddressFormService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        declarations: [
          WorldpayApmAchComponent,
          MockWorldpayBillingAddressComponent
        ],
        imports: [
          I18nTestingModule,
          FormErrorsModule,
          ReactiveFormsModule,
          NgSelectModule,
        ],
        providers: [
          {
            provide: WorldpayACHFacade,
            useValue: worldpayACHFacade
          },
          CheckoutBillingAddressFormService
        ],
      })
      .compileComponents();

    fixture = TestBed.createComponent(WorldpayApmAchComponent);
    checkoutBillingAddressFormService = TestBed.inject(CheckoutBillingAddressFormService);
    component = fixture.componentInstance;
    worldpayACHFacade.placeACHOrder;
    worldpayACHFacade.getACHPaymentFormValue.and.returnValue(of(null));
    worldpayACHFacade.getACHBankAccountTypesState.and.returnValue(mockACHBankAccountTypesState);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize billing address form and sameAsShippingAddress flag', () => {
    component.ngOnInit();

    expect(component.billingAddressForm).toBe(checkoutBillingAddressFormService.getBillingAddressForm());
    expect(component.sameAsShippingAddress).toBe(checkoutBillingAddressFormService.isBillingAddressSameAsDeliveryAddress());
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
          code: 'type1',
          name: 'Type 1'
        },
        {
          code: 'type2',
          name: 'Type 2'
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

  it('should set ACH payment form value on initialization', () => {
    const mockValue: ACHPaymentForm = {
      accountType: 'type1',
      accountNumber: '123456789',
      routingNumber: '987654321',
      checkNumber: '11111',
      companyName: 'Test Company',
      customIdentifier: '12345'
    };

    worldpayACHFacade.getACHPaymentFormValue.and.returnValue(of(mockValue));

    component.ngOnInit();

    expect(component.achForm.value).toEqual({
      ...mockValue,
      accountType: { code: mockValue.accountType }
    });

    expect(worldpayACHFacade.getACHPaymentFormValue).toHaveBeenCalled();
  });

  it('should emit payment details and billing address when form is valid and sameAsShippingAddress is false', () => {
    spyOn(checkoutBillingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
    spyOn(checkoutBillingAddressFormService, 'isBillingAddressFormValid').and.returnValue(true);
    spyOn(checkoutBillingAddressFormService, 'getBillingAddress').and.returnValue(mockBillingAddress);
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
      billingAddress: mockBillingAddress
    });
  });

  it('should emit payment details without billing address when form is valid and sameAsShippingAddress is true', () => {
    spyOn(checkoutBillingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
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
      billingAddress: null
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

  it('should allow only numbers in input fields', () => {
    component.ngOnInit();
    fixture.detectChanges();
    component.allowNumbersOnly({ target: { value: '123abc4' } } as any, 'accountNumber');
    fixture.detectChanges();
    expect(component.achForm.get('accountNumber').value).toEqual('1234');
  });

  it('should emit back event on return', () => {
    spyOn(component.back, 'emit');

    component.return();

    expect(component.back.emit).toHaveBeenCalled();
  });
});
