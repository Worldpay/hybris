/**
 * Facade for the Worldpay Guaranteed Payments feature.
 *
 * This facade provides methods to manage the state and events related to Worldpay Guaranteed Payments,
 * including enabling/disabling the feature, handling session IDs, and managing the script lifecycle.
 *
 * @since 6.4.0
 */
import { Injectable } from '@angular/core';
import { facadeFactory, QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import { WORLDPAY_GUARANTEED_PAYMENTS_FEATURE } from './worldpay-feature-name';

@Injectable({
  providedIn: 'root',
  useFactory: (): WorldpayGuaranteedPaymentsFacade =>
    facadeFactory({
      facade: WorldpayGuaranteedPaymentsFacade,
      feature: WORLDPAY_GUARANTEED_PAYMENTS_FEATURE,
      methods: [
        'isGuaranteedPaymentsEnabledState',
        'isGuaranteedPaymentsEnabled',
        'setGuaranteedPaymentsEnabledEvent',
        'getGuaranteedPaymentsEnabledEvent',
        'getSessionId',
        'setSessionId',
        'getSessionIdEvent',
        'setSessionIdEvent',
        'generateScript',
        'removeScript'
      ],
    }),
})
export abstract class WorldpayGuaranteedPaymentsFacade {

  /**
   * Returns the state of whether Guaranteed Payments is enabled, as a QueryState observable.
   * @returns Observable of QueryState<boolean>
   * @since 6.4.0
   */
  abstract isGuaranteedPaymentsEnabledState(): Observable<QueryState<boolean>>;

  /**
   * Returns whether Guaranteed Payments is enabled.
   *
   * @returns Observable<boolean>
   * @since 2211.43.0
   */
  abstract isGuaranteedPaymentsEnabled(): Observable<boolean>;

  /**
   * Sets the event for enabling/disabling Guaranteed Payments.
   *
   * @param enabled - true to enable, false to disable
   * @since 2211.43.0
   */
  abstract setGuaranteedPaymentsEnabledEvent(enabled: boolean): void;

  /**
   * Gets the event state for Guaranteed Payments enabled/disabled.
   *
   * @returns Observable<boolean>
   *   @since 2211.43.0
   */
  abstract getGuaranteedPaymentsEnabledEvent(): Observable<boolean>

  /**
   * Returns the current session ID for Guaranteed Payments.
   *
   * @returns Observable<string>
   * @since 2211.43.0
   */
  abstract getSessionId(): Observable<string>;

  /**
   * Sets the session ID for Guaranteed Payments.
   *
   * @param sessionId - The session ID string
   * @since 2211.43.0
   */
  abstract setSessionId(sessionId: string): void

  /**
   * Gets the event state for the session ID.
   *
   * @returns Observable<string>
   * @since 2211.43.0
   */
  abstract getSessionIdEvent(): Observable<string>

  /**
   * Emits an event to set the session ID for Guaranteed Payments.
   *
   * @param sessionId string
   * @since 2211.43.0
   */
  abstract setSessionIdEvent(sessionId: string): void

  /**
   * Dynamically generates the Worldpay Guaranteed Payments script with the given session ID.
   *
   * @param sessionId string
   * @since 2211.43.0
   */
  abstract generateScript(sessionId: string): void

  /**
   * Removes the Worldpay Guaranteed Payments script from the DOM.
   *
   * @since 2211.43.0
   */
  abstract removeScript(): void
}
