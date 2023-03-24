import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import {
  IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED,
  IsWorldpayGuaranteedPaymentsEnabledFail,
  IsWorldpayGuaranteedPaymentsEnabledSuccess
} from '../actions/worldpay-guaranteed-payments-enabled.actions';
import { WorldpayConnector } from '../../../connectors/worldpay.connector';
import { of } from 'rxjs';

@Injectable()
export class WorldpayGuaranteedPaymentsEnabledEffects {

  isWorldpayGuaranteedPaymentsEnabled$ = createEffect(
    () => this.actions$.pipe(
      ofType(IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED),
      map((action: any) => action.payload),
      mergeMap(() => this.worldpayConnector.isGuaranteedPaymentsEnabled()
        .pipe(
          switchMap((payload) => [
            new IsWorldpayGuaranteedPaymentsEnabledSuccess(payload),
          ]),
          catchError(error =>
            of(new IsWorldpayGuaranteedPaymentsEnabledFail(error))
          )
        )),
    )
  );

  constructor(
    private actions$: Actions,
    private worldpayConnector: WorldpayConnector
  ) {
  }
}
