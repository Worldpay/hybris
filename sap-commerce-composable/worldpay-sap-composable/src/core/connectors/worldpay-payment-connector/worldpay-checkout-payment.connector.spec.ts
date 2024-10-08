import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { take } from 'rxjs/operators';
import { Address, PaymentDetails } from '@spartacus/core';
import { WorldpayCheckoutPaymentAdapter } from './worldpay-checkout-payment.adapter';
import { WorldpayCheckoutPaymentConnector } from './worldpay-checkout-payment.connector';
import createSpy = jasmine.createSpy;

const mockBillingAddress: Address = {
  firstName: 'John',
  lastName: 'Smith',
  line1: 'Buckingham Street 5',
  line2: '1A',
  phone: '(+11) 111 111 111',
  postalCode: 'MA8902',
  town: 'London',
  country: {
    name: 'test-country-name',
    isocode: 'UK',
  },
  formattedAddress: 'test-formattedAddress',
};

const mockPayment: PaymentDetails = {
  accountHolderName: 'John Smith',
  cardNumber: '************6206',
  expiryMonth: '12',
  expiryYear: '2026',
  cardType: {
    name: 'Visa',
  },
  billingAddress: mockBillingAddress,
};

class MockWorldpayCheckoutPaymentAdapter implements Partial<WorldpayCheckoutPaymentAdapter> {
  createWorldpayPaymentDetails = createSpy().and.returnValue(
    of(mockPayment)
  );
}

describe('WorldpayCheckoutPaymentConnector', () => {
  let service: WorldpayCheckoutPaymentConnector;
  let adapter: WorldpayCheckoutPaymentAdapter;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: WorldpayCheckoutPaymentAdapter,
          useClass: MockWorldpayCheckoutPaymentAdapter
        }
      ]
    });
    service = TestBed.inject(WorldpayCheckoutPaymentConnector);
    adapter = TestBed.inject(WorldpayCheckoutPaymentAdapter);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('isGuaranteedPaymentsEnabled should call adapter', (done) => {
    service.createWorldpayPaymentDetails('userId', 'cartId', mockPayment, 'token').pipe(take(1)).subscribe((res) => {
      expect(res).toEqual(mockPayment);
      done();
    });
    expect(adapter.createWorldpayPaymentDetails).toHaveBeenCalledWith('userId', 'cartId', mockPayment, 'token');
  });
});
