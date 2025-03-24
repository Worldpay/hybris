/* eslint-disable @typescript-eslint/no-explicit-any */
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConverterService, HttpErrorModel, LoggerService, OccEndpointsService, tryNormalizeHttpError } from '@spartacus/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { WorldpayGooglepayAdapter } from '../../../connectors/worldpay-googlepay/worldpay-googlepay.adapter';
import { GooglePayMerchantConfiguration, PlaceOrderResponse } from '../../../interfaces';

@Injectable()
export class OccWorldpayGooglepayAdapter implements WorldpayGooglepayAdapter {

  /**
   * Constructor for OccWorldpayGooglepayAdapter
   * @param {HttpClient} http - The HTTP client for making requests
   * @param {OccEndpointsService} occEndpoints - Service for building OCC endpoint URLs
   * @param {ConverterService} converter - Service for converting data
   * @param {LoggerService} loggerService - Service for logging errors
   */
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected converter: ConverterService,
    protected loggerService: LoggerService
  ) {
  }

  /**
   * Get GooglePay Merchant Configuration
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @returns {Observable<GooglePayMerchantConfiguration>} An observable that emits the GooglePay merchant configuration.
   * @since 6.4.0
   */
  getGooglePayMerchantConfiguration(
    userId: string,
    cartId: string
  ): Observable<GooglePayMerchantConfiguration> {
    const url: string = this.occEndpoints.buildUrl(
      'getGooglePayMerchantConfiguration',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.get<GooglePayMerchantConfiguration>(url).pipe(
      catchError((error: unknown): Observable<never> => throwError((): HttpErrorModel | Error => tryNormalizeHttpError(error, this.loggerService))),
    );
  }

  /**
   * Authorise GooglePay Payment
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {any} token - Payment token
   * @param {any} billingAddress - Billing address
   * @param {boolean} saved - Indicates if the payment method should be saved
   * @returns {Observable<PlaceOrderResponse>} An observable that emits the response of the place order request.
   * @since 6.4.0
   */
  authoriseGooglePayPayment(
    userId: string,
    cartId: string,
    token: any,
    billingAddress: any,
    saved: boolean
  ): Observable<PlaceOrderResponse> {
    // eslint-disable-next-line @typescript-eslint/typedef
    const body = {
      token,
      billingAddress,
      saved
    };
    const url: string = this.occEndpoints.buildUrl(
      'authoriseGooglePayPayment',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.post<PlaceOrderResponse>(url, body, {}).pipe(
      catchError((error: unknown): Observable<never> => throwError((): HttpErrorModel | Error => tryNormalizeHttpError(error, this.loggerService))),
    );
  }
}
