import { StateUtils } from '@spartacus/core';

export const WORLDPAY_GUARANTEED_PAYMENTS_ENABLED = '[WorldpayGuaranteedPaymentsEnabled] WorldpayGuaranteedPayments Enabled';
export const IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED = '[WorldpayGuaranteedPayments] Is WorldpayGuaranteedPayments Enabled';
export const IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED_SUCCESS = '[WorldpayGuaranteedPayments] Is WorldpayGuaranteedPayments Enabled Success';
export const IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED_FAIL = '[WorldpayGuaranteedPayments] Is WorldpayGuaranteedPayments Enabled Fail';

export class IsWorldpayGuaranteedPaymentsEnabled extends StateUtils.LoaderLoadAction {
  readonly type = IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED;

  constructor() {
    super(WORLDPAY_GUARANTEED_PAYMENTS_ENABLED);
  }
}

export class IsWorldpayGuaranteedPaymentsEnabledSuccess extends StateUtils.LoaderSuccessAction {
  readonly type = IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED_SUCCESS;

  constructor(public payload: boolean) {
    super(WORLDPAY_GUARANTEED_PAYMENTS_ENABLED);
  }
}

export class IsWorldpayGuaranteedPaymentsEnabledFail extends StateUtils.LoaderFailAction {
  readonly type = IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED_FAIL;

  constructor(public payload: any) {
    super(WORLDPAY_GUARANTEED_PAYMENTS_ENABLED, payload);
  }
}

export type WorldpayGuaranteedPaymentsEnabledActions =
  IsWorldpayGuaranteedPaymentsEnabled |
  IsWorldpayGuaranteedPaymentsEnabledSuccess |
  IsWorldpayGuaranteedPaymentsEnabledFail;
