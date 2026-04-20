import { CxEvent } from '@spartacus/core';

/**
 * SetFraudSightEnabledEvent is triggered when setFraudSightEnabled is called
 * @since 6.4.0
 */
export class SetFraudSightEnabledEvent extends CxEvent {
  static override readonly type: string = 'SetFraudSightEnabledEvent';
  enabled: boolean;
}

/**
 * SetFraudSightIdEvent is triggered when setFraudSightId is called
 * @since 6.4.0
 */
export class SetFraudSightIdEvent extends CxEvent {
  static override readonly type: string = 'SetFraudSightIdEvent';
  fraudSightId: string;
}
