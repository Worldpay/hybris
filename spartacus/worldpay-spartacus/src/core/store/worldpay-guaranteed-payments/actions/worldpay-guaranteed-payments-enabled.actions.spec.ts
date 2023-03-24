import { StateUtils } from '@spartacus/core';
import {
  IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED,
  IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED_FAIL,
  IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED_SUCCESS,
  IsWorldpayGuaranteedPaymentsEnabled,
  IsWorldpayGuaranteedPaymentsEnabledFail,
  IsWorldpayGuaranteedPaymentsEnabledSuccess,
  WORLDPAY_GUARANTEED_PAYMENTS_ENABLED
} from './worldpay-guaranteed-payments-enabled.actions';

describe('Worldpay Guarantee Payment Enabled Actions', () => {
  describe('IsWorldpayGuaranteedPaymentsEnabled', () => {
    it('should create the action', () => {
      const action = new IsWorldpayGuaranteedPaymentsEnabled();
      expect({ ...action }).toEqual({
        type: IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED,
        meta: StateUtils.loadMeta(
          WORLDPAY_GUARANTEED_PAYMENTS_ENABLED
        ),
      });
    });

    describe('IsWorldpayGuaranteedPaymentsEnabledSuccess', () => {
      it('should create the action', () => {
        const action = new IsWorldpayGuaranteedPaymentsEnabledSuccess(true);
        expect({ ...action }).toEqual({
          type: IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED_SUCCESS,
          payload: true,
          meta: StateUtils.successMeta(
            WORLDPAY_GUARANTEED_PAYMENTS_ENABLED
          ),
        });
      });
    });

    describe('IsWorldpayGuaranteedPaymentsEnabledFail', () => {
      it('should create the action', () => {
        const action = new IsWorldpayGuaranteedPaymentsEnabledFail(true);
        expect({ ...action }).toEqual({
          type: IS_WORLDPAY_GUARANTEED_PAYMENTS_ENABLED_FAIL,
          payload: true,
          meta: StateUtils.failMeta(
            WORLDPAY_GUARANTEED_PAYMENTS_ENABLED
          ),
        });
      });
    });
  });
});
