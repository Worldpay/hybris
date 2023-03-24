import { StateUtils } from '@spartacus/core';
import { createFeatureSelector, MemoizedSelector } from '@ngrx/store';
import { StateWithWorldpay } from '../worldpay.state';

export const WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_KEY = 'worldpayGuaranteedPayments';

export interface WorldpayGuaranteedState {
  enabled: StateUtils.LoaderState<boolean>;
  sessionId: StateUtils.LoaderState<string>;
}

export const getWorldpayGuaranteedPaymentState: MemoizedSelector<StateWithWorldpay,
  WorldpayGuaranteedState> = createFeatureSelector<WorldpayGuaranteedState>(WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_KEY);

