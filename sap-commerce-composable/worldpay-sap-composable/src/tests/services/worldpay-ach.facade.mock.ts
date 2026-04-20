import { Observable, of } from 'rxjs';
import { ACHPaymentForm, WorldpayACHFacade } from '../../core';

export class MockWorldpayACHFacade implements Partial<WorldpayACHFacade> {
  getACHPaymentFormValue(): Observable<ACHPaymentForm> {
    return of({
      accountType: 'checking',
      routingNumber: '123456789',
      accountNumber: '123456789',
      accountHolderName: 'user',
    });
  }
}