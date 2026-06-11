import { inject, Injectable } from '@angular/core';
import { EventService, Query, QueryService, QueryState } from '@spartacus/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { WorldpayFraudsightConnector } from '../../connectors';
import { SetFraudSightEnabledEvent, SetFraudSightIdEvent } from '../../events';
import { WorldpayFraudsightFacade } from '../../facade/worldpay-fraudsight.facade';

@Injectable()
export class WorldpayFraudsightService implements WorldpayFraudsightFacade {

  protected queryService: QueryService = inject(QueryService);
  protected eventService: EventService = inject(EventService);
  protected worldpayFraudsightConnector: WorldpayFraudsightConnector = inject(WorldpayFraudsightConnector);
  /**
   * Command used to get FraudSight status
   * @since 6.4.0
   * @protected
   */
  protected getIsFraudSightEnabledQuery$: Query<boolean> =
    this.queryService.create<boolean>(
      (): Observable<boolean> => this.worldpayFraudsightConnector.isFraudSightEnabled(),
    );
  // Fraud Sight
  private fraudSightId$: BehaviorSubject<string> = new BehaviorSubject('');

  /**
   *  Method used to get FraudSight status
   *  @since 6.4.0
   */
  isFraudSightEnabled(): Observable<QueryState<boolean>> {
    return this.getIsFraudSightEnabledQuery$.getState();
  }

  /**
   * Method used to get FraudSight status
   * @since 6.4.0
   */
  getFraudSightIdFromState(): Observable<string> {
    return this.fraudSightId$.asObservable();
  }

  /**
   * Method used to set FraudSight Id
   * @since 6.4.0
   * @param id
   */
  setFraudSightId(id: string): void {
    this.fraudSightId$.next(id);
    this.eventService.dispatch({ fraudSightId: id }, SetFraudSightIdEvent);
  }

  /**
   * Method used to get FraudSight status
   * @since 6.4.0
   */
  isFraudSightEnabledFromState(): Observable<boolean> {
    return this.isFraudSightEnabled().pipe(
      map((state: QueryState<boolean>): boolean => state.data)
    );
  }

  /**
   * Method used to set FraudSight status
   * @since 6.4.0
   * @param state
   */
  setFraudSightEnabled(state: QueryState<boolean>): void {
    this.eventService.dispatch({ enabled: state.data }, SetFraudSightEnabledEvent);
  }
}
