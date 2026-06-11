import { provideHttpClient, withInterceptorsFromDi, } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting, } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ConverterService, Occ, OccConfig, OccEndpointsService, PAYMENT_DETAILS_NORMALIZER, PaymentDetails } from '@spartacus/core';
import { Observable } from 'rxjs';
import { ApmPaymentDetails, ApmPaymentDetailsListResponse } from '../../../interfaces';
import { OccWorldpayUserPaymentAdapter } from './occ-worldpay-user-payment.adapter';

const username = 'mockUsername';
const cartId = 'cartId';

export const mockOccModuleConfig: OccConfig = {
  backend: {
    occ: {
      baseUrl: '',
      prefix: '',
    },
  },

  context: {
    baseSite: [''],
  },
};

export class MockOccEndpointsService implements Partial<OccEndpointsService> {
  buildUrl(endpointKey: string, _urlParams?: object, _queryParams?: object) {
    if (!endpointKey.startsWith('/')) {
      endpointKey = '/' + endpointKey;
    }
    return endpointKey;
  }

  getBaseUrl() {
    return '';
  }

  isConfigured() {
    return true;
  }
}

describe('OccWorldpayUserPaymentAdapter', () => {
  let occUserPaymentAdapter: OccWorldpayUserPaymentAdapter;
  let httpMock: HttpTestingController;
  let converter: ConverterService;
  let occEnpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [
        OccWorldpayUserPaymentAdapter,
        {
          provide: OccConfig,
          useValue: mockOccModuleConfig
        },
        {
          provide: OccEndpointsService,
          useClass: MockOccEndpointsService,
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
      ],
    });

    occUserPaymentAdapter = TestBed.inject(OccWorldpayUserPaymentAdapter);
    httpMock = TestBed.inject(HttpTestingController);
    converter = TestBed.inject(ConverterService);
    occEnpointsService = TestBed.inject(OccEndpointsService);
    spyOn(converter, 'pipeableMany').and.callThrough();
    spyOn(occEnpointsService, 'buildUrl').and.callThrough();
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('load user payment methods', () => {
    it('should load user payment methods for a given user id', () => {
      const mockPayment1: PaymentDetails = {
        accountHolderName: 'mockAccountHolderName1',
      };
      const mockPayment2: PaymentDetails = {
        accountHolderName: 'mockAccountHolderName2',
      };
      const mockUserPaymentMethods: Occ.PaymentDetailsList = {
        payments: [mockPayment1, mockPayment2],
      };

      occUserPaymentAdapter.loadAll(username).subscribe((result) => {
        expect(result).toEqual(mockUserPaymentMethods.payments);
      });

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');

      expect(occEnpointsService.buildUrl).toHaveBeenCalledWith(
        'paymentDetailsAll',
        {
          urlParams: { userId: username },
        }
      );
      expect(mockReq.cancelled).toBeFalsy();
      expect(mockReq.request.responseType).toEqual('json');
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should use converter', () => {
      occUserPaymentAdapter.loadAll(username).subscribe();
      httpMock
        .expectOne((req) => req.method === 'GET')
        .flush({});
      expect(converter.pipeableMany).toHaveBeenCalledWith(
        PAYMENT_DETAILS_NORMALIZER
      );
    });
  });

  describe('set default user payment method', () => {
    it('should set default payment method for given user', () => {
      const mockPayment: PaymentDetails = {
        defaultPayment: true,
        id: '123',
      };

      occUserPaymentAdapter
        .setDefault(username, mockPayment.id)
        .subscribe((result) => {
          expect(result).toEqual('');
        });

      const mockReq = httpMock.expectOne((req) => req.method === 'PATCH' && req.body.defaultPayment === true);

      expect(occEnpointsService.buildUrl).toHaveBeenCalledWith(
        'paymentDetail',
        {
          urlParams: {
            userId: username,
            paymentDetailId: mockPayment.id
          },
        }
      );
      expect(mockReq.cancelled).toBeFalsy();
      expect(mockReq.request.responseType).toEqual('json');
      mockReq.flush('');
    });
  });

  describe('delete user payment method', () => {
    it('should delete payment method for given user', () => {
      const mockPayment: PaymentDetails = {
        id: '123',
      };

      occUserPaymentAdapter
        .delete(username, mockPayment.id)
        .subscribe((result) => expect(result).toEqual(''));

      const mockReq = httpMock.expectOne((req) => req.method === 'DELETE');

      expect(occEnpointsService.buildUrl).toHaveBeenCalledWith(
        'paymentDetail',
        {
          urlParams: {
            userId: username,
            paymentDetailId: mockPayment.id
          },
        }
      );
      expect(mockReq.cancelled).toBeFalsy();
      expect(mockReq.request.responseType).toEqual('json');
      mockReq.flush('');
    });
  });

  describe('load user for cart payment methods', () => {
    it('should load user payment methods for a given user id and cart id', () => {
      const mockPayment1: PaymentDetails = {
        accountHolderName: 'mockAccountHolderName1',
      };
      const mockPayment2: PaymentDetails = {
        accountHolderName: 'mockAccountHolderName2',
      };
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = {
        payments: [mockPayment1, mockPayment2]
      };

      occUserPaymentAdapter.loadAllForCart(username, cartId).subscribe((result) => {
        expect(result).toEqual(mockUserPaymentMethods.payments);
      });

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');

      expect(occEnpointsService.buildUrl).toHaveBeenCalledWith(
        'paymentDetailsAllForCart',
        {
          urlParams: {
            userId: username,
            cartId
          },
          queryParams: {
            saved: true
          }
        }
      );
      expect(mockReq.cancelled).toBeFalsy();
      expect(mockReq.request.responseType).toEqual('json');
      mockReq.flush(mockUserPaymentMethods);
    });

    /*it('should use converter', () => {
      occUserPaymentAdapter.loadAllForCart(username).subscribe();
      httpMock
        .expectOne((req) => {
          return req.method === 'GET';
        })
        .flush({});
      expect(converter.pipeableMany).toHaveBeenCalledWith(
        PAYMENT_DETAILS_NORMALIZER
      );
    });*/
  });

  describe('loadAllForCart', () => {
    it('should load payment methods for a given user id and cart id', () => {
      const mockPayment1: PaymentDetails = {
        accountHolderName: 'mockAccountHolderName1',
      };
      const mockPayment2: PaymentDetails = {
        accountHolderName: 'mockAccountHolderName2',
      };
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = {
        payments: [mockPayment1, mockPayment2]
      };

      occUserPaymentAdapter.loadAllForCart(username, cartId).subscribe((result) => {
        expect(result).toEqual(mockUserPaymentMethods.payments);
      });

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');

      expect(occEnpointsService.buildUrl).toHaveBeenCalledWith(
        'paymentDetailsAllForCart',
        {
          urlParams: {
            userId: username,
            cartId
          },
          queryParams: {
            saved: true
          }
        }
      );
      expect(mockReq.cancelled).toBeFalsy();
      expect(mockReq.request.responseType).toEqual('json');
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should return empty array when payments array is undefined', () => {
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = {
        payments: undefined
      };

      occUserPaymentAdapter.loadAllForCart(username, cartId).subscribe((result) => {
        expect(result).toEqual([]);
      });

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should return empty array when payments array is null', () => {
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = {
        payments: null
      };

      occUserPaymentAdapter.loadAllForCart(username, cartId).subscribe((result) => {
        expect(result).toEqual([]);
      });

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should return empty array when response has no payments property', () => {
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = {};

      occUserPaymentAdapter.loadAllForCart(username, cartId).subscribe((result) => {
        expect(result).toEqual([]);
      });

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should set Content-Type header to application/json', () => {
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = {
        payments: []
      };

      occUserPaymentAdapter.loadAllForCart(username, cartId).subscribe();

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');
      expect(mockReq.request.headers.get('Content-Type')).toEqual('application/json');
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should pass userId to buildUrl correctly', () => {
      const testUserId = 'testUser123';
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = { payments: [] };

      occUserPaymentAdapter.loadAllForCart(testUserId, cartId).subscribe();

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');
      expect(occEnpointsService.buildUrl).toHaveBeenCalledWith(
        jasmine.any(String),
        jasmine.objectContaining({
          urlParams: jasmine.objectContaining({
            userId: testUserId
          })
        })
      );
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should pass cartId to buildUrl correctly', () => {
      const testCartId = 'cart123';
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = { payments: [] };

      occUserPaymentAdapter.loadAllForCart(username, testCartId).subscribe();

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');
      expect(occEnpointsService.buildUrl).toHaveBeenCalledWith(
        jasmine.any(String),
        jasmine.objectContaining({
          urlParams: jasmine.objectContaining({
            cartId: testCartId
          })
        })
      );
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should pass saved true queryParam to buildUrl', () => {
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = { payments: [] };

      occUserPaymentAdapter.loadAllForCart(username, cartId).subscribe();

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');
      expect(occEnpointsService.buildUrl).toHaveBeenCalledWith(
        'paymentDetailsAllForCart',
        {
          urlParams: {
            userId: username,
            cartId
          },
          queryParams: {
            saved: true
          }
        }
      );
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should handle single payment in response', () => {
      const mockPayment: PaymentDetails = {
        accountHolderName: 'singlePayment',
      };
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = {
        payments: [mockPayment]
      };

      occUserPaymentAdapter.loadAllForCart(username, cartId).subscribe((result) => {
        expect(result.length).toEqual(1);
        expect(result[0]).toEqual(mockPayment);
      });

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should handle multiple payments in response', () => {
      const mockPayments: PaymentDetails[] = Array(5).fill({}).map((_, i) => ({
        accountHolderName: `payment${i}`,
        id: `${i}`
      }));
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = {
        payments: mockPayments
      };

      occUserPaymentAdapter.loadAllForCart(username, cartId).subscribe((result) => {
        expect(result.length).toEqual(5);
        expect(result).toEqual(mockPayments);
      });

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should return observable of ApmPaymentDetails array', (done) => {
      const mockPayment: PaymentDetails = {
        accountHolderName: 'mockPayment',
      };
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = {
        payments: [mockPayment]
      };

      const result = occUserPaymentAdapter.loadAllForCart(username, cartId);
      expect(result instanceof Observable).toBeTrue();

      result.subscribe((payments) => {
        expect(Array.isArray(payments)).toBeTrue();
        done();
      });

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should handle empty payments array', () => {
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = {
        payments: []
      };

      occUserPaymentAdapter.loadAllForCart(username, cartId).subscribe((result) => {
        expect(result).toEqual([]);
        expect(result.length).toEqual(0);
      });

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');
      mockReq.flush(mockUserPaymentMethods);
    });

    it('should handle payment details with all properties', () => {
      const mockPaymentWithAllProperties: ApmPaymentDetails = {
        id: '123',
        accountHolderName: 'John Doe',
        cardNumber: '4111111111111111',
        expiryMonth: '12',
        expiryYear: '2025',
        cvn: '123',
        billingAddress: {
          firstName: 'John',
          lastName: 'Doe',
          line1: '123 Main St',
          town: 'City',
          postalCode: '12345',
          country: { isocode: 'US' }
        }
      };
      const mockUserPaymentMethods: ApmPaymentDetailsListResponse = {
        payments: [mockPaymentWithAllProperties]
      };

      occUserPaymentAdapter.loadAllForCart(username, cartId).subscribe((result) => {
        expect(result[0]).toEqual(mockPaymentWithAllProperties);
        expect(result[0].id).toEqual('123');
      });

      const mockReq = httpMock.expectOne((req) => req.method === 'GET');
      mockReq.flush(mockUserPaymentMethods);
    });
  });
});
