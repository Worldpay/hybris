import { Address, PaymentDetails } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Observable, of } from 'rxjs';
import { BrowserInfo, PlaceOrderResponse, ThreeDsDDCInfo, WorldpayConnector } from '../../core';
import { generateOneAddress } from '../fake-data';

export class MockWorldpayConnector implements Partial<WorldpayConnector> {
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
    return of({});
  }

  /**
   * Get Public Key
   * @since 6.4.0
   * @returns  Observable<string> - PublicKey string as Observable
   */
  public getPublicKey(): Observable<string> {
    return of('publicKey');
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
    return of(generateOneAddress());
  }

  /**
   * Get the JWT and form url for 3DS Flex Device Data Collection (DDC)
   * @since 6.4.0
   * @returns Observable<ThreeDsDDCInfo> - ThreeDsDDCInfo as Observable
   */
  public getDDC3dsJwt(): Observable<ThreeDsDDCInfo> {
    return of({
      jwt: 'jwt',
      formUrl: 'formUrl'
    });
  }

  /**
   * Get the order given userId and code
   * @param userId string - User ID
   * @param orderCode string - Order Code
   * @param guestCustomer boolean - check if customer is guest
   * @returns Observable<Order> - Order as Observable
   */
  public getOrder(userId: string, orderCode: string, guestCustomer: boolean): Observable<Order> {
    return of({} as Order);
  }

  /**
   * Requests the billing address for the specified user and cart.
   *
   * @param {string} userId - The ID of the user.
   * @param {string} cartId - The ID of the cart.
   * @returns {Observable<Address>} An observable that emits the billing address.
   */
  public requestBillingAddress(userId: string, cartId: string): Observable<Address> {
    return of(generateOneAddress());
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
    return of(generateOneAddress());
  }

}