import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { PAYMENT_DETAILS_SERIALIZER } from '@spartacus/checkout/base/core';
import { Address, ConverterService, OccConfig, OccEndpointsService, PaymentDetails } from '@spartacus/core';
import { generateOneAddress, mockUserId } from 'worldpay-sap-composable-tests';
import { MockOccEndpointsService } from '../../../../tests/services/occ-endpoint.service.mock';
import { OccWorldpayCheckoutPaymentAdapter } from './occ-worldpay-checkout-payment.adapter';

const userId = mockUserId;
const cartId = 'cartId';
const cseToken = 'mockCseToken';

const mockBillingAddress: Address = generateOneAddress();

const paymentDetails: PaymentDetails = {
  accountHolderName: 'John Smith',
  cardNumber: '************6206',
  expiryMonth: '12',
  expiryYear: '2026',
  cardType: {
    name: 'Visa',
  },
  billingAddress: mockBillingAddress,
};

const MockOccModuleConfig: OccConfig = {
  backend: {
    occ: {
      baseUrl: '',
      prefix: ''
    }
  },

  context: {
    baseSite: ['']
  }
};

describe('OccWorldpayCheckoutPaymentAdapter', () => {
  let service: OccWorldpayCheckoutPaymentAdapter;
  let httpMock: HttpTestingController;
  let converter: ConverterService;
  let occEndpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [
        OccWorldpayCheckoutPaymentAdapter,
        {
          provide: OccConfig,
          useValue: MockOccModuleConfig
        },
        {
          provide: OccEndpointsService,
          useClass: MockOccEndpointsService
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
      ]
    });

    service = TestBed.inject(OccWorldpayCheckoutPaymentAdapter);
    httpMock = TestBed.inject(HttpTestingController);
    converter = TestBed.inject(ConverterService);
    occEndpointsService = TestBed.inject(OccEndpointsService);
    spyOn(occEndpointsService, 'buildUrl').and.callThrough();
    spyOn(converter, 'convert').and.callThrough();
  });

  it('should be able to create payment details', () => {
    let body = {};
    service.createWorldpayPaymentDetails(userId, cartId, paymentDetails, cseToken).subscribe();

    expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
      'createWorldpayPaymentDetails',
      {
        urlParams: {
          userId,
          cartId
        }
      }
    );

    const mockReq = httpMock.expectOne(req => {
      body = req.body;
      return req.method === 'POST' &&
             req.urlWithParams === 'createWorldpayPaymentDetails' &&
             req.body.accountHolderName === 'John Smith' &&
             req.body.cseToken === 'mockCseToken';
    });

    expect(mockReq.cancelled).toBeFalsy();
    expect(mockReq.request.responseType).toEqual('json');
    mockReq.flush(paymentDetails);

    expect(converter.convert).toHaveBeenCalledWith(
      body,
      PAYMENT_DETAILS_SERIALIZER
    );
  });

  it('should create Worldpay payment details successfully', () => {
    let body = {};
    service.createWorldpayPaymentDetails(userId, cartId, paymentDetails, cseToken).subscribe();

    expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
      'createWorldpayPaymentDetails',
      {
        urlParams: {
          userId,
          cartId
        }
      }
    );

    const mockReq = httpMock.expectOne(req => {
      body = req.body;
      return req.method === 'POST' &&
             req.urlWithParams === 'createWorldpayPaymentDetails' &&
             req.body.accountHolderName === 'John Smith' &&
             req.body.cseToken === 'mockCseToken';
    });

    expect(mockReq.cancelled).toBeFalsy();
    expect(mockReq.request.responseType).toEqual('json');
    mockReq.flush(paymentDetails);

    expect(converter.convert).toHaveBeenCalledWith(
      body,
      PAYMENT_DETAILS_SERIALIZER
    );
  });

  it('should handle error when creating Worldpay payment details', () => {
    service.createWorldpayPaymentDetails(userId, cartId, paymentDetails, cseToken).subscribe({
      error: (error) => {
        expect(error).toBeTruthy();
      }
    });

    const mockReq = httpMock.expectOne('createWorldpayPaymentDetails');
    mockReq.flush('Error', {
      status: 500,
      statusText: 'Server Error'
    });
  });

  it('should use existing payment details successfully', () => {
    const mockPaymentDetails: PaymentDetails = {
      id: '123',
      accountHolderName: 'John Doe'
    };
    const mockResponse: PaymentDetails = { ...mockPaymentDetails };

    service.useExistingPaymentDetails(userId, cartId, mockPaymentDetails).subscribe((response) => {
      expect(response).toEqual(mockResponse);
    });

    const mockReq = httpMock.expectOne('useExistingPaymentDetails');
    expect(mockReq.request.method).toBe('PUT');
    expect(mockReq.request.body).toEqual(mockPaymentDetails);
    mockReq.flush(mockResponse);
  });

  it('should handle error when using existing payment details', () => {
    const mockPaymentDetails: PaymentDetails = {
      id: '123',
      accountHolderName: 'John Doe'
    };

    service.useExistingPaymentDetails(userId, cartId, mockPaymentDetails).subscribe({
      error: (error) => {
        expect(error).toBeTruthy();
      }
    });

    const mockReq = httpMock.expectOne('useExistingPaymentDetails');
    mockReq.flush('Error', {
      status: 500,
      statusText: 'Server Error'
    });
  });
});
