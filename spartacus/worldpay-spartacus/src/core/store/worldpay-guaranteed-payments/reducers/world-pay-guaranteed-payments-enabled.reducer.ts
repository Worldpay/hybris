import { CLEAR_WORLDPAY_GUARANTEED_PAYMENTS } from '../actions/worldpay-guaranteed-payments-session-id.actions';
import { AuthActions } from '@spartacus/core';
import {
  IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED_FAIL,
  IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED_SUCCESS,
  WorldpayGuaranteedPaymentsEnabledActions
} from '../actions/worldpay-guaranteed-payments-enabled.actions';

export const initialStateWorldpayGuaranteedPaymentsEnabled: boolean = false;

export const reducerWorldpayGuaranteedPaymentsEnabled = (
  state = initialStateWorldpayGuaranteedPaymentsEnabled,
  action: WorldpayGuaranteedPaymentsEnabledActions
): boolean => {
  switch (action.type) {
  case IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED_SUCCESS: {
    return action.payload;
  }

  case IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED_FAIL || AuthActions.LOGOUT || CLEAR_WORLDPAY_GUARANTEED_PAYMENTS: {
    return initialStateWorldpayGuaranteedPaymentsEnabled;
  }

  default:
    return state;
  }
};
