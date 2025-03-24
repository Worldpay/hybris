import { ElementRef } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { ActiveCartService } from '@spartacus/cart/base/core';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { Address, EventService, GlobalMessageService, GlobalMessageType, I18nTestingModule, RoutingService, WindowRef } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { FormErrorsModule } from '@spartacus/storefront';
import { MockWorldpayBillingAddressComponent } from '@worldpay-tests/components';
import { MockActiveCartService } from '@worldpay-tests/services/active-cart.service.mock';
import { EMPTY, Observable, of, throwError } from 'rxjs';
import { GooglePayMerchantConfiguration, GooglePayPaymentRequest } from '../../../../core/interfaces';
import { WorldpayCheckoutPaymentService, WorldpayGooglepayService, WorldpayOrderService } from '../../../../core/services';
import { LoadScriptService } from '../../../../core/utils';
import { WorldpayApmGooglepayComponent } from './worldpay-apm-googlepay.component';
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

class MockCheckoutPaymentService implements Partial<WorldpayCheckoutPaymentService> {
  setPaymentAddress(_address: Address): Observable<unknown> {
    return EMPTY;
  }
}

const googlePayAuth: GooglePayPaymentRequest = {
  apiVersion: 2,
  apiVersionMinor: 0,
};

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
  let mockWindowRef: WindowRef;
  let mockDocument: Document;
  let headElement: HTMLElement;

  beforeEach(async () => {
    mockDocument = document.implementation.createHTMLDocument();
    headElement = mockDocument.createElement('head');
    mockDocument.documentElement.appendChild(headElement);

    await TestBed.configureTestingModule({
        declarations: [
          WorldpayApmGooglepayComponent,
          MockWorldpayBillingAddressComponent
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
          WindowRef,
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
    mockWindowRef = TestBed.inject(WindowRef);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should navigate to order confirmation on successful order details retrieval', () => {
      const mockOrder = { code: '123' } as Order;
      spyOn(worldpayOrderService, 'getOrderDetails').and.returnValue(of(mockOrder));

      component.ngOnInit();

      expect(routingService.go).toHaveBeenCalledWith({ cxRoute: 'orderConfirmation' });
    });

    it('should not navigate to order confirmation if order details are empty', () => {
      spyOn(worldpayOrderService, 'getOrderDetails').and.returnValue(of({} as Order));

      component.ngOnInit();

      expect(routingService.go).not.toHaveBeenCalled();
    });

    it('should handle error when retrieving order details fails', () => {
      const error = new Error('Error');
      spyOn(console, 'error');
      spyOn(worldpayOrderService, 'getOrderDetails').and.returnValue(throwError(() => error));

      component.ngOnInit();

      expect(routingService.go).not.toHaveBeenCalled();
      expect(console.error).toHaveBeenCalledWith('Error while navigating to order confirmation page', error);
    });
  });

  describe('ngAfterViewInit', () => {
    it('should initialize Google Pay button if gpayBtn is available', () => {
      // @ts-ignore
      spyOn(component, 'initBtn');
      component.gpayBtn = { nativeElement: {} };

      component.ngAfterViewInit();

      expect(component['initBtn']).toHaveBeenCalled();
    });

    it('should not initialize Google Pay button if gpayBtn is not available', () => {
      // @ts-ignore
      spyOn(component, 'initBtn');
      component.gpayBtn = null;

      component.ngAfterViewInit();

      expect(component['initBtn']).not.toHaveBeenCalled();
    });
  });

  describe('authorisePayment', () => {
    beforeEach(() => {
      component['paymentsClient'] = {
        isReadyToPay: async () => ({ result: true }),
        loadPaymentData: async () => googlePayAuth,
      };
    });

    it('should authorise payment if billing address is same as delivery address', () => {
      spyOn(billingAddressFromService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
      spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(of(merchantConfig));
      spyOn(worldpayOrderService, 'startLoading');
      spyOn(component['paymentsClient'], 'loadPaymentData').and.returnValue(Promise.resolve({}));

      component['authorisePayment']();

      expect(worldpayOrderService.startLoading).toHaveBeenCalled();
    });

    it('should not authorise payment if billing address form is invalid', () => {
      spyOn(billingAddressFromService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
      spyOn(billingAddressFromService, 'isBillingAddressFormValid').and.returnValue(false);
      spyOn(worldpayCheckoutPaymentService, 'setPaymentAddress').and.returnValue(of({}));

      component['authorisePayment']();

      expect(worldpayCheckoutPaymentService.setPaymentAddress).not.toHaveBeenCalled();
    });

    it('should set payment address if billing address is not same as delivery address and form is valid', () => {
      spyOn(billingAddressFromService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
      spyOn(billingAddressFromService, 'isBillingAddressFormValid').and.returnValue(true);
      spyOn(billingAddressFromService, 'getBillingAddress').and.returnValue({});
      spyOn(worldpayCheckoutPaymentService, 'setPaymentAddress').and.returnValue(of({}));
      spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(of(merchantConfig));
      spyOn(worldpayOrderService, 'startLoading');
      spyOn(component['paymentsClient'], 'loadPaymentData').and.returnValue(Promise.resolve({}));

      component['authorisePayment']();

      expect(worldpayCheckoutPaymentService.setPaymentAddress).toHaveBeenCalled();
      expect(worldpayOrderService.startLoading).toHaveBeenCalled();
    });

    it('should handle error during payment authorisation', fakeAsync(() => {
      const error = 'Error';
      spyOn(console, 'error');
      spyOn(billingAddressFromService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
      spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(of(merchantConfig));
      spyOn(worldpayOrderService, 'startLoading');
      spyOn(component['paymentsClient'], 'loadPaymentData').and.returnValue(Promise.reject(error));
      spyOn(worldpayOrderService, 'clearLoading');
      component['authorisePayment']();
      tick();

      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'paymentForm.googlepay.authorisationFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(worldpayOrderService.clearLoading).toHaveBeenCalled();
      expect(console.error).toHaveBeenCalledWith('failed processing googlepay', { error });
    }));

    it('should clear loading state on error when retrieving merchant configuration', () => {
      spyOn(billingAddressFromService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
      spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(throwError(() => new Error('Error')));
      spyOn(worldpayOrderService, 'clearLoading');

      component['authorisePayment']();

      expect(worldpayOrderService.clearLoading).toHaveBeenCalled();
    });
  });

  describe('initBtn', () => {
    it('should initialize PaymentsClient if available', () => {
      let mockButton = document.createElement('button');
      // @ts-ignore
      spyOn(component, 'initPaymentsClient');
      spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(of(merchantConfig));
      component['paymentsClient'] = null;
      component.nativeWindow = {
        google: {
          payments: {
            api: {
              PaymentsClient: function() {
                return {
                  isReadyToPay: async () => ({ result: true }),
                  createButton: () => mockButton,
                  loadPaymentData: async () => googlePayAuth,
                };
              },
            }
          }
        }
      } as unknown as Window;

      // @ts-ignore
      component['initBtn']();

      expect(component['initPaymentsClient']).toHaveBeenCalledWith(merchantConfig);
    });

    it('should load Google Pay script if PaymentsClient is not available', () => {
      component.nativeWindow = {} as Window;
      spyOn(scriptService, 'loadScript').and.callFake((config) => config.onloadCallback());
      // @ts-ignore
      spyOn(component, 'initPaymentsClient');
      spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(of(merchantConfig));

      component['initBtn']();

      expect(scriptService.loadScript).toHaveBeenCalled();
      expect(component['initPaymentsClient']).toHaveBeenCalledWith(merchantConfig);
    });

    it('should not initialize PaymentsClient if already initialized', () => {
      component['paymentsClient'] = {};
      // @ts-ignore
      spyOn(component, 'initPaymentsClient');
      spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(of(merchantConfig));

      component['initBtn']();

      expect(component['initPaymentsClient']).not.toHaveBeenCalled();
    });

    it('should handle error when retrieving merchant configuration fails', () => {
      const error = new Error('Error');
      spyOn(console, 'error');
      spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(throwError(() => error));

      component['initBtn']();

      expect(console.error).toHaveBeenCalledWith('Error while initializing Google Pay button', error);
    });
  });

  describe('initPaymentsClient', () => {
    it('should initialize PaymentsClient and create Google Pay button if ready to pay', fakeAsync(() => {
      const mockMerchantConfig = { clientSettings: {} } as GooglePayMerchantConfiguration;
      const mockButton = document.createElement('button');
      component.gpayBtn = { nativeElement: document.createElement('div') } as ElementRef;
      window['google'] = {
        payments: {
          api: {
            PaymentsClient: function() {
              return {
                isReadyToPay: async () => ({ result: true }),
                createButton: () => mockButton
              };
            }
          }
        }
      };

      spyOn(component['ngZone'], 'run').and.callFake((fn) => fn());
      spyOn(component.gpayBtn.nativeElement, 'appendChild');

      component['initPaymentsClient'](mockMerchantConfig);
      tick();

      expect(component.gpayBtn.nativeElement.appendChild).toHaveBeenCalledWith(mockButton);
    }));

    it('should handle error when isReadyToPay fails', fakeAsync(() => {
      const mockMerchantConfig = { clientSettings: {} } as GooglePayMerchantConfiguration;
      const error = { statusMessage: 'Error' };
      window['google'] = {
        payments: {
          api: {
            PaymentsClient: function() {
              return {
                isReadyToPay: async () => {
                  throw error;
                }
              };
            }
          }
        }
      };

      spyOn(component['logger'], 'error');
      spyOn(component['cd'], 'detectChanges');

      component['initPaymentsClient'](mockMerchantConfig);
      tick();

      expect(component['logger'].error).toHaveBeenCalledWith('failed to initialize googlepay', error);
      expect(component['globalMessageService'].add).toHaveBeenCalledWith(
        { raw: error.statusMessage },
        GlobalMessageType.MSG_TYPE_ERROR
      );
      expect(component['cd'].detectChanges).toHaveBeenCalled();
    }));
  });

  it('should load Google Pay script if PaymentsClient is not available', () => {
    spyOn(scriptService, 'loadScript').and.callFake((config) => config.onloadCallback());
    // @ts-ignore
    spyOn(component, 'initPaymentsClient');

    component['paymentsClient'] = undefined;
    fixture.detectChanges();
    component['initBtn']();
    // @ts-ignore
    expect(component['initPaymentsClient']).toHaveBeenCalled();
  });

  it('should initialize PaymentsClient and create Google Pay button', () => {
    const mockMerchantConfig = { clientSettings: {} } as GooglePayMerchantConfiguration;

    component.nativeWindow = {
      // @ts-ignore
      google: {
        payments: {
          api: {
            PaymentsClient: {
              loadPaymentData: () => Promise.resolve({ paymentMethodData: {} }),
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
    expect(component['initPaymentsClient']).toHaveBeenCalledWith(mockMerchantConfig);
  });
});
