import { Action } from '@ngrx/store';
import { Address, Order, PaymentDetails } from '@spartacus/core';
import { ApplePayAuthorization, ApplePayPaymentRequest, PlaceOrderResponse, ThreeDsDDCInfo, ThreeDsInfo } from '../connectors/worldpay.adapter';
import { SafeResourceUrl } from '@angular/platform-browser';
import { ApmData, ApmPaymentDetails, APMRedirectResponse, GetOrderPayload, GooglePayMerchantConfiguration, InitialPaymentRequestPayload } from '../interfaces';

export const CREATE_WORLDPAY_PAYMENT_DETAILS =
  '[Checkout] Create Worldpay Payment Details';
export const CREATE_WORLDPAY_PAYMENT_DETAILS_SUCCESS =
  '[Checkout] Create Worldpay Payment Details Success';
export const CREATE_WORLDPAY_PAYMENT_DETAILS_FAIL =
  '[Checkout] Create Worldpay Payment Details Failure';

export const GET_WORLDPAY_PUBLIC_KEY = '[Checkout] Get Worldpay Public Key';
export const GET_WORLDPAY_PUBLIC_KEY_FAIL =
  '[Checkout] Get Worldpay Public Key Fail';
export const GET_WORLDPAY_PUBLIC_KEY_SUCCESS =
  '[Checkout] Get Worldpay Public Key Success';

export const USE_EXISTING_WORLDPAY_PAYMENT_DETAILS =
  '[Checkout] Use Exiting Worldpay Payment Details';
export const USE_EXISTING_WORLDPAY_PAYMENT_DETAILS_FAIL =
  '[Checkout] Use Exiting Worldpay Payment Details Fail';
export const USE_EXISTING_WORLDPAY_PAYMENT_DETAILS_SUCCESS =
  '[Checkout] Use Exiting Worldpay Payment Details Success';

export const SET_PAYMENT_ADDRESS = '[Checkout] Set payment address';
export const SET_PAYMENT_ADDRESS_FAIL = '[Checkout] Set payment address fail';
export const SET_PAYMENT_ADDRESS_SUCCESS =
  '[Checkout] Set payment address success';

export const SET_WORLDPAY_DDC_IFRAME_URL =
  '[Checkout] Set Worldpay DDC Iframe URL';
export const SET_WORLDPAY_CHALLENGE_IFRAME_URL =
  '[Checkout] Set Worldpay Challenge Iframe URL';

export const GET_WORLDPAY_DDC_JWT = '[Checkout] Create Worldpay DDC JWT';
export const GET_WORLDPAY_DDC_JWT_FAIL =
  '[Checkout] Create Worldpay DDC JWT Fail';
export const GET_WORLDPAY_DDC_JWT_SUCCESS =
  '[Checkout] Create Worldpay DDC JWT Success';

export const INITIAL_PAYMENT_REQUEST = '[Checkout] Initial payment request';
export const INITIAL_PAYMENT_REQUEST_FAIL =
  '[Checkout] Initial payment request failed';
export const INITIAL_PAYMENT_REQUEST_SUCCESS =
  '[Checkout] Initial payment request success';
export const INITIAL_PAYMENT_REQUEST_CHALLENGE_REQUIRED =
  '[Checkout] Initial payment request challenge required';

export const CLEAR_PAYMENT_DETAILS = '[Checkout] Clear Payment Details';

export const CHALLENGE_ACCEPTED = '[Checkout] Challenge Accepted';
export const CHALLENGE_ACCEPTED_FAIL = '[Checkout] Challenge Accepted Fail';
export const CHALLENGE_ACCEPTED_SUCCESS =
  '[Checkout] Challenge Accepted Success';

export const REQUEST_APPLE_PAY_PAYMENT_REQUEST =
  '[ApplePay] Request Payment Request';
export const REQUEST_APPLE_PAY_PAYMENT_REQUEST_FAIL =
  '[ApplePay] Request Payment Request Fail';
export const REQUEST_APPLE_PAY_PAYMENT_REQUEST_SUCCESS =
  '[ApplePay] Request Payment Request Success';

export const START_APPLE_PAY_SESSION = '[ApplePay] Start session';

export const VALIDATE_APPLE_PAY_MERCHANT = '[ApplePay] Validate Merchant';
export const VALIDATE_APPLE_PAY_MERCHANT_FAIL =
  '[ApplePay] Validate Merchant Fail';
export const VALIDATE_APPLE_PAY_MERCHANT_SUCCESS =
  '[ApplePay] Validate Merchant Success';

export const AUTHORISE_APPLE_PAY_PAYMENT = '[ApplePay] Authorise payment';
export const AUTHORISE_APPLE_PAY_PAYMENT_FAIL =
  '[ApplePay] Authorise payment Fail';
export const AUTHORISE_APPLE_PAY_PAYMENT_SUCCESS =
  '[ApplePay] Authorise payment Success';

export const GET_CONFIG_GOOGLE_PAY = '[GooglePay] Get Configuration';
export const GET_CONFIG_GOOGLE_PAY_FAIL = '[GooglePay] Get Configuration Fail';
export const GET_CONFIG_GOOGLE_PAY_SUCCESS =
  '[GooglePay] Get Configuration Success';

export const AUTHORISE_GOOGLE_PAY_PAYMENT = '[GooglePay] Authorise payment';
export const AUTHORISE_GOOGLE_PAY_PAYMENT_FAIL =
  '[GooglePay] Authorise payment Fail';
export const AUTHORISE_GOOGLE_PAY_PAYMENT_SUCCESS =
  '[GooglePay] Authorise payment Success';

export const SET_SELECTED_APM = '[Worldpay] Set Selected APM';

export const GET_APM_REDIRECT_URL = '[Worldpay] Get AMP Redirect URL';
export const GET_APM_REDIRECT_URL_FAIL = '[Worldpay] Get AMP Redirect URL Fail';
export const GET_APM_REDIRECT_URL_SUCCESS =
  '[Worldpay] Get AMP Redirect URL Success';

export const GET_AVAILABLE_APMS = '[Worldpay] Get Available APMs';
export const GET_AVAILABLE_APMS_FAIL = '[Worldpay] Get Available APMs Fail';
export const GET_AVAILABLE_APMS_SUCCESS = '[Worldpay] Get Available APMs Success';

export const PLACE_ORDER_REDIRECT = '[Worldpay] Place Order Redirect';
export const PLACE_ORDER_REDIRECT_FAIL = '[Worldpay] Place Order Redirect Fail';
export const PLACE_ORDER_REDIRECT_SUCCESS = '[Worldpay] Place Order Redirect Success';

export const START_LOADER = '[Worldpay] Start Loader';

export const SET_FRAUD_SIGHT_ID = '[Worldpay] Set FraudSight ID';

export const IS_FRAUD_SIGHT_ENABLED = '[Worldpay] Is FraudSight Enabled';
export const IS_FRAUD_SIGHT_ENABLED_SUCCESS = '[Worldpay] Is FraudSight Enabled Success';

export const SET_APM_PAYMENT_INFO = '[Checkout] Set APM payment info';
export const SET_APM_PAYMENT_INFO_FAIL = '[Checkout] Set APM payment info fail';
export const SET_APM_PAYMENT_INFO_SUCCESS = '[Checkout] Set APM payment info success';

export class CreateWorldpayPaymentDetails implements Action {
  readonly type = CREATE_WORLDPAY_PAYMENT_DETAILS;

  constructor(
    public payload: {
      userId: string;
      cartId: string;
      paymentDetails: PaymentDetails;
      cseToken: string;
    }
  ) {
  }
}

export class CreateWorldpayPaymentDetailsSuccess implements Action {
  readonly type = CREATE_WORLDPAY_PAYMENT_DETAILS_SUCCESS;

  constructor(public payload: PaymentDetails) {
  }
}

export class CreateWorldpayPaymentDetailsFail implements Action {
  readonly type = CREATE_WORLDPAY_PAYMENT_DETAILS_FAIL;

  constructor() {
  }
}

export class UseExistingWorldpayPaymentDetails implements Action {
  readonly type = USE_EXISTING_WORLDPAY_PAYMENT_DETAILS;

  constructor(
    public payload: {
      userId: string;
      cartId: string;
      paymentDetails: PaymentDetails;
    }
  ) {
  }
}

export class UseExistingWorldpayPaymentDetailsFail implements Action {
  readonly type = USE_EXISTING_WORLDPAY_PAYMENT_DETAILS_FAIL;

  constructor(public payload: any) {
  }
}

export class UseExistingWorldpayPaymentDetailsSuccess implements Action {
  readonly type = USE_EXISTING_WORLDPAY_PAYMENT_DETAILS_SUCCESS;

  constructor(public payload: PaymentDetails) {
  }
}

export class GetWorldpayPublicKey implements Action {
  readonly type = GET_WORLDPAY_PUBLIC_KEY;

  constructor() {
  }
}

export class GetWorldpayPublicKeyFail implements Action {
  readonly type = GET_WORLDPAY_PUBLIC_KEY_FAIL;

  constructor(public payload: any) {
  }
}

export class GetWorldpayPublicKeySuccess implements Action {
  readonly type = GET_WORLDPAY_PUBLIC_KEY_SUCCESS;

  constructor(public payload: string) {
  }
}

export class SetPaymentAddress implements Action {
  readonly type = SET_PAYMENT_ADDRESS;

  constructor(
    public payload: {
      userId: string;
      cartId: string;
      address: Address;
    }
  ) {
  }
}

export class SetPaymentAddressFail implements Action {
  readonly type = SET_PAYMENT_ADDRESS_FAIL;

  constructor(public payload: any) {
  }
}

export class SetPaymentAddressSuccess implements Action {
  readonly type = SET_PAYMENT_ADDRESS_SUCCESS;

  constructor(public payload: Address) {
  }
}

export class GetWorldpayDDCJwt implements Action {
  readonly type = GET_WORLDPAY_DDC_JWT;

  constructor() {
  }
}

export class GetWorldpayDDCJwtFail implements Action {
  readonly type = GET_WORLDPAY_DDC_JWT_FAIL;

  constructor(public payload: string) {
  }
}

export class GetWorldpayDDCJwtSuccess implements Action {
  readonly type = GET_WORLDPAY_DDC_JWT_SUCCESS;

  constructor(public payload: ThreeDsDDCInfo) {
  }
}

export class InitialPaymentRequest implements Action {
  readonly type = INITIAL_PAYMENT_REQUEST;

  constructor(public payload: InitialPaymentRequestPayload) {
  }
}

export class InitialPaymentRequestFail implements Action {
  readonly type = INITIAL_PAYMENT_REQUEST_FAIL;

  constructor(public payload: any) {
  }
}

export class InitialPaymentRequestChallengeRequired implements Action {
  readonly type = INITIAL_PAYMENT_REQUEST_CHALLENGE_REQUIRED;

  constructor(public payload: ThreeDsInfo) {
  }
}

export class SetWorldpayDDCIframeUrl implements Action {
  readonly type = SET_WORLDPAY_DDC_IFRAME_URL;

  constructor(public payload: SafeResourceUrl) {
  }
}

export class SetWorldpayChallengeIframeUrl implements Action {
  readonly type = SET_WORLDPAY_CHALLENGE_IFRAME_URL;

  constructor(public payload: SafeResourceUrl) {
  }
}

export class ClearPaymentDetails implements Action {
  readonly type = CLEAR_PAYMENT_DETAILS;

  constructor() {
  }
}

export class ChallengeAccepted implements Action {
  readonly type = CHALLENGE_ACCEPTED;

  constructor(public payload: GetOrderPayload) {
  }
}

export class ChallengeAcceptedFail implements Action {
  readonly type = CHALLENGE_ACCEPTED_FAIL;

  constructor(public payload: string) {
  }
}

export class ChallengeAcceptedSuccess implements Action {
  readonly type = CHALLENGE_ACCEPTED_FAIL;

  constructor(public payload: Order) {
  }
}

export class RequestApplePayPaymentRequest implements Action {
  readonly type = REQUEST_APPLE_PAY_PAYMENT_REQUEST;

  constructor(public payload: { userId: string; cartId: string }) {
  }
}

export class RequestApplePayPaymentRequestFail implements Action {
  readonly type = REQUEST_APPLE_PAY_PAYMENT_REQUEST_FAIL;

  constructor(public payload: string) {
  }
}

export class RequestApplePayPaymentRequestSuccess implements Action {
  readonly type = REQUEST_APPLE_PAY_PAYMENT_REQUEST_SUCCESS;

  constructor(public payload: ApplePayPaymentRequest) {
  }
}

export class StartApplePaySession implements Action {
  readonly type = START_APPLE_PAY_SESSION;

  constructor() {
  }
}

export class ValidateApplePayMerchant implements Action {
  readonly type = VALIDATE_APPLE_PAY_MERCHANT;

  constructor(
    public payload: { userId: string; cartId: string; validationURL: string }
  ) {
  }
}

export class ValidateApplePayMerchantFail implements Action {
  readonly type = VALIDATE_APPLE_PAY_MERCHANT_FAIL;

  constructor(public payload: string) {
  }
}

export class ValidateApplePayMerchantSuccess implements Action {
  readonly type = VALIDATE_APPLE_PAY_MERCHANT_SUCCESS;

  constructor(public payload: any) {
  }
}

export class AuthoriseApplePayPayment implements Action {
  readonly type = AUTHORISE_APPLE_PAY_PAYMENT;

  constructor(
    public payload: { userId: string; cartId: string; payment: any }
  ) {
  }
}

export class AuthoriseApplePayPaymentFail implements Action {
  readonly type = AUTHORISE_APPLE_PAY_PAYMENT_FAIL;

  constructor(public payload: string) {
  }
}

export class AuthoriseApplePayPaymentSuccess implements Action {
  readonly type = AUTHORISE_APPLE_PAY_PAYMENT_SUCCESS;

  constructor(public payload: ApplePayAuthorization) {
  }
}

export class GetGooglePayMerchantConfiguration implements Action {
  readonly type = GET_CONFIG_GOOGLE_PAY;

  constructor(public payload: { userId: string; cartId: string }) {
  }
}

export class GetGooglePayMerchantConfigurationFail implements Action {
  readonly type = GET_CONFIG_GOOGLE_PAY_FAIL;

  constructor(public payload: string) {
  }
}

export class GetGooglePayMerchantConfigurationSuccess implements Action {
  readonly type = GET_CONFIG_GOOGLE_PAY_SUCCESS;

  constructor(public payload: GooglePayMerchantConfiguration) {
  }
}

export class AuthoriseGooglePayPayment implements Action {
  readonly type = AUTHORISE_GOOGLE_PAY_PAYMENT;

  constructor(
    public payload: {
      userId: string;
      cartId: string;
      token: any;
      billingAddress: any;
      savePaymentMethod: boolean;
    }
  ) {
  }
}

export class AuthoriseGooglePayPaymentFail implements Action {
  readonly type = AUTHORISE_GOOGLE_PAY_PAYMENT_FAIL;

  constructor(public payload: string) {
  }
}

export class AuthoriseGooglePayPaymentSuccess implements Action {
  readonly type = AUTHORISE_GOOGLE_PAY_PAYMENT_SUCCESS;

  constructor(public payload: PlaceOrderResponse) {
  }
}

export class SetSelectedAPM implements Action {
  readonly type = SET_SELECTED_APM;

  constructor(public payload: ApmData) {
  }
}

export class GetAPMRedirectUrl implements Action {
  readonly type = GET_APM_REDIRECT_URL;

  constructor(
    public payload: {
      userId: string;
      cartId: string;
      apm: ApmPaymentDetails;
      save: boolean;
    }
  ) {
  }
}

export class GetAPMRedirectUrlFail implements Action {
  readonly type = GET_APM_REDIRECT_URL_FAIL;

  constructor(public payload: string) {
  }
}

export class GetAPMRedirectUrlSuccess implements Action {
  readonly type = GET_APM_REDIRECT_URL_SUCCESS;

  constructor(public payload: APMRedirectResponse) {
  }
}

export class GetAvailableApms implements Action {
  readonly type = GET_AVAILABLE_APMS;

  constructor(
    public payload: {
      userId: string;
      cartId: string;
    }
  ) {
  }
}

export class GetAvailableApmsFail implements Action {
  readonly type = GET_AVAILABLE_APMS_FAIL;

  constructor(public payload: string) {
  }
}

export class GetAvailableApmsSuccess implements Action {
  readonly type = GET_AVAILABLE_APMS_SUCCESS;

  constructor(public payload: ApmData[]) {
  }
}

export class PlaceOrderRedirect implements Action {
  readonly type = PLACE_ORDER_REDIRECT;

  constructor(
    public payload: {
      userId: string;
      cartId: string;
    }
  ) {
  }
}

export class PlaceOrderRedirectFail implements Action {
  readonly type = PLACE_ORDER_REDIRECT_FAIL;

  constructor(public payload: string) {
  }
}

export class PlaceOrderRedirectSuccess implements Action {
  readonly type = PLACE_ORDER_REDIRECT_SUCCESS;

  constructor(public payload: Order) {
  }
}

export class StartLoader implements Action {
  readonly type = START_LOADER;

  constructor() {
  }
}

export class SetFraudSightId implements Action {
  readonly type = SET_FRAUD_SIGHT_ID;

  constructor(public payload: string) {
  }
}

export class GetFraudSightEnabled implements Action {
  readonly type = IS_FRAUD_SIGHT_ENABLED;

  constructor() {
  }
}

export class GetFraudSightEnabledSuccess implements Action {
  readonly type = IS_FRAUD_SIGHT_ENABLED_SUCCESS;

  constructor(public payload: boolean) {
  }
}

export class SetAPMPaymentInfo implements Action {
  readonly type = SET_APM_PAYMENT_INFO;

  constructor(
    public payload: {
      userId: string;
      cartId: string;
      apmPaymentDetails: ApmPaymentDetails;
    }
  ) {
  }
}

export class SetAPMPaymentInfoFail implements Action {
  readonly type = SET_APM_PAYMENT_INFO_FAIL;

  constructor(public payload: any) {
  }
}

export class SetAPMPaymentInfoSuccess implements Action {
  readonly type = SET_APM_PAYMENT_INFO_SUCCESS;

  constructor(public apmPaymentInfo: any) {
  }
}

export type WorldpayAction =
  | CreateWorldpayPaymentDetails
  | CreateWorldpayPaymentDetailsSuccess
  | CreateWorldpayPaymentDetailsFail
  | GetWorldpayPublicKey
  | GetWorldpayPublicKeyFail
  | GetWorldpayPublicKeySuccess
  | UseExistingWorldpayPaymentDetails
  | UseExistingWorldpayPaymentDetailsFail
  | UseExistingWorldpayPaymentDetailsSuccess
  | SetPaymentAddress
  | SetPaymentAddressFail
  | SetPaymentAddressSuccess
  | GetWorldpayDDCJwt
  | GetWorldpayDDCJwtFail
  | GetWorldpayDDCJwtSuccess
  | InitialPaymentRequest
  | InitialPaymentRequestFail
  | InitialPaymentRequestChallengeRequired
  | SetWorldpayDDCIframeUrl
  | ChallengeAccepted
  | ChallengeAcceptedFail
  | SetWorldpayChallengeIframeUrl
  | ChallengeAcceptedSuccess
  | ClearPaymentDetails
  | RequestApplePayPaymentRequest
  | RequestApplePayPaymentRequestFail
  | RequestApplePayPaymentRequestSuccess
  | StartApplePaySession
  | ValidateApplePayMerchant
  | ValidateApplePayMerchantFail
  | ValidateApplePayMerchantSuccess
  | AuthoriseApplePayPayment
  | AuthoriseApplePayPaymentFail
  | AuthoriseApplePayPaymentSuccess
  | GetGooglePayMerchantConfiguration
  | GetGooglePayMerchantConfigurationFail
  | GetGooglePayMerchantConfigurationSuccess
  | AuthoriseGooglePayPayment
  | AuthoriseGooglePayPaymentFail
  | AuthoriseGooglePayPaymentSuccess
  | SetSelectedAPM
  | GetAPMRedirectUrl
  | GetAPMRedirectUrlFail
  | GetAPMRedirectUrlSuccess
  | GetAvailableApms
  | GetAvailableApmsFail
  | GetAvailableApmsSuccess
  | StartLoader
  | SetFraudSightId
  | GetFraudSightEnabled
  | GetFraudSightEnabledSuccess
  | SetAPMPaymentInfo
  | SetAPMPaymentInfoFail
  | SetAPMPaymentInfoSuccess;
