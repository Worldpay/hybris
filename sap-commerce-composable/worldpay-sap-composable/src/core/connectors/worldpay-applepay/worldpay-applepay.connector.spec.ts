import { TestBed } from '@angular/core/testing';
import { WorldpayApplepayAdapter } from './worldpay-applepay.adapter';
import { takeUntil } from 'rxjs/operators';
import { WorldpayApplepayConnector } from './worldpay-applepay.connector';
import { ApmData, PaymentMethod } from '../../interfaces';
import { of, Subject } from 'rxjs';
import { PaymentDetails } from '@spartacus/cart/base/root';
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

const mockPayment: PaymentDetails = {
  accountHolderName: 'John Smith',
  cardNumber: '************6206',
  expiryMonth: '12',
  expiryYear: '2026',
  cardType: {
    name: 'Visa',
  },
};

class MockWorldpayApplepayAdapter implements WorldpayApplepayAdapter {
  requestApplePayPaymentRequest = createSpy('WorldpayAdapter.authoriseApmRedirect').and.callFake(() => of(null));

  validateApplePayMerchant = createSpy('WorldpayAdapter.getAvailableApms').and.callFake((userId: string, cartId: string) => of(apms));

  authorizeApplePayPayment = createSpy('WorldpayAdapter.setAPMPaymentInfo').and.callFake((userId, cartId, ApmPaymentInfo) => of(ApmPaymentInfo));
}

describe('WorldpayApplepayConnector', () => {
  let service: WorldpayApplepayConnector;
  let adapter: WorldpayApplepayAdapter;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: WorldpayApplepayAdapter,
          useClass: MockWorldpayApplepayAdapter
        }
      ]
    });
    service = TestBed.inject(WorldpayApplepayConnector);
    adapter = TestBed.inject(WorldpayApplepayAdapter);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

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
