import { OccWorldpayAdapter } from './occ-worldpay.adapter';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Address, ConverterService, OccConfig, OccEndpointsService, Order, PaymentDetails } from '@spartacus/core';
import { TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { of } from 'rxjs';
import { PAYMENT_DETAILS_SERIALIZER } from '@spartacus/checkout/core';
import { ApmPaymentDetails, PaymentMethod } from '../interfaces';

const userId = 'userId';
const cartId = 'cartId';
const securityCode = '123';
const cseToken = 'mockCseToken';
const paymentDetails: PaymentDetails = {
  id: 'aaa123',
  cardNumber: '1234123412341234'
};

const orderData: Order = {
  site: 'electronics-spa',
  calculated: true,
  code: '00001004'
};

const address: Address = {
  line1: 'The House',
  line2: 'The Road',
  postalCode: 'AA1 2BB'
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

const jwt =
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c';

class MockOccWorldpayAdapter {
  create() {
    return of(paymentDetails);
  }
}

class MockOccEndpointsService {
  buildUrl(url) {
    return url;
  }
}

describe('OccWorldpayAdapter', () => {
  let service: OccWorldpayAdapter;
  let httpMock: HttpTestingController;
  let converter: ConverterService;
  let occEndpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
      providers: [
        OccWorldpayAdapter,
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

    service = TestBed.inject(OccWorldpayAdapter);
    httpMock = TestBed.inject(HttpTestingController);
    converter = TestBed.inject(ConverterService);
    occEndpointsService = TestBed.inject(OccEndpointsService);

    spyOn(converter, 'pipeable').and.callThrough();
    spyOn(converter, 'convert').and.callThrough();
    spyOn(occEndpointsService, 'buildUrl').and.callThrough();
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('create', () => {
    beforeEach(() => {
      service.create(userId, cartId, paymentDetails, cseToken).subscribe();
      expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
        'createWorldpayPaymentDetails',
        {
          urlParams: {
            userId,
            cartId
          }
        }
      );
    });

    it('should be able to create payment details', () => {
      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'createWorldpayPaymentDetails' &&
        req.body.id === 'aaa123' &&
        req.body.cseToken === 'mockCseToken'
      );

      expect(mockReq.cancelled).toBeFalsy();
      expect(mockReq.request.responseType).toEqual('json');
      mockReq.flush(paymentDetails);
    });

    it('should use converter', () => {
      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'createWorldpayPaymentDetails' &&
        req.body.id === 'aaa123' &&
        req.body.cseToken === 'mockCseToken'
      );

      expect(mockReq.cancelled).toBeFalsy();
      expect(mockReq.request.responseType).toEqual('json');
      mockReq.flush({});

      const pciPaymentDetails = {
        ...paymentDetails,
        cseToken
      };
      delete pciPaymentDetails.cardNumber;

      expect(converter.convert).toHaveBeenCalledWith(
        pciPaymentDetails,
        PAYMENT_DETAILS_SERIALIZER
      );
    });
  });

  describe('useExistingPaymentDetails', () => {
    it('should be able to use existing payment details', () => {
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

  describe('getPublicKey', () => {
    it('should GET the API', () => {
      service.getPublicKey().subscribe();

      expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
        'getPublicKey',
      );

      const mockReq = httpMock.expectOne(req =>
        req.method === 'GET' &&
        req.urlWithParams === 'getPublicKey'
      );

      expect(mockReq.cancelled).toBeFalsy();
      mockReq.flush({});
    });
  });

  describe('setPaymentAddress', () => {
    it('should set the payment address', () => {
      service.setPaymentAddress(userId, cartId, address).subscribe(result => {
        expect(result).toEqual(address);
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'setPaymentAddress' &&
        req.body.line1 === address.line1 &&
        req.body.line2 === address.line2 &&
        req.body.postalCode === address.postalCode &&
        req.body.visibleInAddressBook === false
      );

      expect(mockReq.cancelled).toBeFalsy();
      expect(mockReq.request.responseType).toEqual('json');
      mockReq.flush(address);
    });
  });

  describe('get3dsJwt', () => {
    it('should GET the API', () => {
      service.getDDC3dsJwt().subscribe();

      expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
        'getDDC3dsJwt'
      );

      const mockReq = httpMock.expectOne(req =>
        req.method === 'GET' &&
        req.urlWithParams === 'getDDC3dsJwt'
      );

      expect(mockReq.cancelled).toBeFalsy();

      mockReq.flush({});
    });
  });

  describe('initialPaymentRequest', () => {
    it('should POST the API', () => {
      const challengeWindowSize = '320x400';
      const dfReferenceId = 'ref-id';
      const acceptedTermsAndConditions = true;

      service.initialPaymentRequest(
        userId,
        cartId,
        paymentDetails,
        dfReferenceId,
        challengeWindowSize,
        cseToken,
        acceptedTermsAndConditions,
        null
      )
        .subscribe();

      expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
        'initialPaymentRequest',
        {
          urlParams: {
            userId,
            cartId,
          }
        },
      );

      const mockReq = httpMock.expectOne(req =>
        req.method === 'POST' &&
        req.urlWithParams === 'initialPaymentRequest' &&
        req.body.id === paymentDetails.id &&
        req.body.cardNumber === paymentDetails.cardNumber &&
        req.body.cardNumber === paymentDetails.cardNumber &&
        req.body.acceptedTermsAndConditions === true &&
        req.body.cseToken === cseToken
      );

      expect(mockReq.cancelled).toBeFalsy();
      mockReq.flush({
        ...paymentDetails,
        challengeWindowSize,
        dfReferenceId,
        cseToken,
        acceptedTermsAndConditions
      });
    });

    it('should POST the API with FraudSight', () => {
      const challengeWindowSize = '320x400';
      const dfReferenceId = 'ref-id';
      const acceptedTermsAndConditions = true;
      const deviceSession = 'lolllllll';

      const fsPaymentDetails = {
        ...paymentDetails,
        dateOfBirth: '1970-01-01'
      };
      service.initialPaymentRequest(
        userId,
        cartId,
        fsPaymentDetails,
        dfReferenceId,
        challengeWindowSize,
        cseToken,
        acceptedTermsAndConditions,
        deviceSession
      )
        .subscribe();

      expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
        'initialPaymentRequest',
        {
          urlParams: {
            userId,
            cartId
          }
        }
      );

      const mockReq = httpMock.expectOne(
        req =>
          req.method === 'POST' &&
          req.urlWithParams === 'initialPaymentRequest' &&
          req.body.id === paymentDetails.id &&
          req.body.cardNumber === paymentDetails.cardNumber &&
          req.body.cardNumber === paymentDetails.cardNumber &&
          req.body.acceptedTermsAndConditions === true &&
          req.body.cseToken === cseToken
      );

      expect(mockReq.cancelled).toBeFalsy();
      mockReq.flush({
        ...fsPaymentDetails,
        challengeWindowSize,
        dfReferenceId,
        cseToken,
        acceptedTermsAndConditions,
        deviceSession,
      });
    });
  });

  describe('ApplePay', () => {

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
  });

  describe('APM', () => {
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

    it('should set APM payment info', () => {
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

    it('should get available apms', () => {
      service.getAvailableApms(userId, cartId).subscribe();

      const mockReq = httpMock.expectOne(
        req =>
          req.method === 'GET' &&
          req.urlWithParams === 'getAvailableApms'
      );

      expect(mockReq.cancelled).toBeFalsy();

      mockReq.flush({});
    });
  });
});
