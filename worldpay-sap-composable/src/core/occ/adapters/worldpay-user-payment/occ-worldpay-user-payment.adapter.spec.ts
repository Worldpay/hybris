import { provideHttpClient, withInterceptorsFromDi, } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting, } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ConverterService, Occ, OccConfig, OccEndpointsService, PAYMENT_DETAILS_NORMALIZER, PaymentDetails } from '@spartacus/core';
import { ApmPaymentDetailsListResponse } from '../../../interfaces';
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
});
