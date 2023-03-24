import { initialStateWorldpayGuaranteedPaymentsEnabled, reducerWorldpayGuaranteedPaymentsEnabled } from './world-pay-guaranteed-payments-enabled.reducer';
import {
  IsWorldpayGuaranteedPaymentsEnabled,
  IsWorldpayGuaranteedPaymentsEnabledFail,
  IsWorldpayGuaranteedPaymentsEnabledSuccess
} from '../actions/worldpay-guaranteed-payments-enabled.actions';

describe('WorldPayGuaranteedPaymentsEnabled Reducer', () => {
  const initialState = false;

  describe('an unknown action', () => {
    it('should return the previous state', () => {
      const action = {} as any;

      const result = reducerWorldpayGuaranteedPaymentsEnabled(initialStateWorldpayGuaranteedPaymentsEnabled, action);

      expect(result).toBe(initialStateWorldpayGuaranteedPaymentsEnabled);
    });
  });

  it('should get initialState', () => {
    const state = reducerWorldpayGuaranteedPaymentsEnabled(
      initialStateWorldpayGuaranteedPaymentsEnabled,
      new IsWorldpayGuaranteedPaymentsEnabled()
    );
    expect(state).toBeFalse();
  });

  it('should enable Worldpay Guaranteed Payments', () => {
    const state = reducerWorldpayGuaranteedPaymentsEnabled(
      initialStateWorldpayGuaranteedPaymentsEnabled,
      new IsWorldpayGuaranteedPaymentsEnabled()
    );
    expect(state).toBeFalse();

    reducerWorldpayGuaranteedPaymentsEnabled(
      initialStateWorldpayGuaranteedPaymentsEnabled,
      new IsWorldpayGuaranteedPaymentsEnabledSuccess(true)
    );

    expect();
  });

  it('should clear Worldpay Guaranteed Payments', () => {
    const state = reducerWorldpayGuaranteedPaymentsEnabled(
      initialState,
      new IsWorldpayGuaranteedPaymentsEnabled()
    );
    expect(state).toBeFalse();

    reducerWorldpayGuaranteedPaymentsEnabled(
      initialState,
      new IsWorldpayGuaranteedPaymentsEnabledFail({ error: 'error' })
    );

    expect(state).toBeFalse();
  });
});
