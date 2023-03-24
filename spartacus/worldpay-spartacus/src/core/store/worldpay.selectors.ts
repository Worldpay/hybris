import { createFeatureSelector, createSelector, MemoizedSelector } from '@ngrx/store';
import { StateWithWorldpay, WORLDPAY_FEATURE, WorldpayState } from './worldpay.state';
import { ApplePayAuthorization, ApplePayPaymentRequest, ThreeDsDDCInfo, ThreeDsInfo } from '../connectors/worldpay.adapter';
import { SafeResourceUrl } from '@angular/platform-browser';
import { Address, StateUtils } from '@spartacus/core';
import { ApmData, APMRedirectResponse, GooglePayMerchantConfiguration } from '../interfaces';

const getWorldpayCsePublicKeySelector = (state: WorldpayState): string =>
  state.publicKey;
const getWorldpayCvnSelector = (state: WorldpayState): string => state.cvn;
const getWorldpayCseTokenSelector = (state: WorldpayState): string => state.cseToken;
const getWorldpayDDCInfoSelector = (state: WorldpayState): ThreeDsDDCInfo =>
  state.threeDsDDCInfo;
const getWorldpayThreeDsChallengeInfoSelector = (state: WorldpayState): ThreeDsInfo =>
  state.threeDsChallengeInfo;
const getWorldpayThreeDsDDCIframeUrlSelector = (state: WorldpayState): SafeResourceUrl =>
  state.threeDsDDCIframeUrl;
const getWorldpayThreeDsChallengeIframeUrlSelector = (state: WorldpayState): SafeResourceUrl =>
  state.threeDsChallengeIframeUrl;

const getWorldpayApplePayPaymentRequestSelector = (state: WorldpayState): ApplePayPaymentRequest =>
  state.applePayPaymentRequest;
const getWorldpayApplePayMerchantSessionSelector = (state: WorldpayState): any =>
  state.applePayMerchantSession;
const getWorldpayApplePayPaymentAuthorizationSelector = (
  state: WorldpayState
): ApplePayAuthorization => state.applePayAuthorization;

const getWorldpaygooglePayMerchantConfigurationSelector = (
  state: WorldpayState
): any => state.googlePayMerchantConfiguration;

const getWorldpaySelectedAPMSelector = (state: WorldpayState): ApmData => state.apm;
const getWorldpayAPMRedirectSelector = (state: WorldpayState): APMRedirectResponse =>
  state.apmRedirect;
const getWorldpayAvailableApmsSelector = (state: WorldpayState): ApmData[] =>
  state.availableApms;

const getWorldpayPaymentAddressSelector = (state: WorldpayState): Address =>
  state.paymentAddress;

const getWorldpayFraudSightEnabledSelector = (state: WorldpayState): boolean => state.fraudSightEnabled;
const getWorldpayFraudSightIdSelector = (state: WorldpayState): string => state.fraudSightId;

export const getWorldpayState: MemoizedSelector<StateWithWorldpay,
  WorldpayState> = createFeatureSelector<WorldpayState>(WORLDPAY_FEATURE);

export const getWorldpayCseToken: MemoizedSelector<StateWithWorldpay,
  string> = createSelector(getWorldpayState, getWorldpayCseTokenSelector);

export const getWorldpayCsePublicKey: MemoizedSelector<StateWithWorldpay,
  string> = createSelector(getWorldpayState, getWorldpayCsePublicKeySelector);

export const getWorldpayCvn: MemoizedSelector<StateWithWorldpay,
  string> = createSelector(getWorldpayState, getWorldpayCvnSelector);

export const getWorldpayThreeDsDDCInfo: MemoizedSelector<StateWithWorldpay,
  ThreeDsDDCInfo> = createSelector(getWorldpayState, getWorldpayDDCInfoSelector);

export const getWorldpayThreeDsChallengeInfo: MemoizedSelector<StateWithWorldpay,
  ThreeDsInfo> = createSelector(getWorldpayState, getWorldpayThreeDsChallengeInfoSelector);

export const getWorldpayThreeDsDDCIframeUrl: MemoizedSelector<StateWithWorldpay,
  SafeResourceUrl> = createSelector(getWorldpayState, getWorldpayThreeDsDDCIframeUrlSelector);

export const getWorldpayThreeDsChallengeIframeUrl: MemoizedSelector<StateWithWorldpay,
  SafeResourceUrl> = createSelector(
    getWorldpayState,
    getWorldpayThreeDsChallengeIframeUrlSelector
  );

export const getWorldpayLoading: MemoizedSelector<StateWithWorldpay,
  boolean> = createSelector(getWorldpayState, (state) => StateUtils.loaderLoadingSelector(state));

export const getWorldpayApplePayPaymentRequest: MemoizedSelector<StateWithWorldpay,
  ApplePayPaymentRequest> = createSelector(getWorldpayState, getWorldpayApplePayPaymentRequestSelector);

export const getWorldpayApplePayMerchantSession: MemoizedSelector<StateWithWorldpay,
  any> = createSelector(
    getWorldpayState,
    getWorldpayApplePayMerchantSessionSelector
  );

export const getWorldpayApplePayPaymentAuthorization: MemoizedSelector<StateWithWorldpay,
  ApplePayAuthorization> = createSelector(
    getWorldpayState,
    getWorldpayApplePayPaymentAuthorizationSelector
  );

export const getWorldpayGooglePayMerchantConfiguration: MemoizedSelector<StateWithWorldpay,
  GooglePayMerchantConfiguration> = createSelector(
    getWorldpayState,
    getWorldpaygooglePayMerchantConfigurationSelector
  );

export const getWorldpaySelectedAPM: MemoizedSelector<StateWithWorldpay,
  ApmData> = createSelector(getWorldpayState, getWorldpaySelectedAPMSelector);

export const getWorldpayAPMRedirect: MemoizedSelector<StateWithWorldpay,
  APMRedirectResponse> = createSelector(getWorldpayState, getWorldpayAPMRedirectSelector);

export const getWorldpayAvailableApms: MemoizedSelector<StateWithWorldpay,
  ApmData[]> = createSelector(getWorldpayState, getWorldpayAvailableApmsSelector);

export const getWorldpayPaymentAddress: MemoizedSelector<StateWithWorldpay,
  Address> = createSelector(getWorldpayState, getWorldpayPaymentAddressSelector);

export const getWorldpayFraudSightId: MemoizedSelector<StateWithWorldpay,
  string> = createSelector(getWorldpayState, getWorldpayFraudSightIdSelector);

export const getWorldpayFraudSightEnabled: MemoizedSelector<StateWithWorldpay,
  boolean> = createSelector(getWorldpayState, getWorldpayFraudSightEnabledSelector);
