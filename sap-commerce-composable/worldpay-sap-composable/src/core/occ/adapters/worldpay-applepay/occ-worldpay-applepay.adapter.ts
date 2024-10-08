/* eslint-disable @typescript-eslint/no-explicit-any */
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ConverterService, LoggerService, normalizeHttpError, OccEndpointsService } from '@spartacus/core';
import { WorldpayApplepayAdapter } from '../../../connectors/worldpay-applepay/worldpay-applepay.adapter';
import { catchError } from 'rxjs/operators';
import { ApplePayAuthorization, ApplePayPaymentRequest, ValidateMerchant } from '../../../interfaces';

@Injectable()
export class OccWorldpayApplepayAdapter implements WorldpayApplepayAdapter {
  /**
   * Constructor
   * @param http
   * @param occEndpoints
   * @param converter
   * @param loggerService
   */
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected converter: ConverterService,
    protected loggerService: LoggerService
  ) {
  }

  /**
   * Request Apple Pay Payment Request
   * @since 4.3.6
   * @param userId - User ID
   * @param cartId
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
      catchError((error: unknown) => throwError(() => normalizeHttpError(error, this.loggerService))),
    );
  }

  /**
   * Validate Apple Pay Merchant
   * @since 4.3.6
   * @param userId - User ID
   * @param cartId
   * @param validationURL
   */
  validateApplePayMerchant(
    userId: string,
    cartId: string,
    validationURL: string
  ): Observable<ValidateMerchant> {
    const body = {
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
      catchError((error: unknown) => throwError(() => normalizeHttpError(error, this.loggerService))),
    );

  }

  /**
   * Authorize Apple Pay Payment
   * @since 4.3.6
   * @param userId - User ID
   * @param cartId
   * @param request
   */
  authorizeApplePayPayment(
    userId: string,
    cartId: string,
    request: any
  ): Observable<ApplePayAuthorization> {
    const body = {
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
      catchError((error: unknown) => throwError(() => normalizeHttpError(error, this.loggerService))),
    );
  }
}
