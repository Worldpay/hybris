import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Address, CardType, Country, GlobalMessageService, I18nTestingModule, UserAddressService, UserPaymentService } from '@spartacus/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { StoreModule } from '@ngrx/store';
import { FormErrorsModule, IconTestingModule, ModalService } from '@spartacus/storefront';
import { NgSelectModule } from '@ng-select/ng-select';
import { WorldpayPaymentFormComponent } from './worldpay-payment-form.component';
import { WorldpayFraudsightService } from '../../../../core/services/worldpay-fraudsight/worldpay-fraudsight.service';
import { CheckoutDeliveryService, CheckoutPaymentService } from '@spartacus/checkout/core';
import { Component, Input } from '@angular/core';
import createSpy = jasmine.createSpy;

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

class MockCheckoutPaymentService {
  getCardTypes(): Observable<CardType[]> {
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

class MockCheckoutDeliveryService {
  getDeliveryAddress(): Observable<Address> {
    return of(deliveryAddress);
  }

  getAddressVerificationResults() {
    return of('lol');
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
        FormBuilder,
        {
          provide: CheckoutPaymentService,
          useClass: MockCheckoutPaymentService
        },
        {
          provide: CheckoutDeliveryService,
          useClass: MockCheckoutDeliveryService
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
          provide: ModalService,
          useValue: {}
        },

        {
          provide: WorldpayFraudsightService,
          useClass: MockWorldpayFraudsightService
        },
      ],
      declarations: [
        WorldpayPaymentFormComponent,
        MockWorldpayBillingAddressComponent,
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
  });
});
