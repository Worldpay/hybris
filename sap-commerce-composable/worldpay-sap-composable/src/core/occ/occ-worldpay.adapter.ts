import { Injectable } from '@angular/core';
import { Address, HttpErrorModel, PaymentDetails, tryNormalizeHttpError } from '@spartacus/core';
import { OccOrderAdapter } from '@spartacus/order/occ';
import { Order, ScheduleReplenishmentForm } from '@spartacus/order/root';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { WorldpayAdapter } from '../connectors/worldpay.adapter';
import { BrowserInfo, PlaceOrderResponse, ThreeDsDDCInfo } from '../interfaces';
import { getHeadersForUserId } from './adapters/occ-worldpay.utils';

@Injectable()
export class OccWorldpayAdapter extends OccOrderAdapter implements WorldpayAdapter {

  /**
   * Retrieves the public key for encryption purposes.
   *
   * This method constructs the URL for the `getPublicKey` endpoint and sends a GET request
   * to retrieve the public key as a plain text response.
   *
   * @returns {Observable<string>} - An observable that emits the public key as a string.
   */
  public getPublicKey(): Observable<string> {
    // eslint-disable-next-line @typescript-eslint/typedef
    const options = {
      responseType: 'text' as 'json'
    };
    const url: string = this.occEndpoints.buildUrl('getPublicKey');

    return this.http.get<string>(
      url,
      options
    );
  }

  /**
   * Sets the payment address for the specified user and cart.
   *
   * This method sends a POST request to the `setPaymentAddress` endpoint with the provided
   * address details. The address is marked as not visible in the address book.
   *
   * @param {string} userId - The unique identifier of the user.
   * @param {string} cartId - The unique identifier of the cart.
   * @param {Address} address - The address details to be set as the payment address.
   * @returns {Observable<Address>} - An observable that emits the updated payment address.
   */
  public setPaymentAddress(
    userId: string,
    cartId: string,
    address: Address
  ): Observable<Address> {

    const body: Address = {
      ...address,
      visibleInAddressBook: false,
    };

    const url: string = this.occEndpoints.buildUrl('setPaymentAddress', {
      urlParams: {
        userId,
        cartId,
      },
    });

    return this.http.post<Address>(url, body, {});
  }

  /**
   * Retrieves the JWT (JSON Web Token) required for the 3DS2 Device Data Collection (DDC) process.
   *
   * This method constructs the URL for the `getDDC3dsJwt` endpoint and sends a GET request
   * to retrieve the necessary JWT for the DDC process.
   *
   * @returns {Observable<ThreeDsDDCInfo>} - An observable that emits the 3DS2 DDC information.
   */
  public getDDC3dsJwt(): Observable<ThreeDsDDCInfo> {
    const url: string = this.occEndpoints.buildUrl('getDDC3dsJwt');
    return this.http.get<ThreeDsDDCInfo>(url);
  }

  /**
   * Sends an initial payment request for the 3DS2 payment process.
   *
   * This method constructs the request body using the provided payment details and other parameters,
   * then sends a POST request to the `initialPaymentRequest` endpoint. The response determines
   * the next steps in the payment process.
   *
   * @param {string} userId - The unique identifier of the user.
   * @param {string} cartId - The unique identifier of the cart.
   * @param {PaymentDetails} paymentDetails - The payment details, including anonymized card information.
   * @param {string} dfReferenceId - The device fingerprint reference ID obtained during DDT.
   * @param {string} challengeWindowSize - The dimensions of the challenge window (e.g., "600x400").
   * @param {string} cseToken - The client-side encryption token.
   * @param {boolean} acceptedTermsAndConditions - Indicates whether the terms and conditions are accepted.
   * @param {string} deviceSession - The optional FraudSight unique session ID.
   * @param {BrowserInfo} browserInfo - Information about the user's browser.
   * @param {ScheduleReplenishmentForm} [scheduleReplenishmentFormData] - Optional data for schedule replenishment.
   * @returns {Observable<PlaceOrderResponse>} - An observable that emits the response of the place order request.
   * @since 6.4.0
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
    interface Body extends PaymentDetails {
      challengeWindowSize: string;
      dfReferenceId: string;
      cseToken: string;
      acceptedTermsAndConditions: boolean;
      deviceSession: string;
      browserInfo: BrowserInfo;
      scheduleReplenishmentFormData?: ScheduleReplenishmentForm;
    }

    const body: Body = {
      ...paymentDetails,
      challengeWindowSize,
      dfReferenceId,
      cseToken,
      acceptedTermsAndConditions,
      deviceSession,
      browserInfo,
      scheduleReplenishmentFormData
    };

    const url: string = this.occEndpoints.buildUrl(
      'initialPaymentRequest',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );

    return this.http.post<PlaceOrderResponse>(
      url,
      body,
      {}
    );
  }

  /**
   * Get the order given userId, orderCode and guestCustomer
   * @param userId string
   * @param code string
   * @param guestCustomer boolean
   */
  public getOrder(userId: string, code: string, guestCustomer: boolean): Observable<Order> {
    const endpoint: string = guestCustomer === true ? 'getOrderForGuest' : 'getOrder';
    const url: string = this.occEndpoints.buildUrl(
      endpoint,
      {
        urlParams: {
          userId,
          code
        }
      }
    );
    return this.http.get(url).pipe(
      catchError((error: unknown): Observable<never> => throwError((): HttpErrorModel | Error => tryNormalizeHttpError(error, this.logger))),
    );
  }

  /**
   * Updates the delivery address for the specified user and cart.
   *
   * @param {string} userId - The ID of the user.
   * @param {string} cartId - The ID of the cart.
   * @param {string} addressId - The ID of the address to set as the delivery address.
   * @returns {Observable<Address>} An observable that emits the updated delivery address.
   * @since 2211.43.0
   */
  setDeliveryAddressAsBillingAddress(userId: string, cartId: string, addressId: string): Observable<Address> {
    return this.http.put<Address>(
      this.occEndpoints.buildUrl('setDeliveryAddress', {
        urlParams: {
          userId,
          cartId
        },
        queryParams: {
          addressId
        }
      }),
      {
        headers: getHeadersForUserId(userId),
      }
    );
  }
}
