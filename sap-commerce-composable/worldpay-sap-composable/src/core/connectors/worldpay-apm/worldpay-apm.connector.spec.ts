import { HttpHeaders } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { generateOneAddress } from '@worldpay-tests/fake-data/address.mock';
import { generateOneApmPaymentDetail } from '@worldpay-tests/fake-data/apm-payment.mock';
import { generateOneCart } from '@worldpay-tests/fake-data/cart.mock';
import { generateOrder } from '@worldpay-tests/fake-data/order.mock';
import { of, Subject } from 'rxjs';
import { take, takeUntil } from 'rxjs/operators';
import { ApmData, ApmPaymentDetails, PaymentMethod } from '../../interfaces';
import { WorldpayApmAdapter } from './worldpay-apm.adapter';
import { WorldpayApmConnector } from './worldpay-apm.connector';
import createSpy = jasmine.createSpy;

const userId = 'mockUserId';
const cartId = 'mockCartId';
const address = generateOneAddress();
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
const order = generateOrder();
const cart = generateOneCart();

const mockApmPaymentDetails: ApmPaymentDetails = generateOneApmPaymentDetail();

const errorResponse = {
  error: {
    errors: [{
      type: 'test',
      message: 'Test error message'
    }]
  },
  headers: new HttpHeaders().set('xxx', 'xxx'),
  status: 500,
  statusText: 'Request Error',
};

class MockWorldpayApmAdapter implements WorldpayApmAdapter {
  authoriseApmRedirect = createSpy('WorldpayAdapter.authoriseApmRedirect').and.callFake(() => of(null));

  getAvailableApms = createSpy('WorldpayAdapter.getAvailableApms').and.callFake((userId: string, cartId: string) => of(apms));

  setAPMPaymentInfo = createSpy('WorldpayAdapter.setAPMPaymentInfo').and.callFake((userId, cartId, ApmPaymentInfo) => of(cart));

  placeBankTransferOrderRedirect = createSpy('WorldpayAdapter.placeBankTransferOrderRedirect').and.callFake((userId: string, cartId: string) => of(order));

  placeRedirectOrder = createSpy('WorldpayAdapter.placeRedirectOrder').and.callFake((userId: string, cartId: string) => of(order));

  useExistingPaymentDetails = createSpy('WorldpayAdapter.setPaymentAddress').and.callFake((userId, cartId, paymentDetails) =>
    of(`useExistingPaymentDetails-${userId}-${cartId}-${paymentDetails.cardNumber}-${paymentDetails.cvn}`)
  );
}

describe('WorldpayApmConnector', () => {
  let service: WorldpayApmConnector;
  let adapter: WorldpayApmAdapter;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: WorldpayApmAdapter,
          useClass: MockWorldpayApmAdapter
        }
      ]
    });
    service = TestBed.inject(WorldpayApmConnector);
    adapter = TestBed.inject(WorldpayApmAdapter);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
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

  it('should call the useExistingPaymentDetails adapter', (doneFn) => {
    let result;

    service.useExistingPaymentDetails('userId', 'cartId', {
        cardNumber: '1234123412341234',
        cvn: '123'
      })
      .pipe(take(1))
      .subscribe(res => {
        (result = res);
        doneFn();
      })
      .unsubscribe();

    expect(result).toBe(
      'useExistingPaymentDetails-userId-cartId-1234123412341234-123'
    );
  });

  it('should call the placeRedirectOrder adapter', (doneFn) => {
    let result;

    service.placeOrderRedirect('userId', 'cartId')
      .pipe(take(1))
      .subscribe(res => {
        result = res;
        doneFn();
      })
      .unsubscribe();

    expect(result).toBe(order);
  });

  it('should call the placeBankTransferOrderRedirect adapter', (doneFn) => {
    let result;

    service.placeBankTransferOrderRedirect('userId', 'cartId', order.code)
      .pipe(take(1))
      .subscribe(res => {
        result = res;
        doneFn();
      })
      .unsubscribe();

    expect(result).toBe(order);
  });

  it('should call the setAPMPaymentInfo adapter', (doneFn) => {
    let result;

    service.setAPMPaymentInfo('userId', 'cartId', mockApmPaymentDetails)
      .pipe(take(1))
      .subscribe(res => {
        result = res;
        doneFn();
      })
      .unsubscribe();
    expect(result).toEqual(cart);
  });
});
