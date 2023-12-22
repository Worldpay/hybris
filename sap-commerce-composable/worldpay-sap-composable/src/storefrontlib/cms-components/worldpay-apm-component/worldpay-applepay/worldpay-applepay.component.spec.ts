import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { Component, DebugElement, Input } from '@angular/core';
import { I18nTestingModule, MockTranslatePipe, RoutingService } from '@spartacus/core';
import { WorldpayApplepayComponent } from './worldpay-applepay.component';
import { WorldpayApplepayService } from '../../../../core/services/worldpay-applepay/worldpay-applepay.service';
import { OrderFacade } from '@spartacus/order/root';
import { By } from '@angular/platform-browser';
import { ApplePayAuthorization, ApplePayPaymentRequest, PlaceOrderResponse } from '../../../../core/interfaces';

@Component({
  selector: 'y-worldpay-billing-address',
  template: ''
})
class MockWorldpayBillingAddressComponent {
  @Input() billingAddressForm;
}

@Component({
  selector: 'cx-spinner',
  template: ''
})
class MockCxSpinnerComponent {

}

class MockWorldpayApplepayService implements Partial<WorldpayApplepayService> {
  applePayButtonAvailable() {
    return true;
  }

  createSession(paymentRequest: ApplePayPaymentRequest) {
    return {
      begin: () => {
        return of({
          order: { code: '00001' },
          transactionStatus: 'AUTHORISED'
        });
      }
    };
  }

  enableApplePayButton(): Observable<ApplePayPaymentRequest> {
    return of({
      countryCode: 'US',
      currencyCode: 'USD',
    });
  };

  getPaymentRequestFromState(): Observable<ApplePayPaymentRequest> {
    return of({
      countryCode: 'US',
      currencyCode: 'USD',
    });
  }

  getMerchantSessionFromState(): Observable<PlaceOrderResponse> {
    return of({
      order: {
        code: '00001'
      },
      transactionStatus: 'AUTHORISED',
      threeDSecureInfo: null,
      threeDSecureNeeded: false
    });
  }

  getPaymentAuthorizationFromState(): Observable<ApplePayAuthorization> {
    return of({
      order: { code: '00001' },
      transactionStatus: 'AUTHORISED'
    });
  }
}

class MockRoutingService implements Partial<RoutingService> {
  go = () => Promise.resolve(true);
}

class MockOrderFacade implements Partial<OrderFacade> {
  getOrderDetails() {
    return of({
      code: '00001'
    });
  }
}

describe('WorldpayApplepayComponent', () => {
  let component: WorldpayApplepayComponent;
  let fixture: ComponentFixture<WorldpayApplepayComponent>;
  let element: DebugElement;
  let orderFacade: OrderFacade;
  let routingService: RoutingService;
  let worldpayApplePayService: WorldpayApplepayService;
  let spy;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        I18nTestingModule,
      ],
      declarations: [
        WorldpayApplepayComponent,
        MockTranslatePipe,
        MockWorldpayBillingAddressComponent,
        MockCxSpinnerComponent,
      ],
      providers: [
        {
          provide: WorldpayApplepayService,
          useClass: MockWorldpayApplepayService
        },
        {
          provide: RoutingService,
          useClass: MockRoutingService
        },
        {
          provide: OrderFacade,
          useClass: MockOrderFacade
        },
        {
          provide: WorldpayApplepayService,
          useClass: MockWorldpayApplepayService
        },
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayApplepayComponent);
    component = fixture.componentInstance;
    element = fixture.debugElement;

    orderFacade = TestBed.inject(OrderFacade);
    worldpayApplePayService = TestBed.inject(WorldpayApplepayService);
    routingService = TestBed.inject(RoutingService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize ApplePay', () => {
    let enabled = null;
    worldpayApplePayService.enableApplePayButton().subscribe(response => enabled = response).unsubscribe();
    expect(enabled).toEqual({
      countryCode: 'US',
      currencyCode: 'USD',
    });
  });

  it('should NOT initialize ApplePay', () => {
    spyOn(worldpayApplePayService, 'applePayButtonAvailable').and.returnValue(false);
    spyOn(worldpayApplePayService, 'enableApplePayButton').and.callThrough();
    expect(worldpayApplePayService.enableApplePayButton).not.toHaveBeenCalled();
  });

  describe('Place Order', () => {
    it('should place order', () => {
      spyOn(worldpayApplePayService, 'applePayButtonAvailable').and.returnValue(true);
      spyOn(worldpayApplePayService, 'getPaymentRequestFromState').and.callThrough();
      spyOn(worldpayApplePayService, 'getMerchantSessionFromState').and.callThrough();
      spyOn(worldpayApplePayService, 'createSession').and.callThrough();
      spyOn(worldpayApplePayService, 'getPaymentAuthorizationFromState').and.callThrough();
      spyOn(orderFacade, 'getOrderDetails').and.callThrough();

      const placeOrderButton = fixture.debugElement.query(By.css('.applepay-buy-button'));
      expect(placeOrderButton).toBeTruthy();
      placeOrderButton.nativeElement.click();

      expect(worldpayApplePayService.getPaymentRequestFromState).toHaveBeenCalled();
      expect(worldpayApplePayService.getMerchantSessionFromState).toHaveBeenCalled();
      expect(worldpayApplePayService.getPaymentAuthorizationFromState).toHaveBeenCalled();
      expect(worldpayApplePayService.createSession).toHaveBeenCalledWith({
        countryCode: 'US',
        currencyCode: 'USD',
      });
      expect(orderFacade.getOrderDetails).toHaveBeenCalled();
    });

    it('should be rejected', () => {
      spyOn(worldpayApplePayService, 'applePayButtonAvailable').and.returnValue(true);
      spyOn(worldpayApplePayService, 'getPaymentRequestFromState').and.callThrough();
      spyOn(worldpayApplePayService, 'getMerchantSessionFromState').and.callThrough();
      spyOn(worldpayApplePayService, 'createSession').and.callThrough();
      spyOn(worldpayApplePayService, 'getPaymentAuthorizationFromState').and.returnValue(of({
        transactionStatus: 'DECLINED',
        order: {
          code: '0001'
        }
      }));
      spyOn(orderFacade, 'getOrderDetails').and.callThrough();

      const placeOrderButton = fixture.debugElement.query(By.css('.applepay-buy-button'));
      expect(placeOrderButton).toBeTruthy();
      placeOrderButton.nativeElement.click();

      expect(worldpayApplePayService.getPaymentRequestFromState).toHaveBeenCalled();
      expect(worldpayApplePayService.getMerchantSessionFromState).toHaveBeenCalled();
      expect(worldpayApplePayService.getPaymentAuthorizationFromState).toHaveBeenCalled();
      expect(worldpayApplePayService.createSession).toHaveBeenCalledWith({
        countryCode: 'US',
        currencyCode: 'USD',
      });
      expect(orderFacade.getOrderDetails).toHaveBeenCalled();
    });
  });

});
