import { TestBed } from '@angular/core/testing';
import { WorldpayApmAdapter } from './worldpay-apm.adapter';
import { take, takeUntil } from 'rxjs/operators';
import { WorldpayApmConnector } from './worldpay-apm.connector';
import { ApmData, ApmPaymentDetails, PaymentMethod } from '../../interfaces';
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

class MockWorldpayApmAdapter implements WorldpayApmAdapter {
  authoriseApmRedirect = createSpy('WorldpayAdapter.authoriseApmRedirect').and.callFake(() => of(null));

  getAvailableApms = createSpy('WorldpayAdapter.getAvailableApms').and.callFake((userId: string, cartId: string) => of(apms));

  setAPMPaymentInfo = createSpy('WorldpayAdapter.setAPMPaymentInfo').and.callFake((userId, cartId, ApmPaymentInfo) => of(ApmPaymentInfo));

  placeBankTransferOrderRedirect = createSpy('WorldpayAdapter.placeBankTransferOrderRedirect').and.callFake((userId: string, cartId: string) => of(null));

  placeRedirectOrder = createSpy('WorldpayAdapter.placeRedirectOrder').and.callFake((userId: string, cartId: string) => of(null));

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

  it('should call the useExistingPaymentDetails adapter', () => {
    let result;

    service.useExistingPaymentDetails('userId', 'cartId', {
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

});
