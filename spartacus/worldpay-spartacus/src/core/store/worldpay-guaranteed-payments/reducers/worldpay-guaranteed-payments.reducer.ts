import { WORLDPAY_GUARANTEED_PAYMENTS_SESSION_ID } from '../actions/worldpay-guaranteed-payments-session-id.actions';
import { InjectionToken, Provider } from '@angular/core';
import { ActionReducerMap } from '@ngrx/store';
import { StateUtils } from '@spartacus/core';
import { WorldpayGuaranteedState } from '../worldpay-guaranteed-payments.state';
import { reducerWorldpayGuaranteedPaymentsSessionId } from './world-pay-guaranteed-payments-session-id.reducer';
import { reducerWorldpayGuaranteedPaymentsEnabled } from './world-pay-guaranteed-payments-enabled.reducer';
import { WORLDPAY_GUARANTEED_PAYMENTS_ENABLED } from '../actions/worldpay-guaranteed-payments-enabled.actions';

export const getReducersWorldpayGuaranteedPayments = (): ActionReducerMap<WorldpayGuaranteedState> => ({
  enabled: StateUtils.loaderReducer<boolean>(WORLDPAY_GUARANTEED_PAYMENTS_ENABLED, reducerWorldpayGuaranteedPaymentsEnabled),
  sessionId: StateUtils.loaderReducer<string | null>(WORLDPAY_GUARANTEED_PAYMENTS_SESSION_ID, reducerWorldpayGuaranteedPaymentsSessionId),
});

export const reducerTokenWorldpayGuaranteedPayments: InjectionToken<ActionReducerMap<WorldpayGuaranteedState>> =
  new InjectionToken<ActionReducerMap<WorldpayGuaranteedState>>('WorldpayGuaranteedPaymentsReducers');

export const reducerProviderWorldpayGuaranteedPayments: Provider = {
  provide: reducerTokenWorldpayGuaranteedPayments,
  useFactory: getReducersWorldpayGuaranteedPayments
};
