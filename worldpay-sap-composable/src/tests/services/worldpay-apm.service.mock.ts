import { PaymentDetails } from '@spartacus/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { ApmPaymentDetails, APMRedirectResponse, PaymentMethod, WorldpayApmService } from '../../core';

export const worlpayApmServicePaymentDetails: PaymentDetails = {
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

export class MockWorldpayApmService implements Partial<WorldpayApmService> {
  selectedApm$: BehaviorSubject<ApmPaymentDetails> = new BehaviorSubject<ApmPaymentDetails>(undefined);

  getWorldpayAPMRedirectUrlFromState(): Observable<APMRedirectResponse> {
    return of({
      postUrl: 'https://postURL.com',
      parameters: {
        entry: []
      },
      mappingLabels: {}
    });
  }

  getLoading(): Observable<boolean> {
    return of(false);
  }

  getAPMRedirectUrl() {

  }

  selectAPM(value: PaymentDetails): void {
    this.selectedApm$.next(value);
  }

  checkoutPreconditions(): Observable<[string, string]> {
    return of(['userId', 'cartId']);
  }

  getSelectedAPMFromState(): Observable<ApmPaymentDetails> {
    return this.selectedApm$.asObservable();
  }

  showErrorMessage(): void {

  }

  getSaveApm(): Observable<boolean> {
    return of(true);
  }

  setSaveApm(value: boolean): void {
  }

  getWorldpayAvailableApms(): Observable<any> {
    return of([]);
  }

  getPublicKey(): Observable<string> {
    return of('');
  }

  setApmPaymentDetails(): Observable<ApmPaymentDetails> {
    return of(worlpayApmServicePaymentDetails);
  }
}