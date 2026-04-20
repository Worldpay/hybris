import { Observable, of } from 'rxjs';
import { ApmPaymentDetails, APMRedirectResponse, PaymentMethod, WorldpayApmService } from '../../core';

export class MockWorldpayApmService implements Partial<WorldpayApmService> {
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

  selectAPM(): void {

  }

  checkoutPreconditions(): Observable<[string, string]> {
    return of(['userId', 'cartId']);
  }

  getSelectedAPMFromState(): Observable<ApmPaymentDetails> {
    return of({
      code: PaymentMethod.Card,
      name: 'Visa',
    });
  }

  showErrorMessage(): void {

  }

  getSaveApm(): Observable<boolean> {
    return of(true);
  }

  setSaveApm(value: boolean): void {
  }
}