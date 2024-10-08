import { TestBed } from '@angular/core/testing';
import { takeUntil } from 'rxjs/operators';
import { of, Subject } from 'rxjs';
import { PaymentDetails } from '@spartacus/core';
import { ApmData, PaymentMethod } from '../../interfaces';
import { WorldpayGooglepayAdapter } from './worldpay-googlepay.adapter';
import { WorldpayGooglePayConnector } from './worldpay-googlepay.connector';
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

class MockWorldpayGooglepayAdapter implements WorldpayGooglepayAdapter {
  getGooglePayMerchantConfiguration = createSpy('WorldpayAdapter.authoriseApmRedirect').and.callFake(() => of(null));

  authoriseGooglePayPayment = createSpy('WorldpayAdapter.getAvailableApms').and.callFake((userId: string, cartId: string) => of(apms));
}

describe('WorldpayGooglepayConnector', () => {
  let service: WorldpayGooglePayConnector;
  let adapter: WorldpayGooglepayAdapter;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: WorldpayGooglepayAdapter,
          useClass: MockWorldpayGooglepayAdapter
        }
      ]
    });
    service = TestBed.inject(WorldpayGooglePayConnector);
    adapter = TestBed.inject(WorldpayGooglepayAdapter);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call getGooglePayMerchantConfiguration', () => {
    service.getGooglePayMerchantConfiguration('userId', 'cartId')
      .pipe(takeUntil(drop)).subscribe(response => response);

    expect(adapter.getGooglePayMerchantConfiguration).toHaveBeenCalledWith('userId', 'cartId');
  });

  it('should call authoriseGooglePayPayment', () => {
    service.authoriseGooglePayPayment('userId', 'cartId', 'token', { id: 'yesplease' }, true)
      .pipe(takeUntil(drop)).subscribe(response => response);

    expect(adapter.authoriseGooglePayPayment).toHaveBeenCalledWith('userId', 'cartId', 'token', { id: 'yesplease' }, true);
  });

});
