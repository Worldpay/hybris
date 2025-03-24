import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { OccConfig, OccEndpointsService } from '@spartacus/core';
import { ACHPaymentForm } from '../../../interfaces';
import { OccWorldpayACHAdapter } from './occ-worldpay-ach.adapter';

const userId = 'userId';
const cartId = 'cartId';
const achPaymentForm: ACHPaymentForm = {
  accountType: 'Checking',
  accountNumber: '1234567890',
  routingNumber: '987654321',
  companyName: 'Company Name',
  customIdentifier: 'Identifier',
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

describe('OccWorldpayACHAdapter', () => {
  let service: OccWorldpayACHAdapter;
  let httpMock: HttpTestingController;
  let occEndpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
      providers: [
        OccWorldpayACHAdapter,
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

    service = TestBed.inject(OccWorldpayACHAdapter);
    httpMock = TestBed.inject(HttpTestingController);
    occEndpointsService = TestBed.inject(OccEndpointsService);
    spyOn(occEndpointsService, 'buildUrl').and.callThrough();
  });

  it('should POST placeACHOrder ', () => {
    service.placeACHOrder(userId, cartId, achPaymentForm).subscribe();

    const mockReq = httpMock.expectOne(req =>
      req.method === 'POST' &&
      req.urlWithParams === 'placeACHOrder' &&
      req.body === achPaymentForm
    );

    expect(mockReq.cancelled).toBeFalsy();

    mockReq.flush(achPaymentForm);
  });

  it('should get available apms using getAvailableApms method', () => {
    service.getACHBankAccountTypes(userId, cartId).subscribe();

    const mockReq = httpMock.expectOne(
      req =>
        req.method === 'GET' &&
        req.urlWithParams === 'getACHBankAccountTypes'
    );

    expect(mockReq.cancelled).toBeFalsy();

    mockReq.flush({});
  });

  it('should handle error when placing an ACH order', () => {
    const error = new Error('Http failure response for placeACHOrder: 500 Server Error');

    service.placeACHOrder(userId, cartId, achPaymentForm).subscribe({
      error: (err) => {
        expect(err.status).toBe(500);
        expect(err.message).toEqual(error.message);
      }
    });

    const mockReq = httpMock.expectOne(req =>
      req.method === 'POST' &&
      req.urlWithParams === 'placeACHOrder' &&
      req.body === achPaymentForm
    );

    mockReq.flush(error, {
      status: 500,
      statusText: 'Server Error'
    });
  });
});
