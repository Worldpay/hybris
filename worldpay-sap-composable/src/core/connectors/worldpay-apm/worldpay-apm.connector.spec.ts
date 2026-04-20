import { TestBed } from '@angular/core/testing';
import { Params } from '@angular/router';
import { PaymentDetails } from '@spartacus/core';
import { of, Subject } from 'rxjs';
import { take, takeUntil } from 'rxjs/operators';
import { generateOrder } from 'worldpay-sap-composable-tests';
import { generateOneCart } from '../../../tests/fake-data/cart.mock';
import { ApmData, ApmPaymentDetails, PaymentMethod } from '../../interfaces';
import { WorldpayApmAdapter } from './worldpay-apm.adapter';
import { WorldpayApmConnector } from './worldpay-apm.connector';
import createSpy = jasmine.createSpy;

const mockParams: Params = {
  pending: 'true',
  paymentStatus: 'AUTHORISED',
  orderKey: 'E2Y^MERCHANT2ECOM^00000018-1761730288154',
};

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

const mockPayment: PaymentDetails = {
  accountHolderName: 'John Smith',
  cardNumber: '************6206',
  expiryMonth: '12',
  expiryYear: '2026',
  cardType: {
    name: 'Visa',
  },
};

class MockWorldpayApmAdapter implements WorldpayApmAdapter {
  authoriseApmRedirect = createSpy('WorldpayAdapter.authoriseApmRedirect').and.callFake(() => of(null));

  getAvailableApms = createSpy('WorldpayAdapter.getAvailableApms').and.callFake(() => of(apms));

  setAPMPaymentInfo = createSpy('WorldpayAdapter.setAPMPaymentInfo').and.callFake((userId, cartId, ApmPaymentInfo) => of(ApmPaymentInfo));

  placeBankTransferOrderRedirect = createSpy('WorldpayAdapter.placeBankTransferOrderRedirect').and.callFake(() => of(null));

  placeRedirectOrder = createSpy('WorldpayAdapter.placeRedirectOrder').and.callFake(() => of(null));

  useExistingPaymentDetails = createSpy('WorldpayAdapter.setPaymentAddress').and.callFake((userId, cartId, paymentDetails) =>
    of(
      `useExistingPaymentDetails-${userId}-${cartId}-${paymentDetails.cardNumber}-${paymentDetails.cvn}`
    )
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

    adapter.placeRedirectOrder = createSpy('WorldpayAdapter.placeRedirectOrder').and.callFake(() => of(order));
    service.placeOrderRedirect('userId', 'cartId', mockParams)
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
    adapter.placeBankTransferOrderRedirect = createSpy('WorldpayAdapter.placeBankTransferOrderRedirect').and.callFake(() => of(order));

    service.placeBankTransferOrderRedirect('userId', 'cartId', order.code)
      .pipe(take(1))
      .subscribe(res => {
        result = res;
        doneFn();
      })
      .unsubscribe();

    expect(result).toEqual(order);
  });

  it('should call the setAPMPaymentInfo adapter', (doneFn) => {
    let result;
    adapter.setAPMPaymentInfo = createSpy('WorldpayAdapter.setAPMPaymentInfo').and.callFake(() => of(cart));
    service.setAPMPaymentInfo('userId', 'cartId', mockPayment)
      .pipe(take(1))
      .subscribe(res => {
        result = res;
        doneFn();
      })
      .unsubscribe();
    expect(result).toEqual(cart);
  });
});
