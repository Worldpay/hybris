import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { Address, LoggerService, PaymentDetails } from '@spartacus/core';
import { of } from 'rxjs';
import { WorldpayConnector } from 'worldpay-sap-composable-connectors';
import { WorldpayBillingAddressFormService } from 'worldpay-sap-composable-services';
import { MockWorldpayConnector } from 'worldpay-sap-composable-tests';

import { WorldpayApmBaseComponent } from './worldpay-apm-base.component';
import SpyObj = jasmine.SpyObj;

@Component({
  selector: 'app-test-apm',
  template: '',
  standalone: false
})
class TestApmComponent extends WorldpayApmBaseComponent {
}

describe('WorldpayApmBaseComponent', () => {
  let component: TestApmComponent;
  let fixture: ComponentFixture<TestApmComponent>;
  let worldpayBillingAddressFormService: SpyObj<WorldpayBillingAddressFormService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestApmComponent],
      providers: [
        WorldpayBillingAddressFormService,
        {
          provide: WorldpayConnector,
          useClass: MockWorldpayConnector
        },
        LoggerService
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TestApmComponent);
    component = fixture.componentInstance;
    worldpayBillingAddressFormService = TestBed.inject(WorldpayBillingAddressFormService) as SpyObj<WorldpayBillingAddressFormService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit back event when return is called', () => {
    spyOn(component.back, 'emit');
    component['return']();
    expect(component.back.emit).toHaveBeenCalled();
  });

  describe('validateAndGetBillingAddress()', () => {
    it('should return valid if sameAsDeliveryAddress is true', () => {
      spyOn(worldpayBillingAddressFormService, 'getSameAsDeliveryAddress').and.returnValue(of(true));

      const result = component['validateAndGetBillingAddress']();
      expect(result.isValid).toBeTrue();
      expect(result.billingAddress).toBeUndefined();
    });

    it('should return billing address from form value when form is valid', () => {
      worldpayBillingAddressFormService.setSameAsDeliveryAddress(false);
      spyOn(worldpayBillingAddressFormService, 'isBillingAddressFormValid').and.returnValue(true);
      const fb = TestBed.inject(FormBuilder);
      const form = fb.group({ street: ['Test Street'] });
      spyOn(worldpayBillingAddressFormService, 'getBillingAddressForm').and.returnValue(form);
      component.ngOnInit();

      const result = (component as any).validateAndGetBillingAddress();

      expect(result.isValid).toBe(true);
      expect(result.billingAddress).toEqual({ street: 'Test Street' });
    });

    it('should not return a billing address if form is invalid', () => {
      spyOn(worldpayBillingAddressFormService, 'getSameAsDeliveryAddress').and.returnValue(of(false));
      spyOn(worldpayBillingAddressFormService, 'isBillingAddressFormValid').and.returnValue(false);
      spyOn(worldpayBillingAddressFormService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
      const mockForm = jasmine.createSpyObj('FormGroup', ['markAllAsTouched']);
      spyOn(worldpayBillingAddressFormService, 'getBillingAddressForm').and.returnValue(mockForm);

      spyOn<any>(component, 'billingAddressFormService').and.returnValue(worldpayBillingAddressFormService);

      const result = component['validateAndGetBillingAddress']();
      expect(result.isValid).toBeFalsy();
      expect(result.billingAddress).toBeUndefined();
    });
  });

  describe('createPaymentDetails()', () => {
    it('should emit payment details and billing address when validation passes', () => {
      const paymentDetails = { code: 'testCode' } as PaymentDetails;
      const billingAddress = {
        firstName: 'John',
        lastName: 'Doe'
      } as Address;
      spyOn<any>(component, 'validateAndGetBillingAddress').and.returnValue({
        isValid: true,
        billingAddress,
      });
      spyOn(component.setPaymentDetails, 'emit');
      spyOn(component.isSubmitting$, 'next');

      (component as any).createPaymentDetails(paymentDetails);

      expect(component.isSubmitting$.next).toHaveBeenCalledWith(true);
      expect(component.setPaymentDetails.emit).toHaveBeenCalledWith({
        paymentDetails,
        billingAddress,
      });
    });

    it('should not emit payment details if validation fails', () => {
      const paymentDetails = { code: 'testCode' } as PaymentDetails;
      spyOn<any>(component, 'validateAndGetBillingAddress').and.returnValue({
        isValid: false,
        billingAddress: undefined,
      });
      spyOn(component.setPaymentDetails, 'emit');
      spyOn(component.isSubmitting$, 'next');

      (component as any).createPaymentDetails(paymentDetails);

      expect(component.isSubmitting$.next).not.toHaveBeenCalled();
      expect(component.setPaymentDetails.emit).not.toHaveBeenCalled();
    });
  });
});
