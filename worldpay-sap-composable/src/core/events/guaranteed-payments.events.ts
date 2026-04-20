import { CxEvent } from '@spartacus/core';

/**
 * SetGuaranteedPaymentsSessionIdEvent is triggered when setSessionIdEvent is called
 * @since 6.4.0
 */
export class SetGuaranteedPaymentsSessionIdEvent extends CxEvent {
  static override readonly type: string = 'SetGuaranteedPaymentsSessionIdEvent';
  sessionId: string;
}

/**
 * SetGuaranteedPaymentsEnabledEvent
 * @since 6.4.0
 */
export class SetGuaranteedPaymentsEnabledEvent extends CxEvent {
  static override readonly type: string = 'SetGuaranteedPaymentsEnabledEvent';
  enabled: boolean;
}
