import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Address, ConverterService, OccConfig, OccEndpointsService, PaymentDetails } from '@spartacus/core';
import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { OccWorldpayCheckoutPaymentAdapter } from './occ-worldpay-checkout-payment.adapter';
import { PAYMENT_DETAILS_SERIALIZER } from '@spartacus/checkout/base/core';

const userId = 'userId';
const cartId = 'cartId';
const securityCode = '123';
const cseToken = 'mockCseToken';

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

class MockOccEndpointsService {
  buildUrl(url) {
    return url;
  }
}

describe('OccWorldpayCheckoutPaymentAdapter', () => {
  let service: OccWorldpayCheckoutPaymentAdapter;
  let httpMock: HttpTestingController;
  let converter: ConverterService;
  let occEndpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
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
});
