import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { WorldpayGuaranteedPaymentsAdapter } from './worldpay-guaranteed-payments.adapter';

@Injectable({
  providedIn: 'root'
})
export class WorldpayGuaranteedPaymentsConnector {

  /**
   * Constructor
   * @since 6.4.0
   * @param adapter WorldpayGuaranteedPaymentsAdapter
   */
  constructor(
    protected adapter: WorldpayGuaranteedPaymentsAdapter
  ) {
  }

  /**
   * Method used to check BaseSite configuration and validate if Guaranteed Payments is enabled
   * @since 6.4.0
   * @returns - Observable with boolean value
   */
  public isGuaranteedPaymentsEnabled(): Observable<boolean> {
    return this.adapter.isGuaranteedPaymentsEnabled();
  }

}
