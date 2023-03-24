import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  ClearWorldpayGuaranteedPayments,
  LOAD_WORLDPAY_GUARANTEED_PAYMENTS,
  LoadWorldpayGuaranteedPaymentsSuccess
} from '../actions/worldpay-guaranteed-payments-session-id.actions';
import { map, mergeMap } from 'rxjs/operators';
import { AuthActions } from '@spartacus/core';

@Injectable()
export class WorldpayGuaranteedPaymentsSessionIdEffects {

  loadWorldpayGuaranteedPayments$ = createEffect(
    () => this.actions$.pipe(
      ofType(LOAD_WORLDPAY_GUARANTEED_PAYMENTS),
      map((action: any) => action.payload),
      mergeMap((data) => [
        new LoadWorldpayGuaranteedPaymentsSuccess(data)
      ]),
    )
  );

  clearWorldpayGuaranteedPayments$ = createEffect(
    () => this.actions$.pipe(
      ofType(AuthActions.LOGOUT),
      map((action: any) => action.payload),
      mergeMap(() => [
        new ClearWorldpayGuaranteedPayments()
      ]),
    )
  );

  constructor(private actions$: Actions) {
  }
}
