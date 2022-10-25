import { Address, CardType, CmsComponent, PaymentDetails } from '@spartacus/core';
import { MediaContainer } from '@spartacus/storefront';
import { KeyValue } from '@angular/common';

export interface InitialPaymentRequestPayload {
  userId: string;
  cartId: string;
  paymentDetails: PaymentDetails;
  dfReferenceId: string;
  challengeWindowSize: string;
  cseToken: string;
  acceptedTermsAndConditions: boolean;
  deviceSession?: string;
}

export interface GetOrderPayload {
  userId: string;
  code: string;
}

export interface GooglePayMerchantConfiguration {
  allowedAuthMethods: string[];
  allowedCardNetworks: string[];
  cardType: string;
  environment: string;
  gatewayMerchantId: string;
  merchantName: string;
  merchantId: string;
}

export interface GooglePayPaymentRequest {
  apiVersion: number;
  apiVersionMinor: number;
  paymentMethodData: {
    type?: string;
    description?: string;
    info: {
      billingAddress: any;
    };
    tokenizationData: {
      token: string;
    };
  };
}

export interface APMRedirectRequestBody {
  paymentMethod: string;
  save: boolean;
  shopperBankCode?: string;
}

export interface APMRedirectResponse {
  postUrl: string;
  parameters: {
    entry: KeyValue<string, string> [];
  };
  mappingLabels: any;
}

export interface OccApmDataConfiguration {
  code: PaymentMethod;
  name: string;

  bankConfigurations?: {
    bankCode: string;
    bankName: string;
  }[];
}

export interface OccCmsComponentWithMedia extends CmsComponent {
  media?: {
    code: string;
    mime?: string;
    url: string;
  };
}

export interface OccApmData {
  media?: {
    code: string;
    mime?: string;
    url: string;
  };
  apmConfiguration: OccApmDataConfiguration;
}

export interface ApmData {
  code?: PaymentMethod;
  name?: string;
  media?: MediaContainer;

  bankConfigurations?: {
    code: string;
    name: string;
  } [];
}

/*eslint no-shadow: "off"*/
export enum PaymentMethod {
  PayPal = 'PayPal',
  Card = 'Card',
  GooglePay = 'GooglePay',
  ApplePay = 'ApplePay',
  iDeal = 'IDEAL-SSL',
}

declare module '@spartacus/core' {

  interface PaymentDetails {
    accountHolderName?: string;
    billingAddress?: Address;
    cardNumber?: string;
    cardType?: CardType;
    cvn?: string;
    defaultPayment?: boolean;
    expiryMonth?: string;
    expiryYear?: string;
    id?: string;
    issueNumber?: string;
    saved?: boolean;
    startMonth?: string;
    startYear?: string;
    subscriptionId?: string;
    dateOfBirth?: string;
  }
}

export interface ApmPaymentDetails {
  code: PaymentMethod;

  // add APM specific optional fields
  shopperBankCode?: string;
}
