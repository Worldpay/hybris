import { Observable, of } from 'rxjs';
import { WorldpayACHFacade } from 'worldpay-sap-composable-facade';
import { ACHPaymentForm } from '../../core/interfaces';

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