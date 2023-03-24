import { ApplePayAuthorization, ApplePayPaymentRequest, ThreeDsDDCInfo, ThreeDsInfo } from '../connectors/worldpay.adapter';
import { SafeResourceUrl } from '@angular/platform-browser';
import { Address } from '@spartacus/core';
import { ApmData, APMRedirectResponse } from '../interfaces';
import { WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_KEY, WorldpayGuaranteedState } from './worldpay-guaranteed-payments/worldpay-guaranteed-payments.state';

export const WORLDPAY_FEATURE = 'Worldpay';

export interface StateWithWorldpay {
  [WORLDPAY_FEATURE]: WorldpayState;
  [WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_KEY]: WorldpayGuaranteedState;
}

export interface WorldpayState {
  publicKey: string;
  cvn: string;
  cseToken: string;
  threeDsDDCIframeUrl?: SafeResourceUrl;
  threeDsChallengeIframeUrl?: SafeResourceUrl;
  threeDsDDCInfo: ThreeDsDDCInfo;
  threeDsChallengeInfo: ThreeDsInfo;
  loading: boolean;

  applePayPaymentRequest?: ApplePayPaymentRequest;
  applePayMerchantSession?: any;
  applePayAuthorization?: ApplePayAuthorization;

  googlePayMerchantConfiguration?: any;

  apm: ApmData;
  apmRedirect?: APMRedirectResponse;
  availableApms?: ApmData[];
  apmLoading?: boolean;

  paymentAddress?: Address;

  fraudSightEnabled?: boolean;
  fraudSightId?: string;
}
