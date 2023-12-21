import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Address, ConverterService, OccConfig, OccEndpointsService } from '@spartacus/core';
import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { OccWorldpayGooglepayAdapter } from './occ-worldpay-googlepay.adapter';

const userId = 'userId';
const cartId = 'cartId';
const address: Address = {
  line1: 'The House',
  line2: 'The Road',
  postalCode: 'AA1 2BB'
};
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

class MockOccEndpointsService {
  buildUrl(url) {
    return url;
  }
}

describe('OccWorldpayGooglepayAdapter', () => {
  let service: OccWorldpayGooglepayAdapter;
  let httpMock: HttpTestingController;
  let converter: ConverterService;
  let occEndpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
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
      ]
    });

    service = TestBed.inject(OccWorldpayGooglepayAdapter);
    httpMock = TestBed.inject(HttpTestingController);
    converter = TestBed.inject(ConverterService);
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
});
