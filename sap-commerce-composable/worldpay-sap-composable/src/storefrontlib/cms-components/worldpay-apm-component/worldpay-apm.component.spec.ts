import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldpayApmComponent } from './worldpay-apm.component';
import { BehaviorSubject, NEVER, Observable, of } from 'rxjs';
import { WorldpayApmService } from '../../../core/services/worldpay-apm/worldpay-apm.service';
import { WorldpayGooglepayService } from '../../../core/services/worldpay-googlepay/worldpay-googlepay.service';
import { By } from '@angular/platform-browser';
import { Component, DebugElement, EventEmitter, Input, Output } from '@angular/core';
import { Address, I18nTestingModule, MockTranslatePipe } from '@spartacus/core';
import { ApmData, ApmPaymentDetails, GooglePayMerchantConfiguration, PaymentMethod } from '../../../core/interfaces';
import { UntypedFormGroup } from '@angular/forms';
import { WorldpayApplepayService } from '../../../core/services/worldpay-applepay/worldpay-applepay.service';
import { OrderConnector } from '@spartacus/order/core';
import { LaunchDialogService } from '@spartacus/storefront';
import { WorldpayOrderService } from '../../../core/services';

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
  @Input() billingAddressForm: UntypedFormGroup = new UntypedFormGroup({});
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
  @Input() billingAddressForm;
}

class MockOrderConnector implements Partial<OrderConnector> {

}

describe('WorldpayApmComponent', () => {
  let component: WorldpayApmComponent;
  let fixture: ComponentFixture<WorldpayApmComponent>;
  let element: DebugElement;
  let worldpayApmService;
  let worldpayOrderService: WorldpayOrderService;
  let worldpayApplePayService: WorldpayApplepayService;
  let worldpayGooglepayService: WorldpayGooglepayService;
  let spy;
  let launchDialogService: LaunchDialogService;

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
    selectAPM(): void {
    }

    getSelectedAPMFromState(): Observable<ApmData> {
      return apmSubject;
    }

    getWorldpayAvailableApmsFromState(): Observable<ApmData[]> {
      return of(apms);
    }

    getApmComponentById(): Observable<ApmData> {
      return of(apm);
    }

    getLoading(): Observable<boolean> {
      return of(false);
    }
  }

  class MockWorldpayGooglepayService implements Partial<WorldpayGooglepayService> {
    requestMerchantConfiguration(): void {
    }

    getMerchantConfigurationFromState(): Observable<GooglePayMerchantConfiguration> {
      return of(merchantConfig);
    }
  }

  class MockWorldpayOrderService {
    getLoading() {
      return of(false);
    }
  }

  class MockWorldpayApplepayService {
    applePayButtonAvailable() {
      return of(true);
    }
  }

  class MockLaunchDialogService implements Partial<LaunchDialogService> {
    dialogClose: Observable<any> = NEVER;

    openDialogAndSubscribe(): void {
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [I18nTestingModule],
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
          provide: OrderConnector,
          useClass: MockOrderConnector
        },
        {
          provide: WorldpayApmService,
          useClass: MockWorldpayApmService
        },
        {
          provide: WorldpayGooglepayService,
          useClass: MockWorldpayGooglepayService
        },
        {
          provide: WorldpayOrderService,
          useClass: MockWorldpayOrderService
        },
        {
          provide: WorldpayApplepayService,
          useClass: MockWorldpayApplepayService
        },
        {
          provide: LaunchDialogService,
          useClass: MockLaunchDialogService
        },
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayApmComponent);
    component = fixture.componentInstance;
    element = fixture.debugElement;

    worldpayApmService = TestBed.inject(WorldpayApmService);
    worldpayOrderService = TestBed.inject(WorldpayOrderService);
    worldpayApplePayService = TestBed.inject(WorldpayApplepayService);
    worldpayGooglepayService = TestBed.inject(WorldpayGooglepayService);
    launchDialogService = TestBed.inject(LaunchDialogService);
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

  it('should trigger back functionality when an APM is selected', (done) => {
    spyOn(component, 'return').and.callThrough();
    apmSubject.next({
      code: 'KlARNA-SL'
    });
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      const backButton = fixture.debugElement.query(By.css('.btn-back'));
      expect(backButton).toBeTruthy();
      backButton.nativeElement.click();
      expect(component.return).toHaveBeenCalled();
      done();
    });
  });

  it('should hide back button when card payment method is selected', (done) => {
    spyOn(component, 'return').and.callThrough();
    apmSubject.next({
      code: PaymentMethod.Card
    });
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      const backButton = fixture.debugElement.query(By.css('.btn-back'));
      expect(backButton).toBeFalsy();
      done();
    });
  });

});
