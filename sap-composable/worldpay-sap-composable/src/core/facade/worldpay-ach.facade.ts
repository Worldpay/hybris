import { Injectable } from '@angular/core';
import { facadeFactory, QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import { WORLDPAY_ACH_FEATURE } from './worldpay-feature-name';
import { ACHBankAccountType, ACHPaymentForm, ACHPaymentFormRaw } from '../interfaces';

@Injectable({
  providedIn: 'root',
  useFactory: () =>
    facadeFactory({
      facade: WorldpayACHFacade,
      feature: WORLDPAY_ACH_FEATURE,
      methods: [
        'getACHBankAccountTypesState',
        'getACHPaymentFormValue',
        'setACHPaymentFormValue',
        'clearWorldpayACHPaymentFormEvent'
      ],
    }),
})
export abstract class WorldpayACHFacade {

  /**
   * Get ACH Bank Account Types
   * @since 6.4.2
   * @returns - ACHBankAccountType as Observable
   */
  abstract getACHBankAccountTypesState(): Observable<QueryState<ACHBankAccountType[]>>;

  /**
   * Get ACH Form Values
   * @since 6.4.2
   * @returns - ACHBankAccountType as Observable
   */
  abstract getACHPaymentFormValue(): Observable<ACHPaymentForm>;

  /**
   * Set ACH Form Values
   * @since 6.4.2
   * @param achPaymentFormValue - ACH Payment Form
   * @returns - void
   */
  abstract setACHPaymentFormValue(achPaymentFormValue: ACHPaymentFormRaw): void;

  abstract clearWorldpayACHPaymentFormEvent(): void;
}
