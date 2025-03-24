import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { OccConfig, OccEndpointsService } from '@spartacus/core';
import { OccWorldpayApplepayAdapter } from './occ-worldpay-applepay.adapter';

const userId = 'userId';
const cartId = 'cartId';

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
    occEndpointsService = TestBed.inject(OccEndpointsService);
    spyOn(occEndpointsService, 'buildUrl').and.callThrough();
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('requestApplePayPaymentRequest', () => {
    it('should build URL and make GET request for Apple Pay Payment Request', () => {
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

    it('should handle error when requestApplePayPaymentRequest fails', () => {
      const error = new ErrorEvent('Network error');
      service.requestApplePayPaymentRequest(userId, cartId).subscribe({
        error: (err) => {
          expect(err).toBeTruthy();
        }
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'GET' &&
        req.urlWithParams === 'requestApplePayPaymentRequest'
      );

      mockReq.error(error);
    });
  });

  describe('validateApplePayMerchant', () => {
    it('should build URL and make POST request for Apple Pay Merchant Validation', () => {
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

    it('should handle error when validateApplePayMerchant fails', () => {
      const validationURL = 'https://valid.url.apple.com';
      const error = new ErrorEvent('Network error');
      service.validateApplePayMerchant(userId, cartId, validationURL).subscribe({
        error: (err) => {
          expect(err).toBeTruthy();
        }
      });

      const mockReq = httpMock.expectOne(
        req =>
          req.method === 'POST' &&
          req.urlWithParams === 'validateApplePayMerchant' &&
          req.body.validationURL === validationURL
      );

      mockReq.error(error);
    });
  });

  describe('authorizeApplePayPayment', () => {
    it('should build URL and make POST request for Apple Pay Payment Authorization', () => {
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

    it('should handle error when authorizeApplePayPayment fails', () => {
      const request = { request: 'bar' };
      const error = new ErrorEvent('Network error');
      service.authorizeApplePayPayment(userId, cartId, request).subscribe({
        error: (err) => {
          expect(err).toBeTruthy();
        }
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'authorizeApplePayPayment' &&
        req.body.request === 'bar'
      );

      mockReq.error(error);
    });
  });
});
