import { Observable } from 'rxjs';

export abstract class WorldpayGuaranteedPaymentsAdapter {

  /**
   * Abstract method used to check BaseSite configuration and validate if Guaranteed Payments is enabled
   * @since 6.4.0
   * @returns {Observable<boolean>} - Observable with boolean value
   */
  abstract isGuaranteedPaymentsEnabled(): Observable<boolean>;
}
