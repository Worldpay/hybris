import { Observable } from 'rxjs';
import { Address, Order, PaymentDetails } from '@spartacus/core';
import { ApmData, ApmPaymentDetails, APMRedirectResponse, GooglePayMerchantConfiguration } from '../interfaces';

export interface ThreeDsChallengeResponse {
  acsURL: string;
  transactionId3DS: string;
  threeDSVersion: string;
  challengeJwt: string;
}

export interface ThreeDsDDCInfo {
  jwt: string;
  ddcUrl: string;
}

export interface KeyValuePair {
  key: string;
  value: string;
}

export interface ThreeDsInfo {
  merchantData: string;
  threeDSFlexData: {
    // backend returns a entry array of key value pairs
    entry: KeyValuePair[];

    jwt: string;
    challengeUrl: string;
    autoSubmitThreeDSecureFlexUrl: string;
  };
}

export interface PlaceOrderResponse {
  threeDSecureNeeded: boolean;
  threeDSecureInfo: ThreeDsInfo;
  transactionStatus: string;
  order: Order;
}

export interface ApplePayPaymentRequest {
  currencyCode: string;
  countryCode: string;
  supportedNetworks: string[];
  merchantCapabilities: string[];
  total: {
    type: string;
    label: string;
    amount: string;
  };
  requiredBillingContactFields: string[];
}

export interface ApplePayAuthorization {
  transactionStatus: string;
  order: Order;
}

export abstract class WorldpayAdapter {
  abstract useExistingPaymentDetails(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails
  ): Observable<PaymentDetails>;

  abstract getPublicKey(): Observable<string>;

  abstract setPaymentAddress(
    userId: string,
    cartId: string,
    address: Address
  ): Observable<any>;

  /**
   * get the JWT and form url for 3DS Flex Device Data Collection (DDC)
   * https://developer.worldpay.com/docs/wpg/directintegration/3ds2
   *
   */
  abstract getDDC3dsJwt(): Observable<ThreeDsDDCInfo>;

  /**
   * Perform the initial XML Payment Request.
   * Based on the response from this call we show a challenge or we perform the second payment request
   * https://developer.worldpay.com/docs/wpg/directintegration/3ds2#initial-xml-payment-request
   *
   * @param userId uid of current user
   * @param cartId code of the current cart
   * @param paymentDetails anonymized payment details
   * @param dfReferenceId reference id obtained during DDT
   * @param challengeWindowSize width x height of challenge window
   * @param cseToken CSE token
   * @param acceptedTermsAndConditions boolean must be true
   * @param deviceSession optional FraudSight unique session id
   */
  abstract initialPaymentRequest(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails,
    dfReferenceId: string,
    challengeWindowSize: string,
    cseToken: string,
    acceptedTermsAndConditions: boolean,
    deviceSession: string,
  ): Observable<PlaceOrderResponse>;

  /**
   * Get the order given userId and code
   *
   * @param userId uid of current user
   * @param code code of the newly created order
   */
  abstract getOrder(userId: string, code: string): Observable<Order>;

  /**
   * Request ApplePay Payment Request for given user and cart
   *
   * https://developer.apple.com/documentation/apple_pay_on_the_web/apple_pay_js_api/requesting_an_apple_pay_payment_session
   *
   * @param userId uid of current user
   * @param cartId code of the current cart
   */
  abstract requestApplePayPaymentRequest(
    userId: string,
    cartId: string
  ): Observable<ApplePayPaymentRequest>;

  /**
   * Validate the merchant for ApplePay
   *
   * @param userId uid of current user
   * @param cartId code of the current cart
   * @param validationURL backend validation URL
   */
  abstract validateApplePayMerchant(
    userId: string,
    cartId: string,
    validationURL: string
  ): Observable<any>;

  /**
   * Handle the order after Apple Pay has authorized the payment
   *
   * @param userId uid of current user
   * @param cartId code of the current cart
   * @param payment tokenized payment object from Apple Pay
   */
  abstract authorizeApplePayPayment(
    userId: string,
    cartId: string,
    payment: any
  ): Observable<ApplePayAuthorization>;

  /**
   * Request current merchant configuration
   *
   * @param userId current user
   * @param cartId current cart
   */
  abstract getGooglePayMerchantConfiguration(
    userId: string,
    cartId: string
  ): Observable<GooglePayMerchantConfiguration>;

  /**
   * Authorise GooglePay payment
   *
   * @param userId current user
   * @param cartId current cart
   * @param token tokenized payment details
   * @param billingAddress billing address
   * @param savePaymentMethod save payment method or not
   */
  abstract authoriseGooglePayPayment(
    userId: string,
    cartId: string,
    token: string,
    billingAddress: any,
    savePaymentMethod: boolean
  ): Observable<PlaceOrderResponse>;

  /**
   * Redirect authorise APM payment
   *
   * @param userId current user
   * @param cartId current cart
   * @param apm payment details for APM
   * @param save save payment method or not
   */
  abstract authoriseApmRedirect(
    userId: string,
    cartId: string,
    apm: ApmPaymentDetails,
    save: boolean
  ): Observable<APMRedirectResponse>;

  /**
   * Get a list of all available APM's for the given cart
   *
   * @param userId current user
   * @param cartId current cart
   */
  abstract getAvailableApms(userId: string, cartId: string): Observable<ApmData[]>;

  /**
   * Try placing the order after the user has been sent back from PSP to Spartacus
   *
   * @param userId current user
   * @param cartId current cart
   */
  abstract placeRedirectOrder(userId: string, cartId: string): Observable<Order>;

  /**
   * Check BaseSite configuration to see if FraudSight is enabled
   */
  abstract isFraudSightEnabled(): Observable<boolean>;

  /**
   * Set APM Payment Information
   *
   * @param userId
   * @param cartId
   * @param apmPaymentDetails
   */
  abstract setAPMPaymentInfo(
    userId: string,
    cartId: string,
    apmPaymentDetails: ApmPaymentDetails
  ): Observable<any>;

  /**
   * Check BaseSite configuration to see if Guaranteed Payments is enabled
   */
  abstract isGuaranteedPaymentsEnabled(): Observable<boolean>;
}
