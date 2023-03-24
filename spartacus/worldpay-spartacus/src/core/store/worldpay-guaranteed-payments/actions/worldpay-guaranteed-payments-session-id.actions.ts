import { StateUtils } from '@spartacus/core';

export const WORLDPAY_GUARANTEED_PAYMENTS_SESSION_ID = '[WorldpayGuaranteedPayments] Session Id';
export const LOAD_WORLDPAY_GUARANTEED_PAYMENTS = '[WorldpayGuaranteedPayments] Load WorldpayGuaranteedPayments';
export const LOAD_WORLDPAY_GUARANTEED_PAYMENTS_SUCCESS = '[WorldpayGuaranteedPayments] Load WorldpayGuaranteedPayments Success';
export const LOAD_WORLDPAY_GUARANTEED_PAYMENTS_FAIL = '[WorldpayGuaranteedPayments] Load WorldpayGuaranteedPayments Fail';
export const CLEAR_WORLDPAY_GUARANTEED_PAYMENTS = '[WorldpayGuaranteedPayments] Clear WorldpayGuaranteedPayments';

export class LoadWorldpayGuaranteedPayments extends StateUtils.LoaderLoadAction {
  readonly type = LOAD_WORLDPAY_GUARANTEED_PAYMENTS;

  constructor(public payload: string) {
    super(WORLDPAY_GUARANTEED_PAYMENTS_SESSION_ID);
  }
}

export class LoadWorldpayGuaranteedPaymentsSuccess extends StateUtils.LoaderSuccessAction {
  readonly type = LOAD_WORLDPAY_GUARANTEED_PAYMENTS_SUCCESS;

  constructor(public payload: string) {
    super(WORLDPAY_GUARANTEED_PAYMENTS_SESSION_ID);
  }
}

export class LoadWorldpayGuaranteedPaymentsFail extends StateUtils.LoaderFailAction {
  readonly type = LOAD_WORLDPAY_GUARANTEED_PAYMENTS_FAIL;

  constructor(public payload: any) {
    super(WORLDPAY_GUARANTEED_PAYMENTS_SESSION_ID, payload);
  }
}

export class ClearWorldpayGuaranteedPayments extends StateUtils.LoaderResetAction {
  readonly type = CLEAR_WORLDPAY_GUARANTEED_PAYMENTS;

  constructor() {
    super(WORLDPAY_GUARANTEED_PAYMENTS_SESSION_ID);
  }
}

export type WorldpayGuaranteedPaymentsSessionIdActions =
  LoadWorldpayGuaranteedPayments |
  LoadWorldpayGuaranteedPaymentsSuccess |
  LoadWorldpayGuaranteedPaymentsFail;


