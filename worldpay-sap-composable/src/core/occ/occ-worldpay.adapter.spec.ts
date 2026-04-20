import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Address, ConverterService, OccConfig, OccEndpointsService, PaymentDetails } from '@spartacus/core';
import { DaysOfWeek, ScheduleReplenishmentForm } from '@spartacus/order/root';
import { mockUserId } from 'worldpay-sap-composable-tests';
import { MockOccEndpointsService } from '../../tests/services/occ-endpoint.service.mock';
import { BrowserInfo } from '../interfaces';
import { OccWorldpayAdapter } from './occ-worldpay.adapter';

const userId = mockUserId;
const cartId = 'cartId';
const cseToken = 'mockCseToken';
const paymentDetails: PaymentDetails = {
  id: 'aaa123',
  cardNumber: '1234123412341234'
};
const scheduleReplenishmentForm: ScheduleReplenishmentForm = {
  daysOfWeek: [DaysOfWeek.MONDAY],
  replenishmentStartDate: '2025-01-01',
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

const browserInfo: BrowserInfo = {
  javaEnabled: false,
  language: 'en-GB',
  colorDepth: 24,
  screenHeight: 1080,
  screenWidth: 1920,
  timeZone: (-60).toString(),
  userAgent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36',
  javascriptEnabled: true
};

describe('OccWorldpayAdapter', () => {
  let service: OccWorldpayAdapter;
  let httpMock: HttpTestingController;
  let converter: ConverterService;
  let occEndpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
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
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
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

  describe('getPublicKey', () => {
    it('should GET public key API', () => {
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
    it('should GET 3ds Jwt API', () => {
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
        null,
        browserInfo,
        scheduleReplenishmentForm
      ).subscribe();

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
        acceptedTermsAndConditions,
        browserInfo
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
        deviceSession,
        browserInfo,
        scheduleReplenishmentForm
      ).subscribe();

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
        browserInfo
      });
    });
  });

  describe('getOrder', () => {
    it('should getOrder for anonymous user', () => {
      service.getOrder(
        'test@email.com',
        '100',
        true,
      )
        .subscribe();

      expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
        'getOrderForGuest',
        {
          urlParams: {
            userId: 'test@email.com',
            code: '100',
          }
        },
      );

      const mockReq = httpMock.expectOne(req =>
        req.method === 'GET' &&
        req.urlWithParams === 'getOrderForGuest'
      );

      expect(mockReq.cancelled).toBeFalsy();
      mockReq.flush({
        userId: 'test@email.com',
        code: '100',
      });
    });

    it('should getOrder for registered user', () => {
      service.getOrder(
        userId,
        '100',
        undefined
      )
        .subscribe();

      expect(occEndpointsService.buildUrl).toHaveBeenCalledWith(
        'getOrder',
        {
          urlParams: {
            userId,
            code: '100',
          }
        },
      );

      const mockReq = httpMock.expectOne(req =>
        req.method === 'GET' &&
        req.urlWithParams === 'getOrder'
      );

      expect(mockReq.cancelled).toBeFalsy();
      mockReq.flush({
        userId,
        code: '100',
      });
    });

    it('should handle error when getOrder fails', () => {
      service.getOrder(userId, '100', undefined).subscribe({
        error: (error) => {
          expect(error).toBeTruthy();
        }
      });

      const mockReq = httpMock.expectOne(req =>
        req.method === 'GET' &&
        req.urlWithParams === 'getOrder'
      );

      expect(mockReq.cancelled).toBeFalsy();
      mockReq.flush('Error', {
        status: 500,
        statusText: 'Server Error'
      });
    });
  });
});
