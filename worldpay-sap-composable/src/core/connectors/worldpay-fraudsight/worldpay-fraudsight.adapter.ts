import { Observable } from 'rxjs';

export abstract class WorldpayFraudsightAdapter {

  /**
   * Abstract method used to get FraudSight status
   * @since 6.4.0
   * @returns Observable<boolean> - Observable with boolean value
   */
  abstract isFraudSightEnabled(): Observable<boolean>;
}
