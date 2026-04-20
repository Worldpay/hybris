import { CheckoutPaymentFacade } from '@spartacus/checkout/base/root';
import { Address, PaymentDetails, QueryState } from '@spartacus/core';
import { EMPTY, Observable, of } from 'rxjs';
import { PaymentMethod } from '../../core';
import createSpy = jasmine.createSpy;

const mockCheckoutPaymentPaymentDetails: PaymentDetails = {
  id: 'mock payment id',
  accountHolderName: 'Name',
  cardNumber: '123456789',
  cardType: {
    code: 'Visa',
    name: 'Visa',
  },
  expiryMonth: '01',
  expiryYear: '2022',
  cvn: '123',
  code: PaymentMethod.Card,
  save: true
};
const mockCheckoutPaymentAddress: Address = {
  id: 'mock address id',
  firstName: 'John',
  lastName: 'Doe',
  titleCode: 'mr',
  line1: 'Toyosaki 2 create on cart',
  line2: 'line2',
  town: 'town',
  region: { isocode: 'JP-27' },
  postalCode: 'zip',
  country: { isocode: 'JP' },
};

export class MockCheckoutPaymentFacade implements Partial<CheckoutPaymentFacade> {
  loadSupportedCardTypes = createSpy();
  getPaymentCardTypes = createSpy().and.returnValue(EMPTY);
  getSetPaymentDetailsResultProcess = createSpy().and.returnValue(
    of({ loading: false })
  );
  setPaymentDetails(): Observable<unknown> {
    return of({});
  }

  createPaymentDetails(_paymentDetails: PaymentDetails): Observable<unknown> {
    return of({
      ...mockCheckoutPaymentPaymentDetails,
      ...mockCheckoutPaymentAddress
    });
  }

  getPaymentDetails(): Observable<PaymentDetails> {
    return of(mockCheckoutPaymentPaymentDetails);
  }

  paymentProcessSuccess() {
  }

  getPaymentDetailsState(): Observable<QueryState<PaymentDetails | undefined>> {
    return EMPTY;
  }

  setPaymentAddress(_address: Address): Observable<unknown> {
    return EMPTY;
  }

  getPublicKey(): Observable<QueryState<string>> {
    return EMPTY;
  }

  getPublicKeyFromState(): Observable<string> {
    return EMPTY;
  }

  useExistingPaymentDetails(_paymentDetails: PaymentDetails): Observable<unknown> {
    return EMPTY;
  }

  setSaveCreditCardValue(): void {

  }

  setSaveAsDefaultCardValue(): void {

  }

  generateCseToken(_paymentDetails: PaymentDetails): Observable<string> {
    return of('mock token');
  }

  generatePublicKey(): Observable<string> {
    return of('public key');
  }

  setCseToken(_token: string): void {

  }
}

export class MockCheckoutPaymentService extends MockCheckoutPaymentFacade {
  constructor() {
    super();
  }
}