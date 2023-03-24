import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldpayApmComponent } from './worldpay-apm-component.component';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { WorldpayApmService } from '../../../core/services/worldpay-apm/worldpay-apm.service';
import { WorldpayGooglepayService } from '../../../core/services/worldpay-googlepay/worldpay-googlepay.service';
import { WorldpayCheckoutService } from '../../../core/services/worldpay-checkout/worldpay-checkout.service';
import { By } from '@angular/platform-browser';
import { Component, DebugElement, EventEmitter, Input, Output } from '@angular/core';
import { Address, I18nTestingModule, MockTranslatePipe } from '@spartacus/core';
import { ApmData, ApmPaymentDetails, GooglePayMerchantConfiguration, PaymentMethod } from '../../../core/interfaces';
import { FormGroup } from '@angular/forms';
import { WorldpayApplepayService } from '../../../core/services/worldpay-applepay/worldpay-applepay.service';

@Component({
  selector: 'y-worldpay-billing-address',
  template: ''
})
class MockWorldpayBillingAddressComponent {
  @Input() billingAddressForm;
}

@Component({
  selector: 'y-worldpay-apm-googlepay',
  template: ''
})
class MockWorldpayApmGooglepayComponent {
  @Input() apm;
  @Input() billingAddressForm;
}

@Component({
  selector: 'y-worldpay-apm-tile',
  template: ''
})
class MockWorldpayApmTileComponent {
  @Input() apm: ApmData;
}

@Component({
  selector: 'y-worldpay-apm-ideal',
  template: ''
})
class MockWorldpayApmIdealComponent {
  @Input() apm: ApmData;
  @Input() billingAddressForm: FormGroup = new FormGroup({});
  @Output() setPaymentDetails = new EventEmitter<{ paymentDetails: ApmPaymentDetails; billingAddress: Address }>();
}

@Component({
  selector: 'cx-spinner',
  template: ''
})
class MockCxSpinnerComponent {

}

@Component({
  selector: 'worldpay-applepay',
  template: ''
})
class MockWorldpayApplePayComponent {

}

describe('WorldpayApmComponent', () => {
  let component: WorldpayApmComponent;
  let fixture: ComponentFixture<WorldpayApmComponent>;
  let element: DebugElement;
  let worldpayApmService;
  let worldpayCheckoutService: WorldpayCheckoutService;
  let worldpayApplePayService: WorldpayApplepayService;
  let spy;
  const apm = {
    code: PaymentMethod.Card,
    name: 'credit'
  };
  const apmSubject = new BehaviorSubject<ApmData>(apm);
  const apms = [
    { code: PaymentMethod.iDeal },
    { code: PaymentMethod.PayPal },
    { code: PaymentMethod.ApplePay },
    { code: PaymentMethod.GooglePay },
  ];

  const merchantConfig: GooglePayMerchantConfiguration = {
    allowedAuthMethods: [],
    allowedCardNetworks: [],
    cardType: 'VISA',
    environment: 'test',
    gatewayMerchantId: '1234',
    merchantName: 'johnsnow',
    merchantId: '12122'
  };

  class MockWorldpayApmService implements Partial<WorldpayApmService> {
    getSelectedAPMFromState(): Observable<ApmData> {
      return apmSubject;
    }

    getWorldpayAvailableApmsFromState(): Observable<ApmData[]> {
      return of(apms);
    }

    getApmComponentById(): Observable<ApmData> {
      return of(apm);
    }
  }

  class MockWorldpayGooglepayService implements Partial<WorldpayGooglepayService> {
    requestMerchantConfiguration(): void {
    }

    getMerchantConfigurationFromState(): Observable<GooglePayMerchantConfiguration> {
      return of(merchantConfig);
    }
  }

  class MockWorldpayCheckoutService {
    getLoading() {
      return of(false);
    }
  }

  class MockWorldpayApplepayService {
    applePayButtonAvailable() {
      return of(true);
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        I18nTestingModule,
      ],
      declarations: [
        WorldpayApmComponent,
        MockTranslatePipe,
        MockWorldpayBillingAddressComponent,
        MockWorldpayApmGooglepayComponent,
        MockWorldpayApmTileComponent,
        MockWorldpayApmIdealComponent,
        MockCxSpinnerComponent,
        MockWorldpayApplePayComponent,
      ],
      providers: [
        {
          provide: WorldpayApmService,
          useClass: MockWorldpayApmService
        },
        {
          provide: WorldpayGooglepayService,
          useClass: MockWorldpayGooglepayService
        },
        {
          provide: WorldpayCheckoutService,
          useClass: MockWorldpayCheckoutService
        },
        {
          provide: WorldpayApplepayService,
          useClass: MockWorldpayApplepayService
        },
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayApmComponent);
    component = fixture.componentInstance;
    element = fixture.debugElement;

    worldpayApmService = TestBed.inject(WorldpayApmService);
    worldpayCheckoutService = TestBed.inject(WorldpayCheckoutService);
    worldpayApplePayService = TestBed.inject(WorldpayApplepayService);
    spy = spyOn(worldpayCheckoutService, 'getLoading');
    apmSubject.next({
      code: PaymentMethod.Card
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not show spinner by default', () => {
    expect(element.query(By.css('cx-spinner'))).toBeFalsy();
  });

  it('should not show billing form for Card payments', () => {
    spy.and.returnValue(of(false));
    fixture = TestBed.createComponent(WorldpayApmComponent);
    component = fixture.componentInstance;
    element = fixture.debugElement;
    fixture.detectChanges();
    expect(element.query(By.css('y-worldpay-billing-address'))).toBeFalsy();
  });

  it('should show billing form for APM', (done) => {
    apmSubject.next({
      code: PaymentMethod.PayPal
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('y-worldpay-billing-address'))).toBeTruthy();
      done();
    });
  });

  it('should show component for Google Pay', (done) => {
    apmSubject.next({
      code: PaymentMethod.GooglePay
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('y-worldpay-apm-googlepay'))).toBeTruthy();
      done();
    });
  });

  it('should show component for iDeal', (done) => {
    apmSubject.next({
      code: PaymentMethod.iDeal
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('y-worldpay-apm-ideal'))).toBeTruthy();
      done();
    });
  });

  it('should show component for Apple Pay', (done) => {
    apmSubject.next({
      code: PaymentMethod.ApplePay
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('worldpay-applepay'))).toBeTruthy();
      done();
    });
  });

});
