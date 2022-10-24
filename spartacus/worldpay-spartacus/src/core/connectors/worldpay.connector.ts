import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Address, Order, PaymentDetails } from '@spartacus/core';

import {
  ApplePayAuthorization,
  ApplePayPaymentRequest,
  PlaceOrderResponse,
  ThreeDsDDCInfo,
  WorldpayAdapter
} from './worldpay.adapter';
import { map } from 'rxjs/operators';
import { ApmData, ApmPaymentDetails, APMRedirectResponse, GooglePayMerchantConfiguration } from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class WorldpayConnector {
  constructor(protected adapter: WorldpayAdapter) {
  }

  public useExistingPaymentDetails(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails
  ): Observable<any> {
    return this.adapter.useExistingPaymentDetails(
      userId,
      cartId,
      paymentDetails
    );
  }

  public initialPaymentRequest(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails,
    dfReferenceId: string,
    challengeWindowSize: string,
    cseToken: string,
    acceptedTermsAndConditions: boolean,
    deviceSession: string,
  ): Observable<PlaceOrderResponse> {
    return this.adapter.initialPaymentRequest(
      userId,
      cartId,
      paymentDetails,
      dfReferenceId,
      challengeWindowSize,
      cseToken,
      acceptedTermsAndConditions,
      deviceSession,
    );
  }

  public getPublicKey(): Observable<string> {
    return this.adapter.getPublicKey();
  }

  public setPaymentAddress(
    userId: string,
    cartId: string,
    address: Address
  ): Observable<any> {
    return this.adapter.setPaymentAddress(userId, cartId, address);
  }

  public getDDC3dsJwt(): Observable<ThreeDsDDCInfo> {
    return this.adapter.getDDC3dsJwt();
  }

  public getOrder(userId: string, code: string): Observable<Order> {
    return this.adapter.getOrder(userId, code);
  }

  public requestApplePayPaymentRequest(
    userId: string,
    cartId: string
  ): Observable<ApplePayPaymentRequest> {
    return this.adapter.requestApplePayPaymentRequest(userId, cartId);
  }

  public validateApplePayMerchant(
    userId: string,
    cartId: string,
    validationURL: string
  ): Observable<any> {
    return this.adapter.validateApplePayMerchant(userId, cartId, validationURL);
  }

  public authorizeApplePayPayment(
    userId: string,
    cartId: string,
    payment: any
  ): Observable<ApplePayAuthorization> {
    return this.adapter.authorizeApplePayPayment(userId, cartId, payment);
  }

  getGooglePayMerchantConfiguration(
    userId: string,
    cartId: string
  ): Observable<GooglePayMerchantConfiguration> {
    return this.adapter.getGooglePayMerchantConfiguration(userId, cartId);
  }

  authoriseGooglePayPayment(
    userId: string,
    cartId: string,
    token: string,
    billingAddress: any,
    savePaymentMethod: boolean
  ): Observable<PlaceOrderResponse> {
    return this.adapter.authoriseGooglePayPayment(
      userId,
      cartId,
      token,
      billingAddress,
      savePaymentMethod
    );
  }

  authoriseApmRedirect(
    userId: string,
    cardId: string,
    apm: ApmPaymentDetails,
    save: boolean
  ): Observable<APMRedirectResponse> {
    return this.adapter.authoriseApmRedirect(
      userId,
      cardId,
      apm,
      save
    );
  }

  getAvailableApms(userId: string, cartId: string): Observable<ApmData[]> {
    return this.adapter.getAvailableApms(userId, cartId);
  }

  placeOrderRedirect(userId: string, cartId: string): Observable<Order> {
    return this.adapter.placeRedirectOrder(userId, cartId);
  }

  isFraudSightEnabled(): Observable<boolean> {
    return this.adapter.isFraudSightEnabled();
  }
}
