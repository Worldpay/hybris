import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Address, ConverterService, OccConfig, OccEndpointsService, PaymentDetails } from '@spartacus/core';
import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { OccWorldpayApplepayAdapter } from './occ-worldpay-applepay.adapter';

const userId = 'userId';
const cartId = 'cartId';
const address: Address = {
  line1: 'The House',
  line2: 'The Road',
  postalCode: 'AA1 2BB'
};
const paymentDetails: PaymentDetails = {
  id: 'aaa123',
  cardNumber: '1234123412341234'
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

class MockOccEndpointsService {
  buildUrl(url) {
    return url;
  }
}

describe('OccWorldpayApplepayAdapter', () => {
  let service: OccWorldpayApplepayAdapter;
  let httpMock: HttpTestingController;
  let converter: ConverterService;
  let occEndpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
      providers: [
        OccWorldpayApplepayAdapter,
        {
          provide: OccConfig,
          useValue: MockOccModuleConfig
        },
        {
          provide: OccEndpointsService,
          useClass: MockOccEndpointsService
        },
      ]
    });

    service = TestBed.inject(OccWorldpayApplepayAdapter);
    httpMock = TestBed.inject(HttpTestingController);
    converter = TestBed.inject(ConverterService);
    occEndpointsService = TestBed.inject(OccEndpointsService);
    spyOn(occEndpointsService, 'buildUrl').and.callThrough();
  });

  it('should GET API requestApplePayPaymentRequest', () => {
    service.requestApplePayPaymentRequest(userId, cartId).subscribe();

    const mockReq = httpMock.expectOne(req =>
      req.method === 'GET' &&
      req.urlWithParams === 'requestApplePayPaymentRequest'
    );

    expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
      'requestApplePayPaymentRequest',
      {
        urlParams: {
          userId,
          cartId
        }
      }
    );

    expect(mockReq.cancelled).toBeFalsy();

    mockReq.flush({});
  });

  it('should POST API validateApplePayMerchant', () => {
    const validationURL = 'https://valid.url.apple.com';
    service.validateApplePayMerchant(userId, cartId, validationURL).subscribe();

    const mockReq = httpMock.expectOne(
      req =>
        req.method === 'POST' &&
        req.urlWithParams === 'validateApplePayMerchant' &&
        req.body.validationURL === validationURL
    );

    expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
      'validateApplePayMerchant',
      {
        urlParams: {
          userId,
          cartId
        }
      }
    );

    expect(mockReq.cancelled).toBeFalsy();

    mockReq.flush({ validationURL });
  });

  it('should POST API authorizeApplePayPayment', () => {
    const request = { request: 'bar' };
    service.authorizeApplePayPayment(userId, cartId, request).subscribe();

    const mockReq = httpMock.expectOne(req =>
      req.method === 'POST' &&
      req.urlWithParams === 'authorizeApplePayPayment' &&
      req.body.request === 'bar'
    );

    expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
      'authorizeApplePayPayment',
      {
        urlParams: {
          userId,
          cartId
        }
      }
    );

    expect(mockReq.cancelled).toBeFalsy();
    mockReq.flush({ request });
  });
});
