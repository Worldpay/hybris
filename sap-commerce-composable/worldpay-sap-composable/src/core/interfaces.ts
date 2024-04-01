import { Address, CmsComponent } from '@spartacus/core';
import { MediaContainer } from '@spartacus/storefront';
import { KeyValue } from '@angular/common';
import { Order } from '@spartacus/order/root';
import { CardType, PaymentDetails } from '@spartacus/cart/base/root';

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
    entry?: KeyValue<string, string> [];
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
  } [];
  code?: PaymentMethod | string;
  media?: MediaContainer;
  name?: string;
}

/*eslint no-shadow: "off"*/
export enum PaymentMethod {
  ApplePay = 'ApplePay',
  Card = 'Card',
  GooglePay = 'GooglePay',
  iDeal = 'IDEAL-SSL',
  PayPal = 'PayPal',
}

declare module '@spartacus/order/root' {
  interface Order {
    worldpayAPMPaymentInfo?: WorldpayApmPaymentInfo;
  }
}

declare module '@spartacus/cart/base/root' {
  interface Cart {
    apmCode?: string;
    apmName?: string;
    worldpayAPMPaymentInfo?: WorldpayApmPaymentInfo;
  }

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

declare module '@spartacus/order/root' {
  interface Order {
    worldpayAPMPaymentInfo?: WorldpayApmPaymentInfo;
    replenishmentOrderCode?: string;
  }
}

declare module '@spartacus/cart/base/root' {
  interface Cart {
    apmCode?: string;
    apmName?: string;
    worldpayAPMPaymentInfo?: WorldpayApmPaymentInfo;
  }

  interface PaymentDetails {
    dateOfBirth?: string;
  }

  enum CartOutlets {
    WORLDPAY_CART_ITEM_LIST = 'y-worldpay-cart-item-list',
    WORLDPAY_ITEM = 'y-worldpay-cart-item',
    WORLDPAY_LIST_ITEM = 'y-worldpay-cart-item-list-row',
  }
}

declare module '@spartacus/checkout/base/root' {
  interface CheckoutState {
    worldpayAPMPaymentInfo?: WorldpayApmPaymentInfo;
  }
}

export interface ApmPaymentDetails extends PaymentDetails {
  code?: PaymentMethod | string;
  name?: string;
  shopperBankCode?: string;
}

export interface WorldpayApmPaymentInfo extends PaymentDetails {
  apmCode?: string;
  apmName?: string;
  shopperBankCode?: string;
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
}
