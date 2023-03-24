import * as WorldpayActions from './worldpay.action';
import {
  UseExistingWorldpayPaymentDetails
} from './worldpay.action';
import { ThreeDsDDCInfo, ThreeDsInfo } from '../connectors/worldpay.adapter';
import { Address, PaymentDetails } from '@spartacus/core';
import { InitialPaymentRequestPayload } from '../interfaces';

describe('Worldpay Actions', () => {
  describe('UseExistingWorldpayPaymentDetails', () => {
    it('should create an action', () => {
      const payload = {
        userId: 'userId',
        cartId: 'cartId',
        paymentDetails: {
          id: 'paymentDetails'
        }
      };

      const action = new WorldpayActions.UseExistingWorldpayPaymentDetails(
        payload
      );
      expect({ ...action }).toEqual({
        type: WorldpayActions.USE_EXISTING_WORLDPAY_PAYMENT_DETAILS,
        payload
      });
    });

    it('should create a success action', () => {
      const payload = {
        id: 'paymentDetails'
      };
      const action = new WorldpayActions.UseExistingWorldpayPaymentDetailsSuccess(
        payload
      );
      expect({ ...action }).toEqual({
        type: WorldpayActions.USE_EXISTING_WORLDPAY_PAYMENT_DETAILS_SUCCESS,
        payload
      });
    });

    it('should create a failure action', () => {
      const payload = {
        error: 'error'
      };
      const action = new WorldpayActions.UseExistingWorldpayPaymentDetailsFail(
        payload
      );
      expect({ ...action }).toEqual({
        type: WorldpayActions.USE_EXISTING_WORLDPAY_PAYMENT_DETAILS_FAIL,
        payload
      });
    });
  });

  describe('SetPaymentAddress', () => {
    it('should create an action', () => {
      const payload = {
        userId: 'userId',
        cartId: 'cartId',
        address: {
          postalCode: 'AA1 2BB'
        }
      };

      const action = new WorldpayActions.SetPaymentAddress(payload);
      expect({ ...action }).toEqual({
        type: WorldpayActions.SET_PAYMENT_ADDRESS,
        payload
      });
    });

    it('should create a success action', () => {
      const payload: Address = {firstName: 'john', lastName: 'doe'};
      const action = new WorldpayActions.SetPaymentAddressSuccess(payload);
      expect({ ...action }).toEqual({
        type: WorldpayActions.SET_PAYMENT_ADDRESS_SUCCESS,
        payload
      });
    });

    it('should create a failure action', () => {
      const payload = {
        error: 'error'
      };
      const action = new WorldpayActions.SetPaymentAddressFail(payload);
      expect({ ...action }).toEqual({
        type: WorldpayActions.SET_PAYMENT_ADDRESS_FAIL,
        payload
      });
    });
  });

  describe('GetWorldpayDDCJwt', () => {
    it('should create an action', () => {
      const action = new WorldpayActions.GetWorldpayDDCJwt();

      expect({ ...action }).toEqual({
        type: WorldpayActions.GET_WORLDPAY_DDC_JWT
      });
    });

    it('should create an success action', () => {
      const payload: ThreeDsDDCInfo = {
        jwt: 'jwt-value',
        ddcUrl: 'https://ddc.aws.e2y.io'
      };
      const action = new WorldpayActions.GetWorldpayDDCJwtSuccess(payload);

      expect(action.type).toEqual(WorldpayActions.GET_WORLDPAY_DDC_JWT_SUCCESS);
      expect(action.payload).toEqual(payload);
    });

    it('should create an fail action', () => {
      const payload = 'error message';
      const action = new WorldpayActions.GetWorldpayDDCJwtFail(payload);

      expect(action.type).toEqual(WorldpayActions.GET_WORLDPAY_DDC_JWT_FAIL);
      expect(action.payload).toEqual(payload);
    });
  });

  describe('InitialPaymentRequest', () => {
    it('should create an action', () => {
      const paymentDetails: PaymentDetails = {
        cardNumber: '4444333322221111'
      };

      const payload: InitialPaymentRequestPayload = {
        challengeWindowSize: '320x400',
        dfReferenceId: 'ref id',
        cartId: 'current',
        userId: 'testUser',
        acceptedTermsAndConditions: true,
        cseToken: '1234-5678',
        paymentDetails
      };

      const action = new WorldpayActions.InitialPaymentRequest(payload);

      expect({ ...action }).toEqual({
        type: WorldpayActions.INITIAL_PAYMENT_REQUEST,
        payload
      });
    });

    it('should create an challenge action', () => {
      const payload: ThreeDsInfo = {
        merchantData: '123-456',
        threeDSFlexData: {
          autoSubmitThreeDSecureFlexUrl: 'https://autosubmiturl.aws.e2y.io',
          challengeUrl: 'https://challenge.aws.e2y.io',
          jwt: 'jwt-123',
          entry: []
        }
      };

      const action = new WorldpayActions.InitialPaymentRequestChallengeRequired(
        payload
      );

      expect(action.type).toEqual(
        WorldpayActions.INITIAL_PAYMENT_REQUEST_CHALLENGE_REQUIRED
      );
      expect(
        action.payload.threeDSFlexData.autoSubmitThreeDSecureFlexUrl
      ).toEqual('https://autosubmiturl.aws.e2y.io');
      expect(action.payload.threeDSFlexData.challengeUrl).toEqual(
        'https://challenge.aws.e2y.io'
      );
      expect(action.payload.threeDSFlexData.jwt).toEqual('jwt-123');
      expect(action.payload.merchantData).toEqual('123-456');
    });

    it('should create an fail action', () => {
      const payload = 'error message';
      const action = new WorldpayActions.InitialPaymentRequestFail(payload);

      expect({ ...action }).toEqual({
        type: WorldpayActions.INITIAL_PAYMENT_REQUEST_FAIL,
        payload
      });
    });
  });
});
