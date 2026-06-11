export enum GooglePayCallbackTrigger {
  OFFER = 'OFFER',
  SHIPPING_ADDRESS = 'SHIPPING_ADDRESS',
  SHIPPING_OPTION = 'SHIPPING_OPTION',
  PAYMENT_AUTHORIZATION = 'PAYMENT_AUTHORIZATION'
}

export interface GooglePayTransactionInfo {
  totalPriceStatus?: string;
  totalPrice?: string;
  currencyCode?: string;
  countryCode?: string;
  transactionId?: string;
  totalPriceLabel?: string;
  checkoutOption?: string;
}

export interface GooglePayPaymentDataError {
  reason?: string;
  message?: string;
  intent?: string;
}

export interface GooglePayMerchantConfigurationParameters {
  allowedAuthMethods?: string[];
  allowedCardNetworks?: string[];
  billingAddressParameters?: {
    format?: string;
  };
  billingAddressRequired?: boolean;
}

export interface GooglePayIntermediateAddress {
  administrativeArea?: string;
  countryCode?: string;
  locality?: string;
  postalCode?: string;
}

export interface GooglePaySelectionOptionData {
  id?: string;
}

export interface GooglePayOfferInfoDetails {
  offerDetail?: {
    redemptionCode?: string;
    description?: string;
  };
}

export interface GooglePayPaymentMethod {
  type?: 'CARD' | string;
  parameters?: GooglePayMerchantConfigurationParameters;
  tokenizationSpecification?: {
    type?: 'PAYMENT_GATEWAY' | 'DIRECT';
    parameters?: {
      gateway?: string;
      gatewayMerchantId?: string;
      protocolVersion?: string;
      publicKey?: string;
    };
  };
}

export interface GooglePayPaymentAuthorizationResult {
  transactionState?: string;
  error?: GooglePayPaymentDataError;
}

export interface GooglePaySelectionOption {
  id?: string;
  label?: string;
  description?: string;
}

export interface GooglePayOfferInfo {
  offers?: GooglePayOfferInfoDetails[];
}

export interface GooglePayPaymentDataRequestUpdate {
  newOfferInfo?: GooglePayOfferInfo;
  newTransactionInfo?: GooglePayTransactionInfo;
  newShippingOptionParameters?: GooglePayShippingOptionParameters;
  error?: GooglePayPaymentDataError;
}

export interface GooglePayShippingOptionParameters {
  shippingOptions?: GooglePaySelectionOption[];
  defaultSelectedOptionId?: string;
}

export interface GooglePayMerchantInfo {
  merchantId?: string;
  merchantName?: string;
}

export interface GooglePayClientSettings {
  environment?: string;
  merchantInfo?: GooglePayMerchantInfo;
  paymentDataCallbacks?: GooglePayPaymentDataCallbacks;
}

export interface GooglePayMerchantConfiguration extends GooglePayMerchantInfo {
  allowedAuthMethods?: string[];
  allowedCardNetworks?: string[];
  cardType?: string;
  environment?: string;
  clientSettings?: GooglePayClientSettings;
  gatewayMerchantId?: string;
  merchantId?: string;
  merchantName?: string;
  transactionInfo?: GooglePayTransactionInfo;
}

export interface GooglePayIntermediatePaymentData {
  callbackTrigger?: GooglePayCallbackTrigger;
  shippingAddress?: GooglePayIntermediateAddress;
  shippingOption?: GooglePaySelectionOptionData;
}

export interface GooglePayBaseRequest {
  apiVersion?: number;
  apiVersionMinor?: number;
  allowedPaymentMethods?: GooglePayPaymentMethod[];
}

export interface GooglePayIsReadyToPayRequest extends GooglePayBaseRequest {
  existingPaymentMethodRequired?: boolean;
}

export interface GooglePayAddress {
  name?: string;
  address1?: string;
  address2?: string;
  address3?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  countryCode?: string;
  phoneNumber?: string;
}

export interface GooglePayPaymentResponse {
  apiVersion?: number;
  apiVersionMinor?: number;
  paymentMethodData?: {
    type?: string;
    description?: string;
    info?: {
      billingAddress?: GooglePayAddress;
    };
    tokenizationData?: {
      token?: string;
    };
  };
  shippingAddress?: GooglePayAddress;
  email?: string;
}

export interface GooglePayPaymentDataCallbacks {
  onPaymentAuthorized?: (payload: GooglePayPaymentResponse) => Promise<GooglePayPaymentAuthorizationResult>;
  onPaymentDataChanged?: (payload: GooglePayIntermediatePaymentData) => Promise<GooglePayPaymentDataRequestUpdate>;
}

export interface GooglePayShippingAddressParameters {
  allowedCountryCodes?: string[],
  phoneNumberRequired?: boolean
  format?: string
}

export interface GooglePayPaymentDataRequest extends GooglePayBaseRequest {
  transactionInfo?: GooglePayTransactionInfo;
  merchantInfo?: GooglePayMerchantInfo;
  emailRequired?: boolean;
  shippingAddressRequired?: boolean;
  shippingAddressParameters?: GooglePayShippingAddressParameters;
  callbackIntents?: GooglePayCallbackTrigger[];
  paymentDataCallbacks?: GooglePayPaymentDataCallbacks;
  offerInfo?: GooglePayOfferInfo;
}

export interface GooglePayIsReadyToPayResponse {
  result?: boolean;
  paymentMethodPresent?: boolean;
}

export interface GooglePayButtonOptions {
  onClick?: (event: Event) => void;
  buttonType?: string;
  buttonColor?: 'black' | 'white';
  buttonRadius?: number;
  buttonSizeMode?: 'static' | 'fill';
  allowedPaymentMethods?: GooglePayPaymentMethod[];
}

export interface GooglePaymentsClient {
  isReadyToPay?: (request: GooglePayIsReadyToPayRequest) => Promise<GooglePayIsReadyToPayResponse>;
  loadPaymentData?: (request: GooglePayPaymentDataRequest) => Promise<GooglePayPaymentResponse>;
  prefetchPaymentData?: (request: GooglePayPaymentDataRequest) => void;
  createButton?: (options: GooglePayButtonOptions) => HTMLElement;
}