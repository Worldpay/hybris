import { HttpClientModule, HttpHeaders } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Address, ConverterService, OccConfig, OccEndpointsService, PaymentDetails } from '@spartacus/core';
import { ApmPaymentDetails, PaymentMethod } from '../../../interfaces';
import { OccWorldpayApmAdapter } from './occ-worldpay-apm.adapter';

const userId = 'userId';
const cartId = 'cartId';
const orderId = '000001';
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

const errorResponse = {
  error: {
    errors: [{
      type: 'test',
      message: 'Test error message'
    }]
  },
  headers: new HttpHeaders().set('xxx', 'xxx'),
  status: 500,
  statusText: 'Request Error',
};

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

  describe('authoriseApmRedirect', () => {
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

    it('should POST authoriseApmRedirect with bankShopperCode', () => {
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

    it('should handle error when authoriseApmRedirect fails', () => {
      const apm: ApmPaymentDetails = {
        code: PaymentMethod.PayPal
      };
      const save = true;
      const errorResponse = {
        status: 500,
        statusText: 'Server Error'
      };

      service.authoriseApmRedirect(userId, cartId, apm, save).subscribe({
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'authoriseApmRedirect'
      );

      mockReq.flush(null, errorResponse);
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
  });

  describe('getAvailableAmps', () => {
    it('should get available APMs for a given cart', () => {
      service.getAvailableApms(userId, cartId).subscribe();

      const mockReq = httpMock.expectOne(req =>
        req.method === 'GET' &&
        req.urlWithParams === 'getAvailableApms'
      );

      expect(mockReq.cancelled).toBeFalsy();
      mockReq.flush([]);
    });

    it('should handle error when getAvailableApms fails', () => {
      const errorResponse = {
        status: 500,
        statusText: 'Server Error'
      };

      service.getAvailableApms(userId, cartId).subscribe({
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'GET' &&
        req.urlWithParams === 'getAvailableApms'
      );

      mockReq.flush(null, errorResponse);
    });
  });

  describe('placeRedirectOrder', () => {
    it('should place redirect order successfully', () => {
      service.placeRedirectOrder(userId, cartId).subscribe((order) => {
        expect(order).toBeTruthy();
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'placeRedirectOrder'
      );

      expect(mockReq.cancelled).toBeFalsy();
      mockReq.flush({});
    });

    it('should handle error when placeRedirectOrder fails', () => {

      service.placeRedirectOrder(userId, cartId).subscribe({
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'placeRedirectOrder'
      );

      mockReq.flush(errorResponse.error, {
        status: errorResponse.status,
        statusText: errorResponse.statusText,
      });
    });
  });

  describe('placeBankTransferOrderRedirect', () => {
    it('should place bank transfer order redirect successfully', () => {
      service.placeBankTransferOrderRedirect(userId, cartId, orderId).subscribe((order) => {
        expect(order).toBeTruthy();
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'placeBankTransferRedirectOrder'
      );

      expect(mockReq.cancelled).toBeFalsy();
      mockReq.flush({});
    });

    it('should handle error when placeBankTransferOrderRedirect fails', () => {

      service.placeBankTransferOrderRedirect(userId, cartId, orderId).subscribe({
        error: (error) => {
          expect(error.status).toBe(500);
          expect(error.details).toEqual([{
            type: 'test',
            message: 'Test error message'
          }]);
        }
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'placeBankTransferRedirectOrder'
      );

      mockReq.flush(errorResponse.error, {
        status: errorResponse.status,
        statusText: errorResponse.statusText,
      });
    });
  });

  describe('setAPMPaymentInfo', () => {
    it('should set APM payment info successfully', () => {
      const apm: ApmPaymentDetails = {
        code: PaymentMethod.PayPal,
        billingAddress: {
          ...address
        },
        name: 'PayPal',
      };

      service.setAPMPaymentInfo(userId, cartId, apm).subscribe((cart) => {
        expect(cart).toBeTruthy();
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'setAPMPaymentInfo' &&
        req.body.billingAddress === apm.billingAddress &&
        req.body.apmName === apm.name &&
        req.body.apmCode === apm.code
      );

      expect(mockReq.cancelled).toBeFalsy();
      mockReq.flush({});
    });

    it('should handle error when setAPMPaymentInfo fails', () => {
      const apm: ApmPaymentDetails = {
        code: PaymentMethod.PayPal,
        billingAddress: {
          ...address
        },
        name: 'PayPal',
      };

      service.setAPMPaymentInfo(userId, cartId, apm).subscribe({
        error: (error) => {
          expect(error.status).toBe(500);
          expect(error.details).toEqual([{
            type: 'test',
            message: 'Test error message'
          }]);
        }
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'setAPMPaymentInfo'
      );

      mockReq.flush(errorResponse.error, {
        status: errorResponse.status,
        statusText: errorResponse.statusText,
      });
    });

    it('should set APM payment info with shopperBankCode', () => {
      const apm: ApmPaymentDetails = {
        code: PaymentMethod.iDeal,
        billingAddress: {
          ...address
        },
        name: 'iDeal',
        shopperBankCode: 'ING'
      };

      service.setAPMPaymentInfo(userId, cartId, apm).subscribe((cart) => {
        expect(cart).toBeTruthy();
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'setAPMPaymentInfo' &&
        req.body.billingAddress === apm.billingAddress &&
        req.body.apmName === apm.shopperBankCode &&
        req.body.apmCode === apm.code
      );

      expect(mockReq.cancelled).toBeFalsy();
      mockReq.flush({});
    });
  });

  describe('useExistingPaymentDetails', () => {
    it('should use existing payment details successfully', () => {
      service.useExistingPaymentDetails(userId, cartId, paymentDetails).subscribe((details) => {
        expect(details).toEqual(paymentDetails);
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
        req.urlWithParams === 'useExistingPaymentDetails' &&
        req.body.id === paymentDetails.id
      );

      expect(mockReq.cancelled).toBeFalsy();
      expect(mockReq.request.responseType).toEqual('json');
      mockReq.flush(paymentDetails);
    });

    it('should handle error when useExistingPaymentDetails fails', () => {
      service.useExistingPaymentDetails(userId, cartId, paymentDetails).subscribe({
        error: (error) => {
          expect(error.status).toBe(500);
          expect(error.details).toEqual([{
            type: 'test',
            message: 'Test error message'
          }]);
        }
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'PUT' &&
        req.urlWithParams === 'useExistingPaymentDetails'
      );

      mockReq.flush(errorResponse.error, {
        status: errorResponse.status,
        statusText: errorResponse.statusText,
      });
    });
  });
});
