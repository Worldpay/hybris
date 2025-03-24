import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConverterService, OccEndpointsService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { WorldpayFraudsightAdapter } from '../../../connectors/worldpay-fraudsight/worldpay-fraudsight.adapter';

@Injectable()
export class OccWorldpayFraudsightAdapter implements WorldpayFraudsightAdapter {
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected converter: ConverterService
  ) {
  }

  /**
   * Method verify if Fraud Sight is enabled.
   * @since 6.4.0
   */
  isFraudSightEnabled(): Observable<boolean> {
    const url: string = this.occEndpoints.buildUrl('isFraudSightEnabled');
    // eslint-disable-next-line @typescript-eslint/typedef
    const options = {
      responseType: 'text' as 'json'
    };
    return this.http.get<string>(
      url,
      options,
    ).pipe(map((str: string): boolean => str === 'true'));
  }
}
