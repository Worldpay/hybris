import { TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
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

class MockWorldpayGooglepayAdapter implements WorldpayGooglepayAdapter {
  getGooglePayMerchantConfiguration = createSpy('WorldpayAdapter.authoriseApmRedirect').and.callFake(() => of(null));

  authoriseGooglePayPayment = createSpy('WorldpayAdapter.getAvailableApms').and.callFake(() => of(apms));
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
