import { WorldpayAdapter } from './worldpay.adapter';
import { WorldpayConnector } from './worldpay.connector';
import { TestBed } from '@angular/core/testing';
import { take } from 'rxjs/operators';
import { of, Subject } from 'rxjs';
import { BrowserInfo } from '../interfaces';
import createSpy = jasmine.createSpy;

const drop = new Subject();
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

class MockWorldpayAdapter implements WorldpayAdapter {
  useExistingPaymentDetails = createSpy(
    'WorldpayAdapter.setPaymentAddress'
  ).and.callFake((userId, cartId, paymentDetails) =>
    of(
      `useExistingPaymentDetails-${userId}-${cartId}-${paymentDetails.cardNumber}-${paymentDetails.cvn}`
    )
  );
  getPublicKey = createSpy('WorldpayAdapter.getPublicKey').and.callFake(() =>
    of('publickey')
  );
  setPaymentAddress = createSpy(
    'WorldpayAdapter.setPaymentAddress'
  ).and.callFake((userId, cartId, address) =>
    of(
      `setPaymentAddress-${userId}-${cartId}-${address.line1}-${address.postalCode}`
    )
  );
  getDDC3dsJwt = createSpy('WorldpayAdapter.getDDC3dsJwt').and.callFake(() =>
    of({
      jwt: 'jwt',
      ddcUrl: 'https://ddc.aws.e2y.io'
    })
  );
  initialPaymentRequest = createSpy(
    'WorldpayAdapter.initialPaymentRequest'
  ).and.callFake(() => of({}));
  getOrder = createSpy('WorldpayAdapter.getOrder').and.callFake(
    (userId: string, code: string) =>
      of({
        code
      })
  );

  authorizeApplePayPayment = createSpy(
    'WorldpayAdapter.authorizeApplePayPayment'
  ).and.callFake((userId: string, cartId: string, request: any) =>
    of(`authorize-${userId}-cartId-${cartId}`)
  );
  requestApplePayPaymentRequest = createSpy(
    'WorldpayAdapter.requestApplePayPaymentRequest'
  ).and.callFake((userId: string, cartId: string) =>
    of(`requestApplePayPaymentRequest-${userId}-cartId-${cartId}`)
  );
  validateApplePayMerchant = createSpy(
    'WorldpayAdapter.requestApplePayPaymentRequest'
  ).and.callFake((userId: string, cartId: string, validationURL: string) =>
    of(
      `requestApplePayPaymentRequest-${userId}-cartId-${cartId}-url-${validationURL}`
    )
  );
  getGooglePayMerchantConfiguration = createSpy(
    'WorldpayAdapter.getGooglePayMerchantConfiguration'
  ).and.callFake((userId: string, cartId: string) =>
    of(of('googlePayMerchantConfiguration'))
  );
  authoriseGooglePayPayment = createSpy(
    'WorldpayAdapter.authoriseGooglePayPayment'
  ).and.callFake(() => of('authoriseGooglePay'));

  isGuaranteedPaymentsEnabled = createSpy('WorldpayAdapter.isFraudSightEnabled').and.callFake(() => of(false));
}

describe('WorldpayConnector', () => {
  let service: WorldpayConnector;
  let adapter: WorldpayAdapter;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{
        provide: WorldpayAdapter,
        useClass: MockWorldpayAdapter
      }]
    });

    service = TestBed.inject(WorldpayConnector);
    adapter = TestBed.inject(WorldpayAdapter);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call the getPublicKey adapter', () => {
    let result;

    service
      .getPublicKey()
      .pipe(take(1))
      .subscribe(res => (result = res))
      .unsubscribe();

    expect(result).toBe('publickey');
  });

  it('should call the setPaymentAddress adapter', () => {
    let result;

    service
      .setPaymentAddress('userId', 'cartId', {
        line1: '123 Test Street',
        postalCode: 'AA1 2BB'
      })
      .pipe(take(1))
      .subscribe(res => (result = res))
      .unsubscribe();

    expect(result).toBe(
      'setPaymentAddress-userId-cartId-123 Test Street-AA1 2BB'
    );
  });

  it('should call the getDDC3dsJwt adapter', () => {
    let result;

    service
      .getDDC3dsJwt()
      .pipe(take(1))
      .subscribe(res => (result = res))
      .unsubscribe();

    expect(result).toEqual({
      jwt: 'jwt',
      ddcUrl: 'https://ddc.aws.e2y.io'
    });
  });

  it('should call the getOrder adapter', () => {
    let result;

    service
      .getOrder('userId', 'orderCode', false)
      .pipe(take(1))
      .subscribe(res => (result = res))
      .unsubscribe();

    expect(result).toEqual({
      code: 'orderCode'
    });
  });

  it('should call initialPaymentRequest', (done) => {
    service.initialPaymentRequest(
        'userId',
        'cartId',
        {
          cardNumber: '1234123412341234',
          cvn: '123'
        },
        'ref',
        '500x500',
        'cse-token',
        true,
        null,
        browserInfo
      )
      .subscribe(() => {
        expect(adapter.initialPaymentRequest).toHaveBeenCalledWith(
          'userId',
          'cartId',
          {
            cardNumber: '1234123412341234',
            cvn: '123'
          },
          'ref',
          '500x500',
          'cse-token',
          true,
          null,
          browserInfo
        );

        done();
      });
  });

  describe('FraudSight', () => {

    it('should call initialPaymentRequest with FraudSight ID and dateOfBirth', (done) => {
      const deviceSession = 'lalalaejogere';
      service.initialPaymentRequest(
          'userId',
          'cartId', {
            cardNumber: '1234123412341234',
            cvn: '123',
            dateOfBirth: '2020-01-01',
          },
          'ref',
          '500x500',
          'cse-token',
          true,
          deviceSession,
          browserInfo
        )
        .subscribe(() => {
          expect(adapter.initialPaymentRequest).toHaveBeenCalledWith(
            'userId',
            'cartId',
            {
              cardNumber: '1234123412341234',
              cvn: '123',
              dateOfBirth: '2020-01-01',
            },
            'ref',
            '500x500',
            'cse-token',
            true,
            deviceSession,
            browserInfo
          );

          done();
        });
    });
  });
});

