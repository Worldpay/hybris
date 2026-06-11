/* eslint-disable @typescript-eslint/no-explicit-any */
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { HttpErrorModel, LoggerService, OccEndpointsService, tryNormalizeHttpError } from '@spartacus/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { WorldpayApplepayAdapter } from '../../../connectors';
import { ApplePayAuthorization, ApplePayMerchantSession, ApplePayPayment, ApplePayPaymentRequest } from '../../../models';

@Injectable()
export class OccWorldpayApplepayAdapter implements WorldpayApplepayAdapter {
  protected http: HttpClient = inject(HttpClient);
  protected occEndpoints: OccEndpointsService = inject(OccEndpointsService);
  protected loggerService: LoggerService = inject(LoggerService);

  /**
   * Request Apple Pay Payment Request
   *
   * This method builds the URL for requesting an Apple Pay payment request and makes an HTTP GET request to that URL.
   * It catches any errors that occur during the request and normalizes them using the logger service.
   *
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @returns {Observable<ApplePayPaymentRequest>} - Observable of Apple Pay Payment Request
   * @since 4.3.6
   */
  public requestApplePayPaymentRequest(
    userId: string,
    cartId: string
  ): Observable<ApplePayPaymentRequest> {
    const url: string = this.occEndpoints.buildUrl(
      'requestApplePayPaymentRequest',
      {
        urlParams: {
          userId,
          cartId,
        }
      });
    return this.http.get<ApplePayPaymentRequest>(url).pipe(
      catchError((error: unknown): Observable<never> => throwError((): HttpErrorModel | Error => tryNormalizeHttpError(error, this.loggerService))),
    );
  }

  /**
   * Validate Apple Pay Merchant
   *
   * This method builds the URL for validating an Apple Pay merchant and makes an HTTP POST request to that URL with the validation URL in the body.
   * It catches any errors that occur during the request and normalizes them using the logger service.
   *
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {string} validationURL - Validation URL
   * @returns {Observable<ApplePayMerchantSession>} - Observable of Merchant Session
   * @since 4.3.6
   */
  validateApplePayMerchant(
    userId: string,
    cartId: string,
    validationURL: string
  ): Observable<ApplePayMerchantSession> {
    const body: { validationURL: string } = {
      validationURL
    };
    const url: string = this.occEndpoints.buildUrl(
      'validateApplePayMerchant',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );

    return this.http.post<ApplePayMerchantSession>(url, body, {}).pipe(
      catchError((error: unknown): Observable<never> => throwError((): HttpErrorModel | Error => tryNormalizeHttpError(error, this.loggerService))),
    );
  }

  /**
   * Authorize Apple Pay Payment
   *
   * This method builds the URL for authorizing an Apple Pay payment and makes an HTTP POST request to that URL with the request payload in the body.
   * It catches any errors that occur during the request and normalizes them using the logger service.
   *
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {ApplePayPayment} request - Authorization request payload
   * @returns {Observable<ApplePayAuthorization>} - Observable of Apple Pay Authorization response
   * @since 4.3.6
   */
  authorizeApplePayPayment(
    userId: string,
    cartId: string,
    request: ApplePayPayment
  ): Observable<ApplePayAuthorization> {
    const body: any = {
      ...request
    };

    const url: string = this.occEndpoints.buildUrl(
      'authorizeApplePayPayment',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );

    return this.http.post<ApplePayAuthorization>(url, body, {}).pipe(
      catchError((error: unknown): Observable<never> => throwError((): HttpErrorModel | Error => tryNormalizeHttpError(error, this.loggerService))),
    );
  }
}
