import { initialStateWorldpayGuaranteedPayments, reducerWorldpayGuaranteedPaymentsSessionId } from './world-pay-guaranteed-payments-session-id.reducer';

describe('WorldPayGuaranteedSessionId Reducer', () => {
  describe('an unknown action', () => {
    it('should return the previous state', () => {
      const action = {} as any;

      const result = reducerWorldpayGuaranteedPaymentsSessionId(initialStateWorldpayGuaranteedPayments, action);

      expect(result).toBe(initialStateWorldpayGuaranteedPayments);
    });
  });
});
