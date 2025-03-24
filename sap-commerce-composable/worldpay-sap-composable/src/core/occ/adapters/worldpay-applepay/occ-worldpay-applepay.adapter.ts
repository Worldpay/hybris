/* eslint-disable @typescript-eslint/no-explicit-any */
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpErrorModel, LoggerService, OccEndpointsService, tryNormalizeHttpError } from '@spartacus/core';
import { WorldpayApplepayAdapter } from '@worldpay-connectors/worldpay-applepay/worldpay-applepay.adapter';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApplePayAuthorization, ApplePayPaymentRequest, ValidateMerchant } from '../../../interfaces';

@Injectable()
export class OccWorldpayApplepayAdapter implements WorldpayApplepayAdapter {
  /**
   * Constructor
   * @param {HttpClient} http - The HTTP client for making requests
   * @param {OccEndpointsService} occEndpoints - Service for building OCC endpoints
   * @param {LoggerService} loggerService - Service for logging errors
   * @since 4.3.6
   */
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected loggerService: LoggerService
  ) {
  }

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
   * @returns {Observable<ValidateMerchant>} - Observable of Validate Merchant response
   * @since 4.3.6
   */
  validateApplePayMerchant(
    userId: string,
    cartId: string,
    validationURL: string
  ): Observable<ValidateMerchant> {
    const body: { validationURL: string; } = {
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

    return this.http.post<ValidateMerchant>(url, body, {}).pipe(
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
   * @param {any} request - Authorization request payload
   * @returns {Observable<ApplePayAuthorization>} - Observable of Apple Pay Authorization response
   * @since 4.3.6
   */
  authorizeApplePayPayment(
    userId: string,
    cartId: string,
    request: any
  ): Observable<ApplePayAuthorization> {
    const body: { request: string; } = {
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
