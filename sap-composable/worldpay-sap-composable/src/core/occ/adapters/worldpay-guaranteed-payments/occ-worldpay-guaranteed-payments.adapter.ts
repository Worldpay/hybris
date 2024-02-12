import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ConverterService, normalizeHttpError, OccEndpointsService } from '@spartacus/core';
import { WorldpayGuaranteedPaymentsAdapter } from '../../../connectors/worldpay-guaranteed-payments/worldpay-guaranteed-payments.adapter';
import { catchError } from 'rxjs/operators';

@Injectable()
export class OccWorldpayGuaranteedPaymentsAdapter implements WorldpayGuaranteedPaymentsAdapter {
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
   * Method used to fetch Guaranteed payment Status.
   * @since 4.3.6
   */
  isGuaranteedPaymentsEnabled(): Observable<boolean> {
    const url = this.occEndpoints.buildUrl(
      'isGuaranteedPaymentsEnabled'
    );

    return this.http.get<boolean>(
      url,
      {},
    ).pipe(
      catchError((error: unknown) => throwError(normalizeHttpError(error))),
    );
  }
}
