import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ConverterService, HttpErrorModel, LoggerService, OccEndpointsService, tryNormalizeHttpError } from '@spartacus/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { WorldpayGuaranteedPaymentsAdapter } from '../../../connectors';

@Injectable()
export class OccWorldpayGuaranteedPaymentsAdapter implements WorldpayGuaranteedPaymentsAdapter {
  protected http: HttpClient = inject(HttpClient);
  protected occEndpoints: OccEndpointsService = inject(OccEndpointsService);
  protected converter: ConverterService = inject(ConverterService);
  protected loggerService: LoggerService = inject(LoggerService);

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
