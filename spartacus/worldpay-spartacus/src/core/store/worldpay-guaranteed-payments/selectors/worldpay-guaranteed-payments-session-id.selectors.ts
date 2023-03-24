import { createSelector, MemoizedSelector } from '@ngrx/store';
import { StateUtils } from '@spartacus/core';
import { StateWithWorldpay } from '../../worldpay.state';
import { getWorldpayGuaranteedPaymentState, WorldpayGuaranteedState } from '../worldpay-guaranteed-payments.state';

const getWorldpayGuaranteedPaymentsSessionIdSelector: (state: WorldpayGuaranteedState) => StateUtils.LoaderState<string> = (state: WorldpayGuaranteedState) => state.sessionId;

export const getWorldpayGuaranteedPaymentsSessionIdState: MemoizedSelector<StateWithWorldpay,
  StateUtils.LoaderState<string>> = createSelector(getWorldpayGuaranteedPaymentState, getWorldpayGuaranteedPaymentsSessionIdSelector);

export const getWorldpayGuaranteedPaymentsSessionIdValue: MemoizedSelector<StateWithWorldpay,
  string> = createSelector(getWorldpayGuaranteedPaymentsSessionIdState, (state) => StateUtils.loaderValueSelector(state));

export const getWorldpayGuaranteedPaymentsSessionIdLoading: MemoizedSelector<StateWithWorldpay,
  boolean> = createSelector(getWorldpayGuaranteedPaymentsSessionIdState, (state) => StateUtils.loaderLoadingSelector(state));

export const getWorldpayGuaranteedPaymentsSessionIdSuccess: MemoizedSelector<StateWithWorldpay,
  boolean> = createSelector(getWorldpayGuaranteedPaymentsSessionIdState, (state) => StateUtils.loaderSuccessSelector(state));

export const getWorldpayGuaranteedPaymentsSessionIdFail: MemoizedSelector<StateWithWorldpay,
  boolean> = createSelector(getWorldpayGuaranteedPaymentsSessionIdState, (state) => StateUtils.loaderErrorSelector(state));



