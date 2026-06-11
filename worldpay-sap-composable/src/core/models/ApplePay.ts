import { Order } from '@spartacus/order/root';

export interface ApplePayMerchantSession {
  epochTimestamp?: number;
  expiresAt?: number;
  merchantSessionIdentifier?: string;
  nonce?: string;
  merchantIdentifier?: string;
  domainName?: string;
  displayName?: string;
  signature?: string;
  operationalAnalyticsIdentifier?: string;
  retries?: number;
  pspId?: string;
}

export interface ApplePayLineItem {
  label?: string;
  amount?: string;
  type?: 'final' | 'pending';
}

export interface ApplePayShippingMethod extends ApplePayLineItem {
  identifier?: string;
  detail?: string;
}

export interface ApplePayPaymentContact {
  givenName?: string;
  familyName?: string;
  emailAddress?: string;
  phoneNumber?: string;
  organizationName?: string;
  addressLines?: string[];
  locality?: string;
  subLocality?: string;
  administrativeArea?: string;
  postalCode?: string;
  country?: string;
  countryCode?: string;
}

export type ApplePayShippingType =
  | 'shipping'
  | 'delivery'
  | 'storePickup'
  | 'servicePickup';

export type ApplePayMerchantCapability =
  | 'supports3DS'
  | 'supportsEMV'
  | 'supportsCredit'
  | 'supportsDebit';

export type ApplePaySupportedNetwork =
  | 'amex'
  | 'masterCard'
  | 'visa'
  | 'discover'
  | 'mada'
  | 'cartesBancaires'
  | 'electron'
  | 'elo'
  | 'jcb'
  | 'maestro'
  | 'mir'
  | 'privateLabel'
  | 'quicPay'
  | 'suica'
  | 'vPay';

export type ApplePayContactField =
  | 'postalAddress'
  | 'phone'
  | 'email'
  | 'name'
  | 'phoneticName';

export interface ApplePayPaymentRequest {
  countryCode?: string;
  currencyCode?: string;
  merchantCapabilities?: ApplePayMerchantCapability[];
  supportedNetworks?: ApplePaySupportedNetwork[];
  total?: ApplePayLineItem;
  lineItems?: ApplePayLineItem[];
  shippingMethods?: ApplePayShippingMethod[];
  requiredBillingContactFields?: ApplePayContactField[];
  requiredShippingContactFields?: ApplePayContactField[];
  shippingType?: ApplePayShippingType;
}

export interface ApplePayPaymentMethod {
  displayName?: string;
  network?: string;
  type?: string;
  paymentPass?: {
    primaryAccountIdentifier?: string;
    primaryAccountNumberSuffix?: string;
  };
}

export interface ApplePayPaymentToken {
  paymentData?: unknown;
  paymentMethod?: ApplePayPaymentMethod;
  transactionIdentifier?: string;
}

export interface ApplePayPayment {
  token?: ApplePayPaymentToken;
  billingContact?: ApplePayPaymentContact;
  shippingContact?: ApplePayPaymentContact;
  shippingMethod?: ApplePayShippingMethod;
}

export interface ApplePayPaymentAuthorizedEvent {
  payment?: ApplePayPayment;
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