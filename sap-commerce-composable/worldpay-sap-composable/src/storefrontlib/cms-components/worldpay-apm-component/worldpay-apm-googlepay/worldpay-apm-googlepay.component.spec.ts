import { ElementRef } from '@angular/core';
import { ComponentFixture, fakeAsync, flush, TestBed, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { ActiveCartService } from '@spartacus/cart/base/core';
import { Address, EventService, GlobalMessageService, GlobalMessageType, I18nTestingModule, LoggerService, RoutingService, WindowRef } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { FormErrorsModule } from '@spartacus/storefront';
import { EMPTY, Observable, of, throwError } from 'rxjs';
import { WorldpayConnector } from 'worldpay-sap-composable-connectors';
import { MockActiveCartService, MockGlobalMessageService, MockRoutingService, MockWorldpayBillingAddressComponent, MockWorldpayConnector } from 'worldpay-sap-composable-tests';
import { GooglePayMerchantConfiguration, GooglePayPaymentRequest } from 'worldpay-sap-core';
import { WorldpayBillingAddressFormService, WorldpayCheckoutPaymentService, WorldpayGooglepayService, WorldpayOrderService } from '../../../../core/services';
import { LoadScriptService } from '../../../../core/utils';
import { WorldpayApmSubmitButtonsComponent } from '../worldpay-apm-submit-buttons/worldpay-apm-submit-buttons.component';
import { WorldpayApmGooglepayComponent } from './worldpay-apm-googlepay.component';

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

  createInitialPaymentRequest(): GooglePayPaymentRequest {
    return {
      apiVersion: 2,
      apiVersionMinor: 0
    };
  }

  createFullPaymentRequest(): GooglePayPaymentRequest {
    return {
      apiVersion: 2,
      apiVersionMinor: 0
    };
  }

  authoriseOrder(): Observable<unknown> {
    return EMPTY;
  }
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
  let billingAddressFromService: WorldpayBillingAddressFormService;
  let mockDocument: Document;
  let headElement: HTMLElement;
  let logger: LoggerService;

  beforeEach(async () => {
    mockDocument = document.implementation.createHTMLDocument();
    headElement = mockDocument.createElement('head');
    mockDocument.documentElement.appendChild(headElement);

    await TestBed.configureTestingModule({
      declarations: [
        WorldpayApmGooglepayComponent,
        WorldpayApmSubmitButtonsComponent,
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
        WindowRef,
        WorldpayBillingAddressFormService,
        {
          provide: WorldpayConnector,
          useClass: MockWorldpayConnector,
        },
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
        },
        LoggerService
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(WorldpayApmGooglepayComponent);
    globalMessageService = TestBed.inject(GlobalMessageService);
    worldpayOrderService = TestBed.inject(WorldpayOrderService);
    routingService = TestBed.inject(RoutingService);
    worldpayCheckoutPaymentService = TestBed.inject(WorldpayCheckoutPaymentService);
    worldpayGooglePayService = TestBed.inject(WorldpayGooglepayService);
    billingAddressFromService = TestBed.inject(WorldpayBillingAddressFormService);
    scriptService = TestBed.inject(LoadScriptService);
    logger = TestBed.inject(LoggerService);
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
      spyOn(logger, 'error');
      spyOn(worldpayOrderService, 'getOrderDetails').and.returnValue(throwError(() => error));

      component.ngOnInit();

      expect(routingService.go).not.toHaveBeenCalled();
      expect(logger.error).toHaveBeenCalledWith('Error while navigating to order confirmation page', error);
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
        loadPaymentData: async () => Promise.resolve(googlePayAuth),
      };
    });

    it('should authorise payment when billing address is same as delivery address', fakeAsync(() => {
      spyOn(billingAddressFromService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
      spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(of(merchantConfig));
      spyOn(worldpayGooglePayService, 'createFullPaymentRequest').and.returnValue(googlePayAuth);
      spyOn(component['paymentsClient'], 'loadPaymentData').and.resolveTo(googlePayAuth);
      spyOn(worldpayOrderService, 'startLoading');

      component['authorisePayment']();
      tick();
      flush();

      expect(worldpayOrderService.startLoading).toHaveBeenCalled();
    }));

    it('should not authorise payment if billing address form is invalid', () => {
      spyOn(billingAddressFromService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
      spyOn(billingAddressFromService, 'isBillingAddressFormValid').and.returnValue(false);
      spyOn(worldpayCheckoutPaymentService, 'setPaymentAddress').and.returnValue(of({}));

      component['authorisePayment']();

      expect(worldpayCheckoutPaymentService.setPaymentAddress).not.toHaveBeenCalled();
    });

    it('should set payment address if billing address is not same as delivery address and form is valid', fakeAsync(() => {
      spyOn(billingAddressFromService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(false);
      spyOn(billingAddressFromService, 'isBillingAddressFormValid').and.returnValue(true);
      spyOn(billingAddressFromService, 'getBillingAddress').and.returnValue({});
      spyOn(worldpayCheckoutPaymentService, 'setPaymentAddress').and.returnValue(of({}));
      spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(of(merchantConfig));
      spyOn(worldpayOrderService, 'startLoading');
      spyOn(component['paymentsClient'], 'loadPaymentData').and.resolveTo({});

      component['authorisePayment']();
      tick();
      flush();

      expect(worldpayCheckoutPaymentService.setPaymentAddress).toHaveBeenCalled();
      expect(worldpayOrderService.startLoading).toHaveBeenCalled();
    }));

    it('should handle error during payment authorisation', fakeAsync(() => {
      const error = 'Error';
      spyOn(logger, 'error');
      spyOn(billingAddressFromService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
      spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(of(merchantConfig));
      spyOn(worldpayOrderService, 'startLoading');
      spyOn(component['paymentsClient'], 'loadPaymentData').and.rejectWith(error);
      spyOn(worldpayOrderService, 'clearLoading');
      component['authorisePayment']();
      tick();
      flush();

      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'paymentForm.googlepay.authorisationFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(worldpayOrderService.clearLoading).toHaveBeenCalled();
      expect(logger.error).toHaveBeenCalledWith('failed processing googlepay', { error });
    }));

    it('should clear loading state on error when retrieving merchant configuration', () => {
      spyOn(logger, 'error');
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
      spyOn(logger, 'error');
      spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(throwError(() => error));

      component['initBtn']();

      expect(logger.error).toHaveBeenCalledWith('Error while initializing Google Pay button', error);
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

      spyOn(logger, 'error');
      spyOn(component['cd'], 'detectChanges');

      component['initPaymentsClient'](mockMerchantConfig);
      tick();

      expect(logger.error).toHaveBeenCalledWith('failed to initialize googlepay', error);
      expect(globalMessageService.add).toHaveBeenCalledWith(
        { raw: error.statusMessage },
        GlobalMessageType.MSG_TYPE_ERROR
      );
      expect(component['cd'].detectChanges).toHaveBeenCalled();
    }));
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
    expect(component.initPaymentsClient).toHaveBeenCalledWith(mockMerchantConfig);
  });

  it('should handle Google Pay authorisation failure', fakeAsync(() => {
    component['paymentsClient'] = {
      loadPaymentData: () => new Promise((resolve, reject) => reject('Expected Error Log'))
    };
    spyOn(worldpayGooglePayService, 'getMerchantConfigurationFromState').and.returnValue(of(merchantConfig));
    spyOn(billingAddressFromService, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
    spyOn(worldpayOrderService, 'clearLoading');
    spyOn(logger, 'error');

    component['authorisePayment']();

    tick();
    expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'paymentForm.googlepay.authorisationFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
    expect(worldpayOrderService.clearLoading).toHaveBeenCalled();
    expect(logger.error).toHaveBeenCalledWith('failed processing googlepay', { error: 'Expected Error Log' });
  }));
});
