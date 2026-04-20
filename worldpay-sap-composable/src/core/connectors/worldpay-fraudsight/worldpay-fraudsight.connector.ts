import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { WorldpayFraudsightAdapter } from './worldpay-fraudsight.adapter';

@Injectable({
  providedIn: 'root'
})
export class WorldpayFraudsightConnector {

  /**
   * Constructor
   * @since 6.4.0
   * @param adapter - WorldpayFraudsightAdapter
   */
  constructor(
    protected adapter: WorldpayFraudsightAdapter
  ) {
  }

  /**
   * Method used to get FraudSight status.
   * @since 6.4.0
   * @returns Observable<boolean> - Observable with boolean value
   */
  public isFraudSightEnabled(): Observable<boolean> {
    return this.adapter.isFraudSightEnabled();
  }

}
