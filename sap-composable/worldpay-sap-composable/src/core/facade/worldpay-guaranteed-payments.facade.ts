import { Injectable } from '@angular/core';
import { facadeFactory, QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import { WORLDPAY_GUARANTEED_PAYMENTS_FEATURE } from './worldpay-feature-name';

@Injectable({
  providedIn: 'root',
  useFactory: () =>
    facadeFactory({
      facade: WorldpayGuaranteedPaymentsFacade,
      feature: WORLDPAY_GUARANTEED_PAYMENTS_FEATURE,
      methods: [
        'isGuaranteedPaymentsEnabledState',
      ],
    }),
})
export abstract class WorldpayGuaranteedPaymentsFacade {

  /**
   * Get Guaranteed Payments Enabled State
   * @since 6.4.0
   */
  abstract isGuaranteedPaymentsEnabledState(): Observable<QueryState<boolean>>;
}
