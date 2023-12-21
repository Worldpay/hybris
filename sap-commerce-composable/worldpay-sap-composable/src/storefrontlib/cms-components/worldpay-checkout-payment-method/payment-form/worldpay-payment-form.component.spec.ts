import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { Address, CardType, Country, GlobalMessageService, I18nTestingModule, QueryState, UserAddressService, UserPaymentService } from '@spartacus/core';
import { BehaviorSubject, EMPTY, Observable, of } from 'rxjs';
import { StoreModule } from '@ngrx/store';
import { FormErrorsModule, IconTestingModule, LaunchDialogService } from '@spartacus/storefront';
import { NgSelectModule } from '@ng-select/ng-select';
import { WorldpayPaymentFormComponent } from './worldpay-payment-form.component';
import { WorldpayFraudsightService } from '../../../../core/services/worldpay-fraudsight/worldpay-fraudsight.service';
import { Component, Input } from '@angular/core';
import { CheckoutDeliveryAddressFacade, CheckoutPaymentFacade } from '@spartacus/checkout/base/root';
import createSpy = jasmine.createSpy;

@Component({
  selector: 'cx-spinner',
  template: ''
})
class MockCxSpinnerComponent {

}

@Component({
  selector: 'y-worldpay-billing-address',
  template: ''
})
class MockWorldpayBillingAddressComponent {
  @Input() billingAddressForm;
}

let fsEnabled = new BehaviorSubject<boolean>(false);

class MockWorldpayFraudsightService {
  isFraudSightEnabled = createSpy('WorldpayFraudsightService.isFraudSightEnabled').and.callThrough();

  isFraudSightEnabledFromState() {
    return fsEnabled;
  }
}

const unitedStates: Country = {
  isocode: 'US',
  name: 'United States of Murica'
};

class MockUserPaymentService {
  getAllBillingCountries() {
    return of([unitedStates]);
  }

  loadBillingCountries() {

  }
}

class MockCheckoutPaymentFacade implements Partial<CheckoutPaymentFacade> {
  getPaymentCardTypes(): Observable<CardType[]> {
    return of([{
      code: 'visa',
      name: 'VISA'
    }]);
  }

  getSetPaymentDetailsResultProcess() {
    return of({ loading: false });
  }
}

const deliveryAddress: Address = {
  firstName: 'John',
  lastName: 'Doe',
  country: unitedStates
};

class MockCheckoutDeliveryAddressFacade implements Partial<CheckoutDeliveryAddressFacade> {
  getDeliveryAddress(): Observable<Address> {
    return of(deliveryAddress);
  }

  getDeliveryAddressState(): Observable<QueryState<Address>> {
    return of({
      loading: false,
      error: false,
      data: deliveryAddress
    });
  }

  getAddressVerificationResults() {
    return of('lol');
  }
}

class MockLaunchDialogService implements Partial<LaunchDialogService> {
  openDialogAndSubscribe() {
    return EMPTY;
  }
}

describe('WorldpayPaymentFormComponent', () => {
  let component: WorldpayPaymentFormComponent;
  let fixture: ComponentFixture<WorldpayPaymentFormComponent>;
  let worldpayFraudsightService: WorldpayFraudsightService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        imports: [
          StoreModule.forRoot({}),
          I18nTestingModule,
          ReactiveFormsModule,
          FormErrorsModule,
          NgSelectModule,
          IconTestingModule,
        ],
        providers: [
          UntypedFormBuilder,
          {
            provide: CheckoutPaymentFacade,
            useClass: MockCheckoutPaymentFacade
          },
          {
            provide: CheckoutDeliveryAddressFacade,
            useClass: MockCheckoutDeliveryAddressFacade
          },
          {
            provide: UserPaymentService,
            useClass: MockUserPaymentService
          },
          {
            provide: GlobalMessageService,
            useValue: {}
          },
          {
            provide: UserAddressService,
            useValue: {}
          },
          {
            provide: LaunchDialogService,
            useClass: MockLaunchDialogService
          },

          {
            provide: WorldpayFraudsightService,
            useClass: MockWorldpayFraudsightService
          },
        ],
        declarations: [
          WorldpayPaymentFormComponent,
          MockWorldpayBillingAddressComponent,
          MockCxSpinnerComponent
        ],
      })
      .compileComponents();
  });

  describe('should populate the form', () => {
    beforeEach(() => {
      fsEnabled = new BehaviorSubject<boolean>(false);
      fsEnabled.next(false);

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
      fsEnabled.next(true);

      fixture = TestBed.createComponent(WorldpayPaymentFormComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should add dateOfBirth field is FraudSight is enabled', (done) => {
      fsEnabled.next(true);

      fixture.whenStable().then(() => {
        expect(component.paymentForm.contains('dateOfBirth')).toBeTrue();
        done();
      });
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
});
