import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { backOff, ConverterService, HttpErrorModel, isJaloError, LoggerService, OccEndpointsService, tryNormalizeHttpError } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { WorldpayACHAdapter } from '../../../connectors/worldpay-ach/worldpay-ach.adapter';
import { ACHBankAccountType, ACHPaymentForm } from '../../../interfaces';

@Injectable()
export class OccWorldpayACHAdapter implements WorldpayACHAdapter {
  /**
   * Constructor for OccWorldpayACHAdapter
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
   * Get ACH Bank Account Types
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @returns {Observable<ACHBankAccountType[]>} An observable that emits the list of ACH bank account types.
   * @since 6.4.2
   */
  getACHBankAccountTypes(
    userId: string,
    cartId: string,
  ): Observable<ACHBankAccountType[]> {

    const url: string = this.occEndpoints.buildUrl(
      'getACHBankAccountTypes',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.get<ACHBankAccountType[]>(
      url,
      {}
    );
  }

  /**
   * Place an ACH order
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {ACHPaymentForm} achPaymentForm - ACH Payment Form
   * @returns {Observable<Order>} An observable that emits the order details.
   * @since 4.3.6
   */
  placeACHOrder(
    userId: string,
    cartId: string,
    achPaymentForm: ACHPaymentForm
  ): Observable<Order> {
    const body: ACHPaymentForm = achPaymentForm;
    const url: string = this.occEndpoints.buildUrl(
      'placeACHOrder',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.post<Order>(url, body).pipe(
      catchError((error: unknown): Observable<never> => throwError((): HttpErrorModel | Error => tryNormalizeHttpError(error, this.loggerService))),
      backOff({
        shouldRetry: isJaloError,
      })
    );
  }
}
