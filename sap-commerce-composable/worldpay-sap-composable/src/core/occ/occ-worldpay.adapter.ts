import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Address, ConverterService, HttpErrorModel, LoggerService, OccEndpointsService, PaymentDetails, tryNormalizeHttpError } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { WorldpayAdapter } from '../connectors/worldpay.adapter';
import { BrowserInfo, PlaceOrderResponse, ThreeDsDDCInfo } from '../interfaces';
import { getHeadersForUserId } from './adapters/occ-worldpay.utils';

@Injectable()
export class OccWorldpayAdapter implements WorldpayAdapter {
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected converter: ConverterService,
    protected loggerService: LoggerService,
  ) {
  }

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

  public getDDC3dsJwt(): Observable<ThreeDsDDCInfo> {
    const url: string = this.occEndpoints.buildUrl('getDDC3dsJwt');
    return this.http.get<ThreeDsDDCInfo>(url);
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
    browserInfo: BrowserInfo
  ): Observable<PlaceOrderResponse> {
    interface Body extends PaymentDetails {
      challengeWindowSize: string;
      dfReferenceId: string;
      cseToken: string;
      acceptedTermsAndConditions: boolean;
      deviceSession: string;
      browserInfo: BrowserInfo;
    }

    const body: Body = {
      ...paymentDetails,
      challengeWindowSize,
      dfReferenceId,
      cseToken,
      acceptedTermsAndConditions,
      deviceSession,
      browserInfo
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
      catchError((error: unknown): Observable<never> => throwError((): HttpErrorModel | Error => tryNormalizeHttpError(error, this.loggerService))),
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
