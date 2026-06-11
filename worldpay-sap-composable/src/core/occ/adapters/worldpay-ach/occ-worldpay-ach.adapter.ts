import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { backOff, ConverterService, HttpErrorModel, isJaloError, LoggerService, OccEndpointsService, tryNormalizeHttpError } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { WorldpayACHAdapter } from '../../../connectors';
import { AccountTypes, ACHPaymentForm } from '../../../interfaces';

@Injectable()
export class OccWorldpayACHAdapter implements WorldpayACHAdapter {

  protected http: HttpClient = inject(HttpClient);
  protected occEndpoints: OccEndpointsService = inject(OccEndpointsService);
  protected converter: ConverterService = inject(ConverterService);
  protected loggerService: LoggerService = inject(LoggerService);

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
  ): Observable<AccountTypes> {

    const url: string = this.occEndpoints.buildUrl(
      'getACHBankAccountTypes',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.get<AccountTypes>(
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
