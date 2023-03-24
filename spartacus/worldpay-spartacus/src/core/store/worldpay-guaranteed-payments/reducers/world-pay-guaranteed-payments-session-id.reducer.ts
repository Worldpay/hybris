import {
  CLEAR_WORLDPAY_GUARANTEED_PAYMENTS,
  LOAD_WORLDPAY_GUARANTEED_PAYMENTS_FAIL,
  LOAD_WORLDPAY_GUARANTEED_PAYMENTS_SUCCESS,
  WorldpayGuaranteedPaymentsSessionIdActions
} from '../actions/worldpay-guaranteed-payments-session-id.actions';
import { AuthActions } from '@spartacus/core';

export const initialStateWorldpayGuaranteedPayments: string = '';

export const reducerWorldpayGuaranteedPaymentsSessionId = (
  state = initialStateWorldpayGuaranteedPayments,
  action: WorldpayGuaranteedPaymentsSessionIdActions
): string => {
  switch (action.type) {
  case LOAD_WORLDPAY_GUARANTEED_PAYMENTS_SUCCESS: {
    return action.payload;
  }

  case LOAD_WORLDPAY_GUARANTEED_PAYMENTS_FAIL || AuthActions.LOGOUT || CLEAR_WORLDPAY_GUARANTEED_PAYMENTS: {
    return initialStateWorldpayGuaranteedPayments;
  }

  default:
    return state;
  }
};
