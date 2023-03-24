import { StateUtils } from '@spartacus/core';
import {
  LOAD_WORLDPAY_GUARANTEED_PAYMENTS,
  LOAD_WORLDPAY_GUARANTEED_PAYMENTS_FAIL,
  LOAD_WORLDPAY_GUARANTEED_PAYMENTS_SUCCESS,
  LoadWorldpayGuaranteedPayments,
  LoadWorldpayGuaranteedPaymentsFail,
  LoadWorldpayGuaranteedPaymentsSuccess,
  WORLDPAY_GUARANTEED_PAYMENTS_SESSION_ID
} from './worldpay-guaranteed-payments-session-id.actions';

describe('Payment Types Actions', () => {
  describe('LoadWorldpayGuaranteedPayments', () => {
    it('should create the action', () => {
      const action = new LoadWorldpayGuaranteedPayments('test');
      expect({ ...action }).toEqual({
        type: LOAD_WORLDPAY_GUARANTEED_PAYMENTS,
        payload: 'test',
        meta: StateUtils.loadMeta(
          WORLDPAY_GUARANTEED_PAYMENTS_SESSION_ID
        ),
      });
    });

    describe('LoadWorldpayGuaranteedPaymentsSuccess', () => {
      it('should create the action', () => {
        const action = new LoadWorldpayGuaranteedPaymentsSuccess('test');
        expect({ ...action }).toEqual({
          type: LOAD_WORLDPAY_GUARANTEED_PAYMENTS_SUCCESS,
          payload: 'test',
          meta: StateUtils.successMeta(
            WORLDPAY_GUARANTEED_PAYMENTS_SESSION_ID
          ),
        });
      });
    });

    describe('LoadWorldpayGuaranteedPaymentsFail', () => {
      it('should create the action', () => {
        const action = new LoadWorldpayGuaranteedPaymentsFail(true);
        expect({ ...action }).toEqual({
          type: LOAD_WORLDPAY_GUARANTEED_PAYMENTS_FAIL,
          payload: true,
          meta: StateUtils.failMeta(
            WORLDPAY_GUARANTEED_PAYMENTS_SESSION_ID,
            true
          ),
        });
      });
    });
  });
});
