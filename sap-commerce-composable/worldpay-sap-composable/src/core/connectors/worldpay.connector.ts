import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Address } from '@spartacus/core';
import {BrowserInfo, PlaceOrderResponse, ThreeDsDDCInfo } from '../interfaces';
import { Order } from '@spartacus/order/root';
import { WorldpayAdapter } from './worldpay.adapter';
import { PaymentDetails } from '@spartacus/cart/base/root';

@Injectable({
  providedIn: 'root'
})
export class WorldpayConnector {

  /**
   * Constructor
   * @param adapter - WorldpayAdapter
   */
  constructor(protected adapter: WorldpayAdapter) {
  }

  /**
   * Initial payment Request
   * @param userId - string User ID
   * @param cartId - string Cart ID
   * @param paymentDetails - PaymentDetails Payment Details
   * @param dfReferenceId - string Device Fingerprint Reference ID
   * @param challengeWindowSize - string Challenge Window Size
   * @param cseToken - string CSE Token
   * @param acceptedTermsAndConditions - Boolean Accepted Terms And Conditions
   * @param deviceSession - Device Session
   * @param browserInfo - BrowserInfo Browser information
   */
  public initialPaymentRequest(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails,
    dfReferenceId: string,
    challengeWindowSize: string,
    cseToken: string,
    acceptedTermsAndConditions: boolean,
    deviceSession: string,
    browserInfo: BrowserInfo
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
      browserInfo
    );
  }

  /**
   * Get Public Key
   * @since 6.4.0
   * @returns  Observable<string> - PublicKey string as Observable
   */
  public getPublicKey(): Observable<string> {
    return this.adapter.getPublicKey();
  }

  /**
   * Set Payment Address given userId, cartId and address
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param address - Address
   * @returns Observable<Address> - Address as Observable
   */
  public setPaymentAddress(
    userId: string,
    cartId: string,
    address: Address
  ): Observable<Address> {
    return this.adapter.setPaymentAddress(userId, cartId, address);
  }

  /**
   * Get the JWT and form url for 3DS Flex Device Data Collection (DDC)
   * @since 6.4.0
   * @returns Observable<ThreeDsDDCInfo> - ThreeDsDDCInfo as Observable
   */
  public getDDC3dsJwt(): Observable<ThreeDsDDCInfo> {
    return this.adapter.getDDC3dsJwt();
  }

  /**
   * Get the order given userId and code
   * @param userId string - User ID
   * @param orderCode string - Order Code
   * @param guestCustomer boolean - check if customer is guest
   * @returns Observable<Order> - Order as Observable
   */
  public getOrder(userId: string, orderCode: string, guestCustomer: boolean): Observable<Order> {
    return this.adapter.getOrder(userId, orderCode, guestCustomer);
  }
}
