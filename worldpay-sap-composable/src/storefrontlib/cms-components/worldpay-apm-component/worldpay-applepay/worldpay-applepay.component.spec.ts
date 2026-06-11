import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { I18nTestingModule, RoutingService } from '@spartacus/core';
import { OrderFacade } from '@spartacus/order/root';
import { SpinnerComponent } from '@spartacus/storefront';
import { Observable, of } from 'rxjs';
import { MockRoutingService, MockWorldpayBillingAddressComponent } from 'worldpay-sap-composable-tests';
import { ApplePayAuthorization, ApplePayMerchantSession, ApplePayPaymentRequest, WorldpayApplepayService } from '../../../../core';
import { WorldpayBillingAddressComponent } from '../../worldpay-billing-address/worldpay-billing-address.component';
import { WorldpayApplepayComponent } from './worldpay-applepay.component';

class MockWorldpayApplepayService implements Partial<WorldpayApplepayService> {
  applePayButtonAvailable() {
    return true;
  }

  createSession() {
    return {
      begin: () => of({
        order: { code: '00001' },
        transactionStatus: 'AUTHORISED'
      })
    };
  }

  enableApplePayButton(): Observable<ApplePayPaymentRequest> {
    return of({
      countryCode: 'US',
      currencyCode: 'USD',
    });
  }

  getPaymentRequestFromState(): Observable<ApplePayPaymentRequest> {
    return of({
      countryCode: 'US',
      currencyCode: 'USD',
    });
  }

  getMerchantSessionFromState(): Observable<ApplePayMerchantSession> {
    return of({ merchantSessionIdentifier: 'merchant-session' });
  }

  getPaymentAuthorizationFromState(): Observable<ApplePayAuthorization> {
    return of({
      order: { code: '00001' },
      transactionStatus: 'AUTHORISED'
    });
  }
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
  let orderFacade: OrderFacade;
  let worldpayApplePayService: WorldpayApplepayService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        I18nTestingModule,
        WorldpayApplepayComponent,
        SpinnerComponent,
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
    }).overrideComponent(WorldpayApplepayComponent, {
      remove: {
        imports: [
          WorldpayBillingAddressComponent,
        ]
      },
      add: {
        imports: [
          MockWorldpayBillingAddressComponent
        ]
      }
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayApplepayComponent);
    component = fixture.componentInstance;

    orderFacade = TestBed.inject(OrderFacade);
    worldpayApplePayService = TestBed.inject(WorldpayApplepayService);
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
