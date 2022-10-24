import { WorldpayState } from './worldpay.state';
import * as WorldpayActions from './worldpay.action';
import { InjectionToken, Provider } from '@angular/core';
import { ActionReducerMap } from '@ngrx/store';
import { ApmData, PaymentMethod } from '../interfaces';

export const initialState: WorldpayState = {
  publicKey: '',
  cvn: '',
  cseToken: '',
  threeDsDDCInfo: null,
  threeDsChallengeInfo: null,
  loading: false,
  apm: {
    code: PaymentMethod.Card
  } as ApmData,
  apmLoading: false,

  fraudSightEnabled: false,
};

export const reducer = (
  state = initialState,
  action: WorldpayActions.WorldpayAction
): WorldpayState => {
  switch (action.type) {
  case WorldpayActions.GET_WORLDPAY_PUBLIC_KEY_SUCCESS: {
    return {
      ...state,
      publicKey: action.payload
    };
  }

  case WorldpayActions.CREATE_WORLDPAY_PAYMENT_DETAILS: {
    return {
      ...state,
      cseToken: action.payload.cseToken,
      cvn: action.payload.paymentDetails.cvn,
      loading: true
    };
  }

  case WorldpayActions.USE_EXISTING_WORLDPAY_PAYMENT_DETAILS: {
    return {
      ...state,
      cvn: action.payload.paymentDetails.cvn,
      loading: true
    };
  }

  case WorldpayActions.CREATE_WORLDPAY_PAYMENT_DETAILS_SUCCESS:
  case WorldpayActions.CREATE_WORLDPAY_PAYMENT_DETAILS_FAIL:
  case WorldpayActions.USE_EXISTING_WORLDPAY_PAYMENT_DETAILS_SUCCESS:
  case WorldpayActions.INITIAL_PAYMENT_REQUEST_FAIL:
  case WorldpayActions.USE_EXISTING_WORLDPAY_PAYMENT_DETAILS_FAIL: {
    return {
      ...state,
      loading: false
    };
  }

  case WorldpayActions.GET_WORLDPAY_DDC_JWT_SUCCESS:
    return {
      ...state,
      threeDsDDCInfo: action.payload
    };

  case WorldpayActions.INITIAL_PAYMENT_REQUEST:
    return {
      ...state,
      threeDsDDCIframeUrl: null,
      threeDsChallengeIframeUrl: null,
      threeDsDDCInfo: null,
      threeDsChallengeInfo: null,
      loading: true
    };

  case WorldpayActions.INITIAL_PAYMENT_REQUEST_CHALLENGE_REQUIRED: {
    const values = {};
    action.payload.threeDSFlexData.entry.map(kv => {
      values[kv.key] = kv.value;
    });

    return {
      ...state,
      threeDsChallengeInfo: {
        ...action.payload,
        threeDSFlexData: {
          ...action.payload.threeDSFlexData,
          ...values
        }
      }
    };
  }

  case WorldpayActions.SET_WORLDPAY_DDC_IFRAME_URL:
    return {
      ...state,
      threeDsDDCIframeUrl: action.payload
    };

  case WorldpayActions.SET_WORLDPAY_CHALLENGE_IFRAME_URL:
    return {
      ...state,
      threeDsDDCIframeUrl: null,
      threeDsChallengeIframeUrl: action.payload,
      loading: false
    };

  case WorldpayActions.CHALLENGE_ACCEPTED_FAIL:
    return {
      ...state,
      threeDsChallengeIframeUrl: null
    };

  case WorldpayActions.CLEAR_PAYMENT_DETAILS:
    return {
      ...state,
      threeDsChallengeIframeUrl: null,
      threeDsDDCIframeUrl: null,
      cvn: null,
      cseToken: null,
      publicKey: null,
      threeDsChallengeInfo: null,
      threeDsDDCInfo: null,
      loading: false,
      applePayPaymentRequest: null,
      applePayAuthorization: null,
      applePayMerchantSession: null,
      apm: null,
      availableApms: null,
    };

  case WorldpayActions.REQUEST_APPLE_PAY_PAYMENT_REQUEST_SUCCESS:
    return {
      ...state,
      applePayPaymentRequest: action.payload,
    };

  case WorldpayActions.START_APPLE_PAY_SESSION: {
    return {
      ...state,
      applePayAuthorization: null,
      applePayMerchantSession: null
    };
  }
  case WorldpayActions.VALIDATE_APPLE_PAY_MERCHANT_SUCCESS:
    return {
      ...state,
      applePayMerchantSession: action.payload
    };

  case WorldpayActions.AUTHORISE_APPLE_PAY_PAYMENT_SUCCESS:
    return {
      ...state,
      applePayAuthorization: action.payload
    };

  case WorldpayActions.GET_CONFIG_GOOGLE_PAY_SUCCESS:
    return {
      ...state,
      googlePayMerchantConfiguration: action.payload
    };

  case WorldpayActions.SET_SELECTED_APM:
    return {
      ...state,
      apm: action.payload
    };

  case WorldpayActions.GET_APM_REDIRECT_URL:
    return {
      ...state,
      apmRedirect: null
    };

  case WorldpayActions.GET_APM_REDIRECT_URL_SUCCESS:
    return {
      ...state,
      apmRedirect: action.payload
    };

  case WorldpayActions.GET_AVAILABLE_APMS:
    return {
      ...state,
      loading: true
    };
  case WorldpayActions.GET_AVAILABLE_APMS_SUCCESS:
    return {
      ...state,
      availableApms: action.payload,
      loading: false,
    };
  case WorldpayActions.SET_PAYMENT_ADDRESS_SUCCESS:
    return {
      ...state,
      paymentAddress: action.payload
    };

  case WorldpayActions.START_LOADER:
  case WorldpayActions.AUTHORISE_GOOGLE_PAY_PAYMENT:
    return {
      ...state,
      loading: true,
    };

  case WorldpayActions.AUTHORISE_GOOGLE_PAY_PAYMENT_FAIL:
    return {
      ...state,
      loading: false,
    };

  case WorldpayActions.AUTHORISE_GOOGLE_PAY_PAYMENT_SUCCESS:
    return {
      ...state,
      loading: false,
    };
  case WorldpayActions.SET_FRAUD_SIGHT_ID:
    return {
      ...state,
      fraudSightId: action.payload
    };

  case WorldpayActions.IS_FRAUD_SIGHT_ENABLED_SUCCESS:
    return {
      ...state,
      fraudSightEnabled: action.payload
    };
  }

  return state;
};

export const reducerToken: InjectionToken<ActionReducerMap<WorldpayState>> = new InjectionToken<ActionReducerMap<WorldpayState>>('WorldpayReducers');

export const reducerProvider: Provider = {
  provide: reducerToken,
  useFactory: reducer
};
