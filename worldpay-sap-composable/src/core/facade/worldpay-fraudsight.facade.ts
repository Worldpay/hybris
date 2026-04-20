import { Injectable } from '@angular/core';
import { facadeFactory, QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import { WORLDPAY_FRAUD_SIGHT_FEATURE } from './worldpay-feature-name';

@Injectable({
  providedIn: 'root',
  useFactory: (): WorldpayFraudsightFacade =>
    facadeFactory({
      facade: WorldpayFraudsightFacade,
      feature: WORLDPAY_FRAUD_SIGHT_FEATURE,
      methods: [
        'getFraudSightIdFromState',
        'isFraudSightEnabled',
        'setFraudSightId',
        'setFraudSightEnabled',
        'isFraudSightEnabledFromState'
      ],
    }),
})
export abstract class WorldpayFraudsightFacade {

  /**
   * Returns an observable of the current FraudSight session ID from state.
   * @returns Observable emitting the FraudSight session ID as a string.
   */
  abstract getFraudSightIdFromState(): Observable<string>

  /**
   * Returns an observable of the FraudSight enabled status as a QueryState.
   * @returns Observable emitting the QueryState of the enabled status.
   * @since 6.4.0
   */
  abstract isFraudSightEnabled(): Observable<QueryState<boolean>>;

  /**
   * Sets the FraudSight session ID in state.
   * @param id The FraudSight session ID to set.
   * @since 2211.43.0
   */
  abstract setFraudSightId(id: string): void;

  /**
   * Sets the FraudSight enabled status in state.
   * @param state The QueryState<boolean> representing the enabled status.
   * @since 2211.43.0
   */
  abstract setFraudSightEnabled(state: QueryState<boolean>): void;

  /**
   * Returns an observable of the FraudSight enabled status from state.
   * @returns Observable emitting the enabled status as a boolean.
   * @since 2211.43.0
   */
  abstract isFraudSightEnabledFromState(): Observable<boolean>;
}
