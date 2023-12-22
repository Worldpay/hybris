import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Address, ConverterService, normalizeHttpError, OccEndpointsService } from '@spartacus/core';
import { WorldpayAdapter } from '../connectors/worldpay.adapter';
import { PlaceOrderResponse, ThreeDsDDCInfo, } from '../interfaces';
import { Order } from '@spartacus/order/root';
import { PaymentDetails } from '@spartacus/cart/base/root';
import { catchError } from 'rxjs/operators';

@Injectable()
export class OccWorldpayAdapter implements WorldpayAdapter {
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected converter: ConverterService
  ) {
  }

  public getPublicKey(): Observable<string> {
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

    const body = {
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
  ): Observable<PlaceOrderResponse> {
    const body = {
      ...paymentDetails,
      challengeWindowSize,
      dfReferenceId,
      cseToken,
      acceptedTermsAndConditions,
      deviceSession,
    };

    const url = this.occEndpoints.buildUrl(
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
      catchError((error: unknown) => throwError(normalizeHttpError(error))),
    );
  }
}
