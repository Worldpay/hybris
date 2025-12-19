import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { take } from 'rxjs/operators';
import { BrowserInfo } from '../interfaces';
import { WorldpayAdapter } from './worldpay.adapter';
import { WorldpayConnector } from './worldpay.connector';
import createSpy = jasmine.createSpy;

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
  useExistingPaymentDetails = createSpy('useExistingPaymentDetails');
  getPublicKey = createSpy('getPublicKey').and.callFake(() => of('publickey'));
  setPaymentAddress = createSpy('setPaymentAddress').and.callFake((userId, cartId, address) =>
    of(`setPaymentAddress-${userId}-${cartId}-${address.line1}-${address.postalCode}`)
  );
  getDDC3dsJwt = createSpy('getDDC3dsJwt').and.callFake(() =>
    of({
      jwt: 'jwt',
      ddcUrl: 'https://ddc.aws.e2y.io'
    })
  );
  initialPaymentRequest = createSpy('initialPaymentRequest').and.callFake(() => of({}));
  getOrder = createSpy('getOrder').and.callFake((userId: string, code: string) => of({ code }));
  setDeliveryAddressAsBillingAddress = createSpy('setDeliveryAddressAsBillingAddress').and.callFake(
    (userId, cartId, addressId) => of({
      id: addressId,
      line1: 'Delivery as Billing',
      postalCode: 'ZZ99ZZ'
    })
  );
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

  it('should call getPublicKey adapter', (done) => {
    service.getPublicKey().subscribe(result => {
      expect(result).toBe('publickey');
      expect(adapter.getPublicKey).toHaveBeenCalled();
      done();
    });
  });

  it('should call the setPaymentAddress adapter', (done) => {
    service
      .setPaymentAddress('userId', 'cartId', {
        line1: '123 Test Street',
        postalCode: 'AA1 2BB'
      })
      .pipe(take(1))
      .subscribe((result: any) => {
        expect(result).toBe('setPaymentAddress-userId-cartId-123 Test Street-AA1 2BB');
        expect(adapter.setPaymentAddress).toHaveBeenCalledWith(
          'userId',
          'cartId',
          {
            line1: '123 Test Street',
            postalCode: 'AA1 2BB'
          }
        );
        done();
      });
  });

  it('should call getDDC3dsJwt adapter', (done) => {
    service.getDDC3dsJwt().subscribe(result => {
      expect(result).toEqual({
        jwt: 'jwt',
        ddcUrl: 'https://ddc.aws.e2y.io'
      });
      expect(adapter.getDDC3dsJwt).toHaveBeenCalled();
      done();
    });
  });

  it('should call initialPaymentRequest adapter', (done) => {
    const paymentDetails = {
      cardNumber: '1234123412341234',
      cvn: '123'
    };
    service.initialPaymentRequest(
      'userId',
      'cartId',
      paymentDetails,
      'ref',
      '500x500',
      'cse-token',
      true,
      'device-session',
      {
        javaEnabled: false,
        language: 'en-GB',
        colorDepth: 24,
        screenHeight: 1080,
        screenWidth: 1920,
        timeZone: '-60',
        userAgent: 'UA',
        javascriptEnabled: true
      }
    ).subscribe(() => {
      expect(adapter.initialPaymentRequest).toHaveBeenCalledWith(
        'userId',
        'cartId',
        paymentDetails,
        'ref',
        '500x500',
        'cse-token',
        true,
        'device-session',
        jasmine.any(Object)
      );
      done();
    });
  });

  it('should call getOrder adapter', (done) => {
    service.getOrder('userId', 'orderCode', false).subscribe(result => {
      expect(result).toEqual({ code: 'orderCode' });
      expect(adapter.getOrder).toHaveBeenCalledWith('userId', 'orderCode', false);
      done();
    });
  });

  it('should call setDeliveryAddressAsBillingAddress adapter', (done) => {
    service.setDeliveryAddressAsBillingAddress('user1', 'cart1', 'addr123').subscribe(result => {
      expect(result).toEqual({
        id: 'addr123',
        line1: 'Delivery as Billing',
        postalCode: 'ZZ99ZZ'
      });
      expect(adapter.setDeliveryAddressAsBillingAddress).toHaveBeenCalledWith('user1', 'cart1', 'addr123');
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

