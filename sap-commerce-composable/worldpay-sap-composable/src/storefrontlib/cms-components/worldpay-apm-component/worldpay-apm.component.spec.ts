import { CdkAccordionModule } from '@angular/cdk/accordion';
import { Component, DebugElement, EventEmitter, Input, Output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Address, I18nTestingModule, LoggerService, MockTranslatePipe } from '@spartacus/core';
import { LaunchDialogService } from '@spartacus/storefront';
import { BehaviorSubject, Observable, of } from 'rxjs';
import {
  MockCxSpinnerComponent,
  MockLaunchDialogService,
  MockWorldpayAPMACHComponent,
  MockWorldpayBillingAddressComponent,
  MockWorldpayConnector
} from 'worldpay-sap-composable-tests';
import {
  ApmData,
  ApmPaymentDetails,
  GooglePayMerchantConfiguration,
  PaymentMethod,
  WorldpayApmService,
  WorldpayApplepayService,
  WorldpayBillingAddressFormService,
  WorldpayConnector,
  WorldpayGooglepayService,
  WorldpayOrderService
} from '../../../core';
import { MockWorldpayApmSepaComponent } from '../../../tests/components/worldpay-apm-sepa.component.mock';
import { WorldpayApmSubmitButtonsComponent } from './worldpay-apm-submit-buttons/worldpay-apm-submit-buttons.component';
import { WorldpayApmComponent } from './worldpay-apm.component';

@Component({
  selector: 'y-worldpay-apm-googlepay',
  template: '',
  standalone: false
})
class MockWorldpayApmGooglepayComponent {
  @Input() apm;
}

@Component({
  selector: 'y-worldpay-apm-tile',
  template: '',
  standalone: false
})
class MockWorldpayApmTileComponent {
  @Input() apm: ApmData;
}

@Component({
  selector: 'y-worldpay-apm-ideal',
  template: '',
  standalone: false
})
class MockWorldpayApmIdealComponent {
  @Input() apm: ApmData;
  @Output() setPaymentDetails = new EventEmitter<{ paymentDetails: ApmPaymentDetails; billingAddress: Address }>();
}

@Component({
  selector: 'worldpay-applepay',
  template: '',
  standalone: false
})
class MockWorldpayApplePayComponent {
}

describe('WorldpayApmComponent', () => {
  let component: WorldpayApmComponent;
  let fixture: ComponentFixture<WorldpayApmComponent>;
  let element: DebugElement;
  let worldpayApmService: WorldpayApmService;
  let logger: LoggerService;
  const apm = {
    code: PaymentMethod.Card,
    name: 'credit'
  };
  const apmSubject = new BehaviorSubject<ApmData>(apm);
  const apms = [
    { code: PaymentMethod.Card },
    { code: PaymentMethod.ACH },
    { code: PaymentMethod.ApplePay },
    { code: PaymentMethod.GooglePay },
    { code: PaymentMethod.iDeal },
    { code: PaymentMethod.SepaDirectDebit },
    { code: PaymentMethod.PayPal },
    { code: PaymentMethod.KlarnaSSL },
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

    getWorldpayAvailableApms(): Observable<ApmData[]> {
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

  class MockWorldpayApplepayService implements Partial<WorldpayApplepayService> {
    applePayButtonAvailable() {
      return true;
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        I18nTestingModule,
        CdkAccordionModule
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
        MockWorldpayAPMACHComponent,
        WorldpayApmSubmitButtonsComponent,
        MockWorldpayApmSepaComponent,
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
        WorldpayBillingAddressFormService,
        {
          provide: WorldpayConnector,
          useClass: MockWorldpayConnector,
        },
        LoggerService,
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayApmComponent);
    component = fixture.componentInstance;
    element = fixture.debugElement;
    logger = TestBed.inject(LoggerService);
    worldpayApmService = TestBed.inject(WorldpayApmService);
    component.apms = worldpayApmService.getWorldpayAvailableApms();
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
    }).catch(done.fail);
  });

  it('should show component for Google Pay', (done) => {
    apmSubject.next({
      code: PaymentMethod.GooglePay
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('y-worldpay-apm-googlepay'))).toBeTruthy();
      expect(element.query(By.css('y-worldpay-billing-address'))).toBeFalsy();
      done();
    }).catch(done.fail);
  });

  it('should show component for iDeal', (done) => {
    apmSubject.next({
      code: PaymentMethod.iDeal
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('y-worldpay-apm-ideal'))).toBeTruthy();
      expect(element.query(By.css('y-worldpay-billing-address'))).toBeFalsy();
      done();
    }).catch(done.fail);
  });

  it('should show component for ACH', (done) => {
    apmSubject.next({
      code: PaymentMethod.ACH
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('y-worldpay-apm-ach'))).toBeTruthy();
      expect(element.query(By.css('y-worldpay-billing-address'))).toBeFalsy();
      done();
    }).catch(done.fail);
  });

  it('should show component for Apple Pay', (done) => {
    apmSubject.next({
      code: PaymentMethod.ApplePay
    });

    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(element.query(By.css('worldpay-applepay'))).toBeTruthy();
      expect(element.query(By.css('y-worldpay-billing-address'))).toBeFalsy();
      done();
    }).catch(done.fail);
  });

  it('should trigger back functionality when an APM is selected', (done) => {
    const selectedApm = PaymentMethod.PayPal;
    // @ts-ignore
    spyOn(component, 'return').and.callThrough();
    apmSubject.next({
      code: selectedApm
    });
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      const headerButton = fixture.debugElement.query(By.css('#accordion-header-' + selectedApm));
      headerButton.nativeElement.click();
      const apmSubmitButtons = fixture.debugElement.query(By.css('.btn-back'));
      expect(apmSubmitButtons).toBeTruthy();
      apmSubmitButtons.nativeElement.click();
      expect(component['return']).toHaveBeenCalled();
      done();
    }).catch(done.fail);
  });

  it('should hide back button when card payment method is selected', (done) => {
    const selectedApm = PaymentMethod.Card;
    // @ts-ignore
    spyOn(component, 'return').and.callThrough();
    apmSubject.next({
      code: selectedApm
    });
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      const apmSubmitButtons = fixture.debugElement.query(By.css('.btn-back'));
      expect(apmSubmitButtons).toBeFalsy();
      done();
    }).catch(done.fail);
  });

  describe('show apm collapsible content', () => {
    const availableApms = [
      {
        code: PaymentMethod.Card,
        apmSubmitButtons: false
      },
      {
        code: PaymentMethod.ACH,
        apmSubmitButtons: false
      },
      {
        code: PaymentMethod.ApplePay,
        apmSubmitButtons: false
      },
      {
        code: PaymentMethod.GooglePay,
        apmSubmitButtons: false
      },
      {
        code: PaymentMethod.iDeal,
        apmSubmitButtons: false
      },
      {
        code: PaymentMethod.SepaDirectDebit,
        apmSubmitButtons: false
      },
      {
        code: PaymentMethod.PayPal,
        apmSubmitButtons: true
      },
      {
        code: PaymentMethod.KlarnaSSL,
        apmSubmitButtons: true
      },
    ];

    for (const apm of availableApms) {
      it(`should ${apm.code === PaymentMethod.Card ? 'not ' : ''}show the collapsible content when ${apm.code} is selected`, (done) => {
        // @ts-ignore
        spyOn(component, 'return').and.callThrough();
        apmSubject.next({
          code: apm.code
        });
        const headerButton = fixture.debugElement.query(By.css('#accordion-header-' + apm.code));
        headerButton.nativeElement.click();
        fixture.detectChanges();
        fixture.whenStable().then(() => {
          const apmContent = fixture.debugElement.query(By.css('#accordion-body-' + apm.code));
          const showApmSubmitButtons = apmContent.query(By.css(`[data-test-id="apm-submit-buttons-${apm.code}"]`));
          const apmSubmitButtons = apmContent.query(By.css('.btn-back'));
          if (apm.apmSubmitButtons) {
            expect(showApmSubmitButtons).toBeTruthy();
            apmSubmitButtons.nativeElement.click();
            expect(component['return']).toHaveBeenCalled();
          } else {
            expect(showApmSubmitButtons).toBeFalsy();
          }

          done();
        }).catch(done.fail);
      });
    }
  });

  describe('selectApmPaymentDetails', () => {
    it('should log an error and return early if no payment details are selected', () => {
      spyOn(logger, 'error');
      spyOn<any>(component, 'validateAndGetBillingAddress');
      spyOn<any>(component, 'createPaymentDetails');
      component['paymentDetails'] = null;

      component.selectApmPaymentDetails();

      expect(logger.error).toHaveBeenCalledWith('No payment details selected');
      expect(component['validateAndGetBillingAddress']).not.toHaveBeenCalled();
      expect(component['createPaymentDetails']).not.toHaveBeenCalled();
    });

    it('should return early if billing address validation fails', () => {
      spyOn(component.setPaymentDetails, 'emit');
      spyOn<any>(component, 'validateAndGetBillingAddress').and.returnValue({
        isValid: false,
        billingAddress: undefined,
      });
      spyOn<any>(component, 'createPaymentDetails').and.callThrough();
      component['paymentDetails'] = {
        code: 'testCode',
        name: 'testName'
      };

      component.selectApmPaymentDetails();

      expect(component['createPaymentDetails']).toHaveBeenCalled();
      expect(component['validateAndGetBillingAddress']).toHaveBeenCalled();
      expect(component.setPaymentDetails.emit).not.toHaveBeenCalled();
    });

    it('should create payment details when billing address validation passes', () => {
      const mockBillingAddress = { line1: '123 Test St' };
      // @ts-ignore
      spyOn(component, 'validateAndGetBillingAddress').and.returnValue({
        isValid: true,
        billingAddress: mockBillingAddress,
      });

      spyOn<any>(component, 'createPaymentDetails').and.callThrough();
      component['paymentDetails'] = {
        code: 'testCode',
        name: 'testName'
      };

      component.selectApmPaymentDetails();

      expect(component['createPaymentDetails']).toHaveBeenCalledWith(
        {
          code: 'testCode',
          name: 'testName'
        },
      );
      expect(component['validateAndGetBillingAddress']).toHaveBeenCalled();
    });
  });

  describe('selectDefaultCardPaymentMethod', () => {
    it('should select default card payment method when no APM is provided', () => {
      spyOn(worldpayApmService, 'selectAPM');
      component['selectDefaultCardPaymentMethod'](null);

      expect(worldpayApmService.selectAPM).toHaveBeenCalledWith({ code: PaymentMethod.Card });
    });

    it('should set payment details when an APM is provided', () => {
      const mockApm: ApmData = {
        code: 'testCode',
        name: 'testName'
      };
      component['selectDefaultCardPaymentMethod'](mockApm);

      expect(component['paymentDetails']).toEqual(mockApm);
    });
  });

  it('should emit back event when return is called', () => {
    spyOn(component.back, 'emit');

    component['return']();

    expect(component.back.emit).toHaveBeenCalled();
  });
});
