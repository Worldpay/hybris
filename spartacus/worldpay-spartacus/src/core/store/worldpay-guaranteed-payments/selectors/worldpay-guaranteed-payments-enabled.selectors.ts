import { createSelector, MemoizedSelector } from '@ngrx/store';
import { StateUtils } from '@spartacus/core';
import { StateWithWorldpay } from '../../worldpay.state';
import { getWorldpayGuaranteedPaymentState, WorldpayGuaranteedState } from '../worldpay-guaranteed-payments.state';

const getWorldpayGuaranteedPaymentsEnabledSelector: (state: WorldpayGuaranteedState) => StateUtils.LoaderState<boolean> = (state) => state.enabled;

export const getWorldpayGuaranteedPaymentsEnabledState: MemoizedSelector<StateWithWorldpay,
  StateUtils.LoaderState<boolean>> = createSelector(getWorldpayGuaranteedPaymentState, getWorldpayGuaranteedPaymentsEnabledSelector);

export const getWorldpayGuaranteedPaymentsEnabledValue: MemoizedSelector<StateWithWorldpay,
  boolean> = createSelector(getWorldpayGuaranteedPaymentsEnabledState, (state) => StateUtils.loaderValueSelector(state));

export const getWorldpayGuaranteedPaymentsEnabledLoading: MemoizedSelector<StateWithWorldpay,
  boolean> = createSelector(getWorldpayGuaranteedPaymentsEnabledState, (state) => StateUtils.loaderLoadingSelector(state));

export const getWorldpayGuaranteedPaymentsEnabledSuccess: MemoizedSelector<StateWithWorldpay,
  boolean> = createSelector(getWorldpayGuaranteedPaymentsEnabledState, (state) => StateUtils.loaderSuccessSelector(state));

export const getWorldpayGuaranteedPaymentsEnabledFail: MemoizedSelector<StateWithWorldpay,
  boolean> = createSelector(getWorldpayGuaranteedPaymentsEnabledState, (state) => StateUtils.loaderErrorSelector(state));



