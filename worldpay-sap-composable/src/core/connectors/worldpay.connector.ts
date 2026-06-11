import { Injectable } from '@angular/core';
import { Address, PaymentDetails } from '@spartacus/core';
import { OrderConnector } from '@spartacus/order/core';
import { Order, ScheduleReplenishmentForm } from '@spartacus/order/root';
import { Observable } from 'rxjs';
import { BrowserInfo, PlaceOrderResponse, ThreeDsDDCInfo } from '../interfaces';
import { WorldpayAdapter } from './worldpay.adapter';

/* eslint-disable @angular-eslint/prefer-inject */
@Injectable({
  providedIn: 'root'
})
export class WorldpayConnector extends OrderConnector {

  /**
   * Constructor
   * @param adapter - WorldpayAdapter
   */
  constructor(protected override adapter: WorldpayAdapter) {
    super(adapter);
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
   * @param {ScheduleReplenishmentForm} [scheduleReplenishmentFormData] - Optional data for schedule replenishment.
   * @since 6.4.0
   * @returns Observable<PlaceOrderResponse> - PlaceOrderResponse as Observable
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
    browserInfo: BrowserInfo,
    scheduleReplenishmentFormData?: ScheduleReplenishmentForm
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
      browserInfo,
      scheduleReplenishmentFormData
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

  /**
   * Updates the delivery address for the specified user and cart.
   *
   * @param {string} userId - The ID of the user.
   * @param {string} cartId - The ID of the cart.
   * @param {string} addressId - The ID of the address to update.
   * @returns {Observable<Address>} An observable that emits the updated delivery address.
   */
  public setDeliveryAddressAsBillingAddress(userId: string, cartId: string, addressId: string): Observable<Address> {
    return this.adapter.setDeliveryAddressAsBillingAddress(userId, cartId, addressId);
  }
}
