import { KeyValue } from '@angular/common';
import { PaymentType } from '@spartacus/cart/base/root';
import { Address, CardType, CmsComponent, PaymentDetails } from '@spartacus/core';
import { Order, ScheduleReplenishmentForm } from '@spartacus/order/root';
import { Card, ICON_TYPE, Media, MediaContainer } from '@spartacus/storefront';

export interface InitialPaymentRequestPayload {
  acceptedTermsAndConditions?: boolean;
  cartId?: string;
  challengeWindowSize?: string;
  cseToken?: string;
  deviceSession?: string;
  dfReferenceId?: string;
  paymentDetails?: PaymentDetails;
  userId?: string;
}

export interface GetOrderPayload {
  code?: string;
  userId?: string;
}

export interface GooglePayMerchantConfiguration {
  allowedAuthMethods?: string[];
  allowedCardNetworks?: string[];
  cardType?: string;
  environment?: string;
  gatewayMerchantId?: string;
  merchantId?: string;
  merchantName?: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  clientSettings?: any;
}

export interface GooglePayPaymentRequest {
  apiVersion?: number;
  apiVersionMinor?: number;
  paymentMethodData?: {
    description?: string;
    info?: {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      billingAddress?: any;
    };
    tokenizationData?: {
      token?: string;
    };
    type?: string;
  };
}

export interface APMRedirectRequestBody {
  paymentMethod?: string;
  save?: boolean;
  shopperBankCode?: string;
}

export interface APMRedirectResponse {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  mappingLabels?: any;
  parameters?: {
    entry?: KeyValue<string, string>[];
  };
  postUrl?: string;
}

export interface OccApmDataConfiguration {
  bankConfigurations?: {
    bankCode?: string;
    bankName?: string;
  }[];
  code?: PaymentMethod;
  name?: string;
}

export interface OccCmsComponentWithMedia extends CmsComponent {
  media?: {
    code: string;
    mime?: string;
    url?: string;
  };
}

export interface OccApmData {
  apmConfiguration?: OccApmDataConfiguration;
  media?: {
    code: string;
    mime?: string;
    url: string;
  };
}

export interface ApmData {
  bankConfigurations?: {
    code?: string;
    name?: string;
  }[];
  code?: PaymentMethod | string;
  media?: MediaContainer;
  name?: string;
}

/*eslint no-shadow: "off"*/
export enum PaymentMethod {
  ApplePay = 'ApplePay',
  // eslint-disable-next-line @typescript-eslint/no-shadow
  Card = 'Card',
  GooglePay = 'GooglePay',
  iDeal = 'IDEAL-SSL',
  PayPal = 'PayPal',
  PayPalSSL = 'PAYPAL-SSL',
  PayPalSSLExpress = 'PAYPAL-EXPRESS',
  ACH = 'ACH_DIRECT_DEBIT-SSL',
  SepaDirectDebit = 'SEPA_DIRECT_DEBIT-SSL',
  KlarnaSSL = 'KLARNA-SSL',
}

declare module '@spartacus/order/root' {
  interface Order {
    worldpayAPMPaymentInfo?: WorldpayApmPaymentInfo;
  }
}

declare module '@spartacus/order/root' {
  interface Order {
    worldpayAPMPaymentInfo?: WorldpayApmPaymentInfo;
    replenishmentOrderCode?: string;
  }
}

declare module '@spartacus/core' {
  interface PaymentDetails {
    accountHolderName?: string;
    billingAddress?: Address;
    cardNumber?: string;
    cardType?: CardType;
    code?: string;
    cvn?: string;
    dateOfBirth?: string;
    defaultPayment?: boolean;
    expiryMonth?: string;
    expiryYear?: string;
    id?: string;
    issueNumber?: string;
    name?: string;
    save?: boolean;
    saved?: boolean;
    startMonth?: string;
    startYear?: string;
    subscriptionId?: string;
    worldpayAPMPaymentInfo?: ApmPaymentDetails;
  }
}

declare module '@spartacus/cart/base/root' {
  interface Cart {
    apmCode?: string;
    apmName?: string;
    save?: boolean;
    worldpayAPMPaymentInfo?: WorldpayApmPaymentInfo;
  }

  enum CartOutlets {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    WORLDPAY_CART_ITEM_LIST = 'y-worldpay-cart-item-list',
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    WORLDPAY_ITEM = 'y-worldpay-cart-item',
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    WORLDPAY_LIST_ITEM = 'y-worldpay-cart-item-list-row',
  }
}

declare module '@spartacus/checkout/base/root' {
  interface CheckoutState {
    worldpayAPMPaymentInfo?: WorldpayApmPaymentInfo;
    paymentType?: PaymentType;
  }
}

export interface ACHPaymentFormCommon {
  accountNumber?: string;
  routingNumber?: string;
  checkNumber?: string;
  companyName?: string;
  customIdentifier?: string;
}

export interface ACHPaymentFormRaw extends ACHPaymentFormCommon {
  accountType?: {
    code: string;
  };
}

export interface ACHPaymentForm extends ACHPaymentFormCommon {
  accountType?: string;
}

export interface AccountTypes {
  checking?: string;
  corporate?: string;
  corporateSavings?: string;
  savings?: string;
}

export interface ACHBankAccountType {
  code?: string;
  name?: string;
}

export interface ApmPaymentDetails extends PaymentDetails {
  code?: PaymentMethod | string;
  name?: string;
  shopperBankCode?: string;
  achPaymentForm?: ACHPaymentForm;
  isAPM?: boolean;
}

export interface ApmPaymentDetailsListResponse {
  payments?: ApmPaymentDetails[];
}

export interface WorldpayMediaApm extends Media {
  code?: string;
  url?: string;
}

export interface WorldpayApmPaymentInfo extends PaymentDetails {
  apmCode?: string;
  apmName?: string;
  save?: boolean;
  shopperBankCode?: string;
  achPaymentForm?: ACHPaymentForm;
  isAPM?: boolean;
  media?: WorldpayMediaApm;
}

export interface ThreeDsChallengeResponse {
  acsURL?: string;
  transactionId3DS?: string;
  threeDSVersion?: string;
  challengeJwt?: string;
}

export interface ThreeDsDDCInfo {
  jwt?: string;
  ddcUrl?: string;
}

export interface KeyValuePair {
  key?: string;
  value?: string;
}

export interface ThreeDsInfo {
  issuerUrl?: string;
  merchantData?: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  paRequest?: any;
  threeDSFlexData?: {
    // backend returns an entry array of key value pairs
    entry?: KeyValuePair[];
    jwt?: string;
    challengeUrl?: string;
    autoSubmitThreeDSecureFlexUrl?: string;
  };
}

export interface PlaceOrderResponse {
  threeDSecureNeeded?: boolean;
  threeDSecureInfo?: ThreeDsInfo;
  transactionStatus?: string;
  order?: Order;
  returnCode?: string;
  returnMessage?: string;
}

export interface ApplePayPaymentRequest {
  currencyCode?: string;
  countryCode?: string;
  supportedNetworks?: string[];
  merchantCapabilities?: string[];
  total?: {
    type?: string;
    label?: string;
    amount?: string;
  };
  requiredBillingContactFields?: string[];
}

export interface ApplePayAuthorization {
  transactionStatus?: string;
  order?: Order;
}

export interface ValidateMerchant {
  displayName?: string;
  merchantIdentifier?: string;
  initiative?: string;
  initiativeContext?: string;
}

export interface GooglepayPaymentRequest {
  allowedPaymentMethods?: {
    parameters?: {
      allowedAuthMethods?: string[];
      billingAddressRequired?: boolean;
      billingAddressParameters?: { format: string };
      allowedCardNetworks?: string[];
    };
    tokenizationSpecification?: {
      type?: string;
      parameters?: {
        gateway?: string;
        gatewayMerchantId?: string;
      };
    };
    type?: string;
  }[];
  apiVersion?: number;
  apiVersionMinor?: number;
  merchantInfo?: {
    merchantName?: string;
    merchantId?: string;
  };
  transactionInfo?: {
    currencyCode?: string;
    totalPrice?: string;
    totalPriceStatus?: string;
  };
}

export interface OCCResponse {
  statusMessage: string;
}

export enum WorldpayPlacedOrderStatus {
  ERROR = 'ERROR',
  FAILURE = 'FAILURE',
  EXPIRED = 'EXPIRED',
  REFUSED = 'REFUSED'
}

export interface WorldpayChallengeResponse {
  accepted?: boolean;
  orderCode?: string;
  guestCustomer?: boolean;
  customerID?: string;
}

export interface BrowserInfo {
  javaEnabled?: boolean;
  javascriptEnabled?: boolean;
  language?: string;
  colorDepth?: number;
  screenHeight?: number;
  screenWidth?: number;
  timeZone?: string;
  userAgent?: string;
}

export interface CSEPaymentForm {
  paymentDetails?: PaymentDetails;
  dfReferenceId?: string;
  challengeWindowSize?: string;
  cseToken?: string;
  acceptedTermsAndConditions?: boolean;
  deviceSession?: string;
  browserInfo?: BrowserInfo;
  scheduleReplenishmentFormData?: ScheduleReplenishmentForm;
}

export interface PaymentFormData {
  paymentDetails: ApmPaymentDetails;
  billingAddress?: Address;
}

// eslint-disable-next-line @typescript-eslint/typedef
export const FORM_VALIDATION_LIMITS = {
  ACCOUNT_NUMBER_MAX: 17,
  ROUTING_NUMBER_MIN: 8,
  ROUTING_NUMBER_MAX: 9,
  CHECK_NUMBER_MAX: 15,
  COMPANY_NAME_MAX: 40,
  CUSTOM_IDENTIFIER_MAX: 15
};

export interface BillingAddressFormValidation {
  isValid: boolean;
  billingAddress?: Address;
}

export interface IdealFormValue {
  bank: {
    code: string | null;
  };
}

export interface WorldpayCard extends Card {
  customImg?: WorldpayMediaApm;
}

// eslint-disable-next-line @typescript-eslint/typedef
export const WorldpayIconType = {
  AMEX: 'amex',
  APPLE_PAY: 'applepay',
  CARTE_BLEUE: 'cartebleue',
  DINERS_CLUB: 'dinersclub',
  DISCOVER: 'discover',
  GOOGLE_PAY: 'googlepay',
  JCB: 'jcb',
  MAESTRO: 'maestro',
  MASTER_CARD: 'mastercard',
  PAYPAL: 'paypal',
  SEPA: 'sepa',
  VISA: 'visa'
};

export type WorldpayIconType = typeof WorldpayIconType[keyof typeof WorldpayIconType];

export type SupportedWorldpayIconType = WorldpayIconType | ICON_TYPE;

export const WORLDPAY_ICONS: Record<keyof typeof WorldpayIconType, SupportedWorldpayIconType> = {
  AMEX: ICON_TYPE.AMEX,
  APPLE_PAY: WorldpayIconType.APPLE_PAY,
  CARTE_BLEUE: WorldpayIconType.CARTE_BLEUE,
  DINERS_CLUB: ICON_TYPE.DINERS_CLUB,
  DISCOVER: WorldpayIconType.DISCOVER,
  GOOGLE_PAY: WorldpayIconType.GOOGLE_PAY,
  JCB: WorldpayIconType.JCB,
  MAESTRO: WorldpayIconType.MAESTRO,
  MASTER_CARD: ICON_TYPE.MASTER_CARD,
  PAYPAL: WorldpayIconType.PAYPAL,
  SEPA: WorldpayIconType.SEPA,
  VISA: ICON_TYPE.VISA
};

export function getWorldpayIconSymbols(): Record<string, string> {
  return Object.entries(WORLDPAY_ICONS).reduce((acc: Record<string, string>, [, value]: [string, SupportedWorldpayIconType]): Record<string, string> => {
    const iconCode: string = String(value);
    acc[iconCode] = iconCode.toLowerCase().replace(/_/g, '');
    return acc;
  }, {} as Record<string, string>);
}