import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { backOff, ConverterService, isJaloError, LoggerService, normalizeHttpError, OccEndpointsService } from '@spartacus/core';
import { catchError } from 'rxjs/operators';
import { Order } from '@spartacus/order/root';
import { ACHBankAccountType, ACHPaymentForm } from '../../../interfaces';
import { WorldpayACHAdapter } from '../../../connectors/worldpay-ach/worldpay-ach.adapter';

@Injectable()
export class OccWorldpayACHAdapter implements WorldpayACHAdapter {
  /**
   * Constructor
   * @param http HttpClient
   * @param occEndpoints OccEndpointsService
   * @param converter ConverterService
   * @param loggerService LoggerService
   */
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected converter: ConverterService,
    protected loggerService: LoggerService
  ) {
  }

  /**
   * Redirect authorise APM payment
   * @since 6.4.2
   * @param userId - User ID
   * @param cartId - Cart ID
   */
  getACHBankAccountTypes(
    userId: string,
    cartId: string,
  ): Observable<ACHBankAccountType[]> {

    const url = this.occEndpoints.buildUrl(
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
   * Get a list of all available APM's for the given cart
   * @since 4.3.6
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param achPaymentForm - ACH Payment Form
   */
  placeACHOrder(
    userId: string,
    cartId: string,
    achPaymentForm: ACHPaymentForm
  ): Observable<Order> {
    const body = achPaymentForm;
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
      catchError((error: unknown) => throwError(() => normalizeHttpError(error, this.loggerService))),
      backOff({
        shouldRetry: isJaloError,
      })
    );
  }
}
