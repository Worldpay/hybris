import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Address, ConverterService, OccConfig, OccEndpointsService, PaymentDetails } from '@spartacus/core';
import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { OccWorldpayApmAdapter } from './occ-worldpay-apm.adapter';
import { ApmPaymentDetails, PaymentMethod } from '../../../interfaces';

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

describe('OccWorldpayApmAdapter', () => {
  let service: OccWorldpayApmAdapter;
  let httpMock: HttpTestingController;
  let converter: ConverterService;
  let occEndpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
      providers: [
        OccWorldpayApmAdapter,
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

    service = TestBed.inject(OccWorldpayApmAdapter);
    httpMock = TestBed.inject(HttpTestingController);
    converter = TestBed.inject(ConverterService);
    occEndpointsService = TestBed.inject(OccEndpointsService);
    spyOn(occEndpointsService, 'buildUrl').and.callThrough();
  });

  it('should POST authoriseApmRedirect without bankShopperCode', () => {
    const apm: ApmPaymentDetails = {
      code: PaymentMethod.PayPal
    };

    const save = true;
    service.authoriseApmRedirect(userId, cartId, apm, save).subscribe();

    const mockReq = httpMock.expectOne(req =>
      req.method === 'POST' &&
      req.urlWithParams === 'authoriseApmRedirect' &&
      req.body.paymentMethod === PaymentMethod.PayPal &&
      req.body.save === save
    );

    expect(mockReq.cancelled).toBeFalsy();

    mockReq.flush({
      paymentMethod: PaymentMethod.PayPal,
      save
    });
  });

  it('should POST authoriseApmRedirect with bankShopperCode for iDeal', () => {
    const apm: ApmPaymentDetails = {
      code: PaymentMethod.iDeal,
      shopperBankCode: 'ING'
    };
    const save = false;
    service.authoriseApmRedirect(userId, cartId, apm, save).subscribe();

    const mockReq = httpMock.expectOne(req =>
      req.method === 'POST' &&
      req.urlWithParams === 'authoriseApmRedirect' &&
      req.body.paymentMethod === PaymentMethod.iDeal &&
      req.body.shopperBankCode === 'ING'
    );

    expect(mockReq.cancelled).toBeFalsy();

    mockReq.flush({
      paymentMethod: PaymentMethod.iDeal,
      save,
      shopperBankCode: 'ING'
    });
  });

  it('should set APM payment info using serAPMPaymentInfo method', () => {
    const apm: ApmPaymentDetails = {
      code: PaymentMethod.PayPal,
      billingAddress: {
        ...address
      },
      name: 'PayPal',
    };

    service.setAPMPaymentInfo(userId, cartId, apm).subscribe();

    const mockReq = httpMock.expectOne(req =>
      req.method === 'POST' &&
      req.urlWithParams === 'setAPMPaymentInfo' &&
      req.body.billingAddress === apm.billingAddress &&
      req.body.apmName === apm.name &&
      req.body.apmCode === apm.code
    );

    expect(mockReq.cancelled).toBeFalsy();

    mockReq.flush({
      apm
    });
  });

  it('should get available apms using getAvailableApms method', () => {
    service.getAvailableApms(userId, cartId).subscribe();

    const mockReq = httpMock.expectOne(
      req =>
        req.method === 'GET' &&
        req.urlWithParams === 'getAvailableApms'
    );

    expect(mockReq.cancelled).toBeFalsy();

    mockReq.flush({});
  });

  it('should be able to use existing payment details using useExistingPaymentDetails method', () => {
    service.useExistingPaymentDetails(userId, cartId, paymentDetails)
      .subscribe(result => {
        expect(result).toEqual(paymentDetails);
      });

    expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
      'useExistingPaymentDetails',
      {
        urlParams: {
          userId,
          cartId
        },
        queryParams: {
          paymentDetailsId: 'aaa123'
        }
      }
    );

    const mockReq = httpMock.expectOne(req =>
      req.method === 'PUT' &&
      req.urlWithParams === 'useExistingPaymentDetails'
    );

    expect(mockReq.cancelled).toBeFalsy();
    expect(mockReq.request.responseType).toEqual('json');
    mockReq.flush(paymentDetails);
  });

  it('should be able to use existing payment details using useExistingPaymentDetails method', () => {
    service.useExistingPaymentDetails(userId, cartId, paymentDetails)
      .subscribe(result => {
        expect(result).toEqual(paymentDetails);
      });

    expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
      'useExistingPaymentDetails',
      {
        urlParams: {
          userId,
          cartId
        },
        queryParams: {
          paymentDetailsId: 'aaa123'
        }
      }
    );

    const mockReq = httpMock.expectOne(req =>
      req.method === 'PUT' &&
      req.urlWithParams === 'useExistingPaymentDetails'
    );

    expect(mockReq.cancelled).toBeFalsy();
    expect(mockReq.request.responseType).toEqual('json');
    mockReq.flush(paymentDetails);
  });
});
