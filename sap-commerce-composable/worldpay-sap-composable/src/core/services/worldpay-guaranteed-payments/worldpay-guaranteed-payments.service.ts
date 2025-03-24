import { Injectable } from '@angular/core';
import { EventService, Query, QueryService, QueryState, WindowRef } from '@spartacus/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { WorldpayGuaranteedPaymentsConnector } from '../../connectors/worldpay-guaranteed-payments/worldpay-guaranteed-payments.connector';
import { SetGuaranteedPaymentsEnabledEvent, SetGuaranteedPaymentsSessionIdEvent } from '../../events/worldpay.events';
import { WorldpayGuaranteedPaymentsFacade } from '../../facade/worldpay-guaranteed-payments.facade';
import { LoadScriptService } from '../../utils/load-script.service';

@Injectable({
  providedIn: 'root'
})
export class WorldpayGuaranteedPaymentsService implements WorldpayGuaranteedPaymentsFacade {

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  window: any = this.winRef.nativeWindow;
  document: Document = this.winRef.document;
  idScript: string = 'sig-api';
  idScriptTag: string = 'script-tag-tmx';
  attributeId: string = 'data-order-session-id';
  userId: string;
  cartId: string;
  drop: Subject<void> = new Subject<void>();
  protected sessionId$: BehaviorSubject<string> = new BehaviorSubject('');

  /**
   * Command used to get Guaranteed Payments Enabled status
   * @since 6.4.0
   * @returns Query<boolean> - Query with boolean value
   */
  protected fetchIsGuaranteedPaymentsEnabledQuery$: Query<boolean> =
    this.queryService.create<boolean>((): Observable<boolean> => this.worldpayGuaranteedPaymentsConnector.isGuaranteedPaymentsEnabled()
      .pipe(
        tap((enabled: boolean): void => this.setGuaranteedPaymentsEnabledEvent(enabled))
      )
    );

  /**
   * Constructor
   * @param queryService QueryService
   * @param loadScriptService LoadScriptService
   * @param winRef WindowRef
   * @param eventService EventService
   * @param worldpayGuaranteedPaymentsConnector WorldpayGuaranteedPaymentsConnector
   */
  constructor(
    protected queryService: QueryService,
    protected loadScriptService: LoadScriptService,
    protected winRef: WindowRef,
    protected eventService: EventService,
    protected worldpayGuaranteedPaymentsConnector: WorldpayGuaranteedPaymentsConnector
  ) {
  }

  /**
   * Get Guaranteed Payments Enabled State
   * @since 6.4.0
   * @returns QueryState<boolean> - QueryState with boolean value
   */
  isGuaranteedPaymentsEnabledState(): Observable<QueryState<boolean>> {
    return this.fetchIsGuaranteedPaymentsEnabledQuery$.getState();
  }

  /**
   * Get Guaranteed Payments Enabled status
   * @since 6.4.0
   * @returns Observable<boolean> - Observable with boolean value
   */
  isGuaranteedPaymentsEnabled(): Observable<boolean> {
    return this.isGuaranteedPaymentsEnabledState().pipe(
      map((state: QueryState<boolean>): boolean => state.data ?? false),
    );
  }

  /**
   * Set Guaranteed Payments Enabled status
   * @since 6.4.0
   * @param enabled - boolean
   */
  setGuaranteedPaymentsEnabledEvent(enabled: boolean): void {
    this.eventService.dispatch({ enabled }, SetGuaranteedPaymentsEnabledEvent);
  }

  /**
   * Get Guaranteed Payments Enabled status
   * @since 6.4.0
   * @returns Observable<boolean> - Observable with boolean value
   */
  getGuaranteedPaymentsEnabledEvent(): Observable<boolean> {
    return this.eventService.get(SetGuaranteedPaymentsEnabledEvent).pipe(
      map((event: SetGuaranteedPaymentsEnabledEvent): boolean => event.enabled)
    );
  }

  /**
   * Get Session Id
   * @since 6.4.0
   * @returns Observable<string> - Observable with string value
   */
  getSessionId(): Observable<string> {
    return this.sessionId$.asObservable();
  }

  /**
   * Set Session Id
   *
   * This method sets the session ID for Guaranteed Payments and dispatches an event
   * to notify other parts of the application about the new session ID.
   *
   * @param {string} sessionId - The session ID to be set
   * @since 6.4.0
   */
  setSessionId(sessionId: string): void {
    this.sessionId$.next(sessionId);
    this.setSessionIdEvent(sessionId);
  }

  /**
   * Get Session Id Event
   *
   * This method retrieves the session ID for Guaranteed Payments by listening
   * for the `SetGuaranteedPaymentsSessionIdEvent` and mapping the event to the session ID.
   *
   * @returns {Observable<string>} - Observable that emits the session ID
   * @since 6.4.0
   */
  getSessionIdEvent(): Observable<string> {
    return this.eventService.get(SetGuaranteedPaymentsSessionIdEvent).pipe(
      map((event: SetGuaranteedPaymentsSessionIdEvent): string => event.sessionId || '')
    );
  }

  /**
   * Set Session Id Event
   *
   * This method dispatches an event to set the session ID for Guaranteed Payments.
   *
   * @param {string} sessionId - The session ID to be dispatched
   */
  setSessionIdEvent(sessionId: string): void {
    this.eventService.dispatch({ sessionId }, SetGuaranteedPaymentsSessionIdEvent);
  }

  /**
   * Generate Guaranteed Payments script
   *
   * This method generates the Guaranteed Payments script by setting the session ID,
   * creating the necessary script attributes, and either updating an existing script
   * node or loading a new script if the session ID is provided.
   *
   * @since 4.3.6
   * @param {string} sessionId - The session ID to be used for the script
   */
  generateScript(sessionId: string): void {
    this.window.tmx_profiling_started = false;
    this.setSessionId(sessionId);
    const attributes: { [p: string]: string } = {
      [this.attributeId]: sessionId
    };
    const node: Element = this.document.querySelector(`script#${this.idScript}`);

    if (node) {
      this.removeScript();
      this.loadScriptService.updateScript(node, attributes);
      this.window?.SIGNIFYD_GLOBAL?.init();
    } else if (sessionId) {
      this.loadScriptService.loadScript({
        idScript: this.idScript,
        src: 'https://cdn-scripts.signifyd.com/api/script-tag.js',
        defer: true,
        attributes,
      });
    }
  }

  /**
   * Remove Guaranteed Payments script
   *
   * This method removes the Guaranteed Payments script by calling the
   * `removeScript` method of the `LoadScriptService` with the script tag ID.
   *
   * @since 4.3.6
   */
  removeScript(): void {
    this.loadScriptService.removeScript(this.idScriptTag);
  }
}
