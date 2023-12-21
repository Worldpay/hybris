/* eslint-disable @typescript-eslint/no-explicit-any */
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ConverterService, normalizeHttpError, OccEndpointsService } from '@spartacus/core';
import { GooglePayMerchantConfiguration, PlaceOrderResponse } from '../../../interfaces';
import { WorldpayGooglepayAdapter } from '../../../connectors/worldpay-googlepay/worldpay-googlepay.adapter';
import { catchError } from 'rxjs/operators';

@Injectable()
export class OccWorldpayGooglepayAdapter implements WorldpayGooglepayAdapter {

  /**
   * Constructor
   * @param http
   * @param occEndpoints
   * @param converter
   */
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected converter: ConverterService
  ) {
  }

  /**
   * Get GooglePay Merchant Configuration
   * @since 6.4.0
   * @param userId - User ID
   * @param cartId
   */
  getGooglePayMerchantConfiguration(
    userId: string,
    cartId: string
  ): Observable<GooglePayMerchantConfiguration> {
    const url = this.occEndpoints.buildUrl(
      'getGooglePayMerchantConfiguration',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.get<GooglePayMerchantConfiguration>(url).pipe(
      catchError((error: unknown) => throwError(normalizeHttpError(error))),
    );
  }

  /**
   * Authorise Googlepay Payment
   * @since 6.4.0
   * @param userId - User ID
   * @param cartId
   * @param token
   * @param billingAddress
   * @param saved
   */
  authoriseGooglePayPayment(
    userId: string,
    cartId: string,
    token: any,
    billingAddress: any,
    saved: boolean
  ): Observable<PlaceOrderResponse> {
    const body = {
      token,
      billingAddress,
      saved
    };
    const url = this.occEndpoints.buildUrl(
      'authoriseGooglePayPayment',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.post<PlaceOrderResponse>(url, body, {}).pipe(
      catchError((error: unknown) => throwError(normalizeHttpError(error))),
    );
  }
}
