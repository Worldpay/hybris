import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Address, OccConfig, OccEndpointsService } from '@spartacus/core';
import { mockUserId } from 'worldpay-sap-composable-tests';
import { MockOccEndpointsService } from '../../../../tests/services/occ-endpoint.service.mock';
import { OccWorldpayGooglepayAdapter } from './occ-worldpay-googlepay.adapter';

const userId = mockUserId;
const cartId = 'cartId';
const mockBillingAddress: Address = {
  firstName: 'John',
  lastName: 'Smith',
  line1: 'Buckingham Street 5',
  line2: '1A',
  phone: '(+11) 111 111 111',
  postalCode: 'MA8902',
  town: 'London',
  country: {
    name: 'test-country-name',
    isocode: 'UK',
  },
  formattedAddress: 'test-formattedAddress',
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

describe('OccWorldpayGooglepayAdapter', () => {
  let service: OccWorldpayGooglepayAdapter;
  let httpMock: HttpTestingController;
  let occEndpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [
        OccWorldpayGooglepayAdapter,
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

    service = TestBed.inject(OccWorldpayGooglepayAdapter);
    httpMock = TestBed.inject(HttpTestingController);
    occEndpointsService = TestBed.inject(OccEndpointsService);
    spyOn(occEndpointsService, 'buildUrl').and.callThrough();
  });

  it('should POST API getGooglePayMerchantConfiguration', () => {
    const validationURL = 'https://valid.url.apple.com';
    service.getGooglePayMerchantConfiguration(userId, cartId).subscribe();

    const mockReq = httpMock.expectOne(
      req =>
        req.method === 'GET' &&
        req.urlWithParams === 'getGooglePayMerchantConfiguration'
    );

    expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
      'getGooglePayMerchantConfiguration',
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

  it('should POST API authoriseGooglePayPayment', () => {
    const request = { request: 'bar' };
    service.authoriseGooglePayPayment(userId, cartId, 'toekn', mockBillingAddress, false).subscribe();

    const mockReq = httpMock.expectOne(req =>
      req.method === 'POST' &&
      req.urlWithParams === 'authoriseGooglePayPayment'
    );

    expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
      'authoriseGooglePayPayment',
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

  it('should POST getGooglePayMerchantConfiguration', () => {

    const token = '123-token-456';
    const billingAddress = { foo: 'bar' };
    const saved = false;

    service.authoriseGooglePayPayment(userId, cartId, token, billingAddress, saved).subscribe();

    const mockReq = httpMock.expectOne(req =>
      req.method === 'POST' &&
      req.urlWithParams === 'authoriseGooglePayPayment' &&
      req.body.token === token &&
      req.body.billingAddress === billingAddress &&
      req.body.saved === false
    );

    expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
      'authoriseGooglePayPayment',
      {
        urlParams: {
          userId,
          cartId
        }
      }
    );

    expect(mockReq.cancelled).toBeFalsy();

    mockReq.flush({
      token,
      billingAddress,
      saved
    });
  });

  describe('GooglePay', () => {

    it('should GET getGooglePayMerchantConfiguration', () => {
      service.getGooglePayMerchantConfiguration(userId, cartId).subscribe();

      const mockReq = httpMock.expectOne(req =>
        req.method === 'GET' &&
        req.urlWithParams === 'getGooglePayMerchantConfiguration'
      );

      expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
        'getGooglePayMerchantConfiguration',
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
  });

  it('should handle error when getGooglePayMerchantConfiguration fails', () => {
    const userId = 'user123';
    const cartId = 'cart123';

    service.getGooglePayMerchantConfiguration(userId, cartId).subscribe({
      error: (error) => {
        expect(error).toBeTruthy();
      }
    });

    const mockReq = httpMock.expectOne(req =>
      req.method === 'GET' &&
      req.urlWithParams === 'getGooglePayMerchantConfiguration'
    );

    expect(mockReq.cancelled).toBeFalsy();
    mockReq.flush('Error', {
      status: 500,
      statusText: 'Server Error'
    });
  });

  it('should handle error when authoriseGooglePayPayment fails', () => {
    const userId = 'user123';
    const cartId = 'cart123';
    const token = 'invalid-token';
    const billingAddress = { line1: '123 Main St' };
    const saved = false;

    service.authoriseGooglePayPayment(userId, cartId, token, billingAddress, saved).subscribe({
      error: (error) => {
        expect(error).toBeTruthy();
      }
    });

    const mockReq = httpMock.expectOne(req =>
      req.method === 'POST' &&
      req.urlWithParams === 'authoriseGooglePayPayment'
    );

    expect(mockReq.cancelled).toBeFalsy();
    mockReq.flush('Error', {
      status: 500,
      statusText: 'Server Error'
    });
  });
});
