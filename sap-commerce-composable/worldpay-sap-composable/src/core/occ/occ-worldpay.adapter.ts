import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Address, ConverterService, LoggerService, normalizeHttpError, OccEndpointsService, PaymentDetails } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { WorldpayAdapter } from '../connectors/worldpay.adapter';
import { BrowserInfo, PlaceOrderResponse, ThreeDsDDCInfo } from '../interfaces';

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
    browserInfo: BrowserInfo
  ): Observable<PlaceOrderResponse> {
    const body = {
      ...paymentDetails,
      challengeWindowSize,
      dfReferenceId,
      cseToken,
      acceptedTermsAndConditions,
      deviceSession,
      browserInfo
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
      catchError((error: unknown) => throwError(() => normalizeHttpError(error, this.loggerService))),
    );
  }
}
