import { Injectable } from '@angular/core';
import { facadeFactory, QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import { WORLDPAY_FRAUD_SIGHT_FEATURE } from './worldpay-feature-name';

@Injectable({
  providedIn: 'root',
  useFactory: () =>
    facadeFactory({
      facade: WorldpayFraudsightFacade,
      feature: WORLDPAY_FRAUD_SIGHT_FEATURE,
      methods: [
        'isFraudSightEnabled',
      ],
    }),
})
export abstract class WorldpayFraudsightFacade {

  /**
   * Method used to get Frau Sight Enabled status
   * @since 6.4.0
   */
  abstract isFraudSightEnabled(): Observable<QueryState<boolean>>;
}
