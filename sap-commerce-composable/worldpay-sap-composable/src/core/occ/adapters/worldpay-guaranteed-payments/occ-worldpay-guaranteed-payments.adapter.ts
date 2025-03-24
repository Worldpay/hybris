import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConverterService, HttpErrorModel, LoggerService, OccEndpointsService, tryNormalizeHttpError } from '@spartacus/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { WorldpayGuaranteedPaymentsAdapter } from '../../../connectors/worldpay-guaranteed-payments/worldpay-guaranteed-payments.adapter';

@Injectable()
export class OccWorldpayGuaranteedPaymentsAdapter implements WorldpayGuaranteedPaymentsAdapter {
  /**
   * Constructor for OccWorldpayGuaranteedPaymentsAdapter
   * @param {HttpClient} http - The HTTP client for making requests
   * @param {OccEndpointsService} occEndpoints - Service for building OCC endpoint URLs
   * @param {ConverterService} converter - Service for converting data
   * @param {LoggerService} loggerService - Service for logging errors
   * @since 4.3.6
   */
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected converter: ConverterService,
    protected loggerService: LoggerService,
  ) {
  }

  /**
   * Method used to fetch Guaranteed payment Status.
   * @returns {Observable<boolean>} An observable that emits the guaranteed payment status.
   * @since 4.3.6
   */
  isGuaranteedPaymentsEnabled(): Observable<boolean> {
    const url: string = this.occEndpoints.buildUrl(
      'isGuaranteedPaymentsEnabled'
    );

    return this.http.get<boolean>(
      url,
      {},
    ).pipe(
      catchError((error: unknown): Observable<never> => throwError((): HttpErrorModel | Error => tryNormalizeHttpError(error, this.loggerService))),
    );
  }
}
