import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { EMPTY, Observable, of } from 'rxjs';
import { Address, EventService, GlobalMessageService, GlobalMessageType, I18nTestingModule, RoutingService } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { WorldpayCheckoutPaymentService, WorldpayGooglepayService, WorldpayOrderService } from '../../../../core/services';
import { WorldpayApmGooglepayComponent } from './worldpay-apm-googlepay.component';
import { LoadScriptService } from '../../../../core/utils';
import { ActiveCartService } from '@spartacus/cart/base/core';
import { GooglePayMerchantConfiguration, GooglePayPaymentRequest } from '../../../../core/interfaces';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { Order } from '@spartacus/order/root';
import createSpy = jasmine.createSpy;

const merchantConfig: GooglePayMerchantConfiguration = {
  allowedAuthMethods: [],
  allowedCardNetworks: [],
  cardType: 'VISA',
  environment: 'test',
  gatewayMerchantId: '1234',
  merchantName: 'johnsnow',
  merchantId: '12122',
};

class MockWorldpayOrderService implements Partial<WorldpayOrderService> {
  setPlacedOrder() {

  }

  getOrderDetails(): Observable<Order> {
    return of({ code: '123' } as Order);
  }

  clearLoading() {

  }

  startLoading() {

  }
}

class MockWorldpayGooglepayService implements Partial<WorldpayGooglepayService> {
  requestMerchantConfiguration(): void {
  }

  getMerchantConfigurationFromState(): Observable<GooglePayMerchantConfiguration> {
    return of(merchantConfig);
  }

  createInitialPaymentRequest(merchantConfiguration): GooglePayPaymentRequest {
    return {
      apiVersion: 2,
      apiVersionMinor: 0
    };
  }

  createFullPaymentRequest(merchantConfiguration): GooglePayPaymentRequest {
    return {
      apiVersion: 2,
      apiVersionMinor: 0
    };
  }

  authoriseOrder(): Observable<unknown> {
    return EMPTY;
  }
}

class MockRoutingService implements Partial<RoutingService> {
  go = createSpy().and.returnValue(of(true).toPromise());
}

class MockGlobalMessageService implements Partial<GlobalMessageService> {
  add = createSpy().and.callThrough();
}

class MockActiveCartService implements Partial<ActiveCartService> {
  getActive = createSpy().and.returnValue(of('123'));
}

class MockCheckoutPaymentService implements Partial<WorldpayCheckoutPaymentService> {
  setPaymentAddress(_address: Address): Observable<unknown> {
    return EMPTY;
  }
}

describe('WorldpayApmGooglepayComponent', () => {
  let component: WorldpayApmGooglepayComponent;
  let fixture: ComponentFixture<WorldpayApmGooglepayComponent>;
  let routingService: RoutingService;
  let scriptService: LoadScriptService;
  let worldpayGooglePayService: WorldpayGooglepayService;
  let worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService;
  let worldpayOrderService: WorldpayOrderService;
  let globalMessageService: GlobalMessageService;
  let activeCartService: ActiveCartService;
  let billingAddressFromService: CheckoutBillingAddressFormService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        declarations: [
          WorldpayApmGooglepayComponent,
        ],
        imports: [
          I18nTestingModule,
          FormErrorsModule,
          ReactiveFormsModule,
          NgSelectModule,
        ],
        providers: [
          EventService,
          CheckoutBillingAddressFormService,
          {
            provide: RoutingService,
            useClass: MockRoutingService
          },
          {
            provide: GlobalMessageService,
            useClass: MockGlobalMessageService
          },
          {
            provide: WorldpayGooglepayService,
            useClass: MockWorldpayGooglepayService
          },
          {
            provide: WorldpayOrderService,
            useClass: MockWorldpayOrderService,
          },
          {
            provide: ActiveCartService,
            useClass: MockActiveCartService
          },
          {
            provide: WorldpayCheckoutPaymentService,
            useClass: MockCheckoutPaymentService
          }
        ],
      })
      .compileComponents();

    fixture = TestBed.createComponent(WorldpayApmGooglepayComponent);
    globalMessageService = TestBed.inject(GlobalMessageService);
    worldpayOrderService = TestBed.inject(WorldpayOrderService);
    activeCartService = TestBed.inject(ActiveCartService);
    routingService = TestBed.inject(RoutingService);
    worldpayCheckoutPaymentService = TestBed.inject(WorldpayCheckoutPaymentService);
    worldpayGooglePayService = TestBed.inject(WorldpayGooglepayService);
    billingAddressFromService = TestBed.inject(CheckoutBillingAddressFormService);
    scriptService = TestBed.inject(LoadScriptService);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to order confirmation on successful order details retrieval', () => {
    const mockOrder = { code: '123' } as Order;
    spyOn(worldpayOrderService, 'getOrderDetails').and.returnValue(of(mockOrder));

    component.ngOnInit();

    expect(routingService.go).toHaveBeenCalledWith({ cxRoute: 'orderConfirmation' });
  });

  it('should initialize Google Pay button after view init', () => {
    // @ts-ignore
    spyOn(component, 'initBtn').and.returnValue(component.gpayBtn = {
      nativeElement: {
        content: 'native Element'
      }
    });

    component.ngAfterViewInit();
    expect(component['initBtn']).toHaveBeenCalled();
  });

  it('should load Google Pay script if PaymentsClient is not available', () => {
    spyOn(scriptService, 'loadScript').and.callFake((config) => config.onloadCallback());
    // @ts-ignore
    spyOn(component, 'initPaymentsClient');

    component['initBtn']();

    expect(scriptService.loadScript).toHaveBeenCalled();
    // @ts-ignore
    expect(component.initPaymentsClient).toHaveBeenCalled();
  });

  it('should initialize PaymentsClient and create Google Pay button', () => {
    const mockMerchantConfig = { clientSettings: {} } as GooglePayMerchantConfiguration;

    component.nativeWindow = {
      // @ts-ignore
      google: {
        payments: {
          api: {
            PaymentsClient: {
              success: true
            }
          }
        }
      }
    };

    spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(of(mockMerchantConfig));
    // @ts-ignore
    spyOn(component, 'initPaymentsClient');

    // @ts-ignore
    component['initBtn']();

    // @ts-ignore
    expect(component.initPaymentsClient).toHaveBeenCalledWith(mockMerchantConfig);
  });

  it('should handle Google Pay authorisation failure', fakeAsync(() => {
    component['paymentsClient'] = {
      loadPaymentData: (data) => new Promise((resolve, reject) => reject('Expected Error Log'))
    };
    spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(of(merchantConfig));
    spyOn(billingAddressFromService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
    spyOn(worldpayOrderService, 'clearLoading');

    component['authorisePayment']();

    tick();
    expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'paymentForm.googlepay.authorisationFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
    expect(worldpayOrderService.clearLoading).toHaveBeenCalled();
  }));
});
