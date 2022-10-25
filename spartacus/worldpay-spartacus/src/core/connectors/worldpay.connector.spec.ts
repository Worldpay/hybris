import { WorldpayAdapter } from './worldpay.adapter';
import { WorldpayConnector } from './worldpay.connector';
import { TestBed } from '@angular/core/testing';
import { take, takeUntil } from 'rxjs/operators';
import { of, Subject } from 'rxjs';
import { ApmData, ApmPaymentDetails, PaymentMethod } from '../interfaces';
import createSpy = jasmine.createSpy;

const drop = new Subject();
const apms: ApmData[] = [
  {
    code: PaymentMethod.ApplePay,
    name: 'ApplePay'
  },
  {
    code: PaymentMethod.GooglePay,
    name: 'GooglePay'
  },
  {
    code: PaymentMethod.Card,
    name: 'Credit'
  },
];

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

  authoriseApmRedirect = createSpy(
    'WorldpayAdapter.authoriseApmRedirect'
  ).and.callFake(() => of(null));

  getAvailableApms = createSpy(
    'WorldpayAdapter.getAvailableApms'
  ).and.callFake((userId: string, cartId: string) => of(apms));

  placeRedirectOrder = createSpy(
    'WorldpayAdapter.placeRedirectOrder'
  ).and.callFake((userId: string, cartId: string) => of(null));

  isFraudSightEnabled = createSpy('WorldpayAdapter.isFraudSightEnabled').and.callFake(() => of(false));
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

  it('should call the useExistingPaymentDetails adapter', () => {
    let result;

    service
      .useExistingPaymentDetails('userId', 'cartId', {
        cardNumber: '1234123412341234',
        cvn: '123'
      })
      .pipe(take(1))
      .subscribe(res => (result = res))
      .unsubscribe();

    expect(result).toBe(
      'useExistingPaymentDetails-userId-cartId-1234123412341234-123'
    );
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
      .getOrder('userId', 'orderCode')
      .pipe(take(1))
      .subscribe(res => (result = res))
      .unsubscribe();

    expect(result).toEqual({
      code: 'orderCode'
    });
  });

  it('should call initialPaymentRequest', (done) => {
    service.initialPaymentRequest('userId', 'cartId', {
      cardNumber: '1234123412341234',
      cvn: '123'
    }, 'ref', '500x500', 'cse-token', true, null)
      .subscribe(() => {
        expect(adapter.initialPaymentRequest).toHaveBeenCalledWith(
          'userId', 'cartId', {
            cardNumber: '1234123412341234',
            cvn: '123'
          }, 'ref', '500x500', 'cse-token', true, null
        );

        done();
      });
  });

  describe('ApplePay', () => {
    it('should call requestApplePayPaymentRequest', (done) => {
      service.requestApplePayPaymentRequest('userId', 'cartId').subscribe(() => {
        expect(adapter.requestApplePayPaymentRequest).toHaveBeenCalledWith('userId', 'cartId');
        done();
      });

    });

    it('should call validateApplePayMerchant', () => {
      service.validateApplePayMerchant('userId', 'cartId', 'https://store.apple.com')
        .pipe(takeUntil(drop)).subscribe(response => response);
      expect(adapter.validateApplePayMerchant).toHaveBeenCalledWith('userId', 'cartId', 'https://store.apple.com');
    });

    it('should call authorizeApplePayPayment', () => {
      service.authorizeApplePayPayment('userId', 'cartId', { foo: 'bar' })
        .pipe(takeUntil(drop)).subscribe(response => response);

      expect(adapter.authorizeApplePayPayment).toHaveBeenCalledWith('userId', 'cartId', { foo: 'bar' });
    });
  });

  describe('GooglePay', () => {
    it('should call getGooglePayMerchantConfiguration', () => {
      service.getGooglePayMerchantConfiguration('userId', 'cartId')
        .pipe(takeUntil(drop)).subscribe(response => response);

      expect(adapter.getGooglePayMerchantConfiguration).toHaveBeenCalledWith('userId', 'cartId');
    });

    it('should call authoriseGooglePayPayment', () => {
      service.authoriseGooglePayPayment('userId', 'cartId', 'token', { billing: 'yesplease' }, true)
        .pipe(takeUntil(drop)).subscribe(response => response);

      expect(adapter.authoriseGooglePayPayment).toHaveBeenCalledWith('userId', 'cartId', 'token', { billing: 'yesplease' }, true);
    });
  });

  describe('Apm', () => {
    it('should call authoriseApmRedirect', () => {
      const apm: ApmPaymentDetails = {
        code: PaymentMethod.iDeal
      };

      service.authoriseApmRedirect('userId', 'cartId', apm, true)
        .pipe(takeUntil(drop)).subscribe(response => response);

      expect(adapter.authoriseApmRedirect).toHaveBeenCalledWith('userId', 'cartId', apm, true);
    });

    it('should call getAvailableApms', () => {
      service.getAvailableApms('userId', 'cartId')
        .pipe(takeUntil(drop)).subscribe(response => response);

      expect(adapter.getAvailableApms).toHaveBeenCalledWith('userId', 'cartId');
    });
  });

  describe('FraudSight', () => {
    it('should call isFraudSightEnabled', (done) => {
      service.isFraudSightEnabled().subscribe((res) => {
        expect(res).toEqual(false);
        done();
      });
    });

    it('should call initialPaymentRequest with FraudSight ID and dateOfBirth', (done) => {
      const deviceSession = 'lalalaejogere';
      service.initialPaymentRequest('userId', 'cartId', {
        cardNumber: '1234123412341234',
        cvn: '123',
        dateOfBirth: '2020-01-01',
      }, 'ref', '500x500', 'cse-token', true, deviceSession)
        .subscribe(() => {
          expect(adapter.initialPaymentRequest).toHaveBeenCalledWith(
            'userId', 'cartId', {
              cardNumber: '1234123412341234',
              cvn: '123',
              dateOfBirth: '2020-01-01',
            }, 'ref', '500x500', 'cse-token', true, deviceSession
          );

          done();
        });
    });
  });
});

