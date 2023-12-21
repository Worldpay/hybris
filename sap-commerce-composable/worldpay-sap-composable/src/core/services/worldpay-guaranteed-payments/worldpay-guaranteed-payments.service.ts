import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { LoadScriptService } from '../../utils/load-script.service';
import { EventService, Query, QueryService, QueryState, WindowRef } from '@spartacus/core';
import { WorldpayGuaranteedPaymentsConnector } from '../../connectors/worldpay-guaranteed-payments/worldpay-guaranteed-payments.connector';
import { WorldpayGuaranteedPaymentsFacade } from '../../facade/worldpay-guaranteed-payments.facade';
import { SetGuaranteedPaymentsEnabledEvent, SetGuaranteedPaymentsSessionIdEvent } from '../../events/worldpay.events';
import { map, tap } from 'rxjs/operators';

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
    this.queryService.create<boolean>(() => this.worldpayGuaranteedPaymentsConnector.isGuaranteedPaymentsEnabled()
      .pipe(
        tap((enabled: boolean) => this.setGuaranteedPaymentsEnabledEvent(enabled))
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
      map((state: QueryState<boolean>) => state.data ?? false),
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
      map((event: SetGuaranteedPaymentsEnabledEvent) => event.enabled)
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
   * Set Session id
   * @since 6.4.0
   * @param sessionId - string
   */
  setSessionId(sessionId: string): void {
    this.sessionId$.next(sessionId);
    this.setSessionIdEvent(sessionId);
  }

  /**
   * Get Session Id Event
   * @since 6.4.0
   * @returns Observable<string> - Observable with string value
   */
  getSessionIdEvent(): Observable<string> {
    return this.eventService.get(SetGuaranteedPaymentsSessionIdEvent).pipe(
      map((event: SetGuaranteedPaymentsSessionIdEvent) => event.sessionId)
    );
  }

  /**
   * Set Session Id Event
   * @param sessionId - string
   */
  setSessionIdEvent(sessionId: string): void {
    this.eventService.dispatch({ sessionId }, SetGuaranteedPaymentsSessionIdEvent);
  }

  /**
   * Generate Guaranteed Payments script
   * @since 4.3.6
   * @param sessionId - string
   */
  generateScript(sessionId: string): void {
    this.window.tmx_profiling_started = false;
    this.setSessionId(sessionId);
    const attributes = {
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
   * @since 4.3.6
   */
  removeScript(): void {
    this.loadScriptService.removeScript(this.idScriptTag);
  }
}
