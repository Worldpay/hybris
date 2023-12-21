import { CxEvent } from '@spartacus/core';

export * from './guaranteed-payments.events';
export * from './fraudsight.events';
export * from './apm.events';

/**
 * ClearWorldpayPaymentStateEvent clear all payment data information from state when an order is placed
 * @since 6.4.0
 */
export class ClearWorldpayPaymentStateEvent extends CxEvent {
  static override readonly type: string = 'ClearWorldpayPaymentStateEvent';
}

/**
 * ClearWorldpayPaymentDetailsEvent clear all payment data information from state when an order is placed
 * @since 6.4.0
 */
export class ClearWorldpayPaymentDetailsEvent extends CxEvent {
  static override readonly type: string = 'ClearWorldpayPaymentDetailsEvent';
}

/**
 * WorldpayChallengeAcceptedSetEvent
 * @since 6.4.0
 */
export class WorldpayChallengeAcceptedSetEvent extends CxEvent {
  static override readonly type: string = 'WorldpayChallengeAcceptedSetEvent';
  threeDsChallengeIframeUrl: string;
}

/**
 * ResetWorldpaySavedCreditCardEvent
 * @since 6.4.0
 */
export class SetWorldpaySavedCreditCardEvent extends CxEvent {
  static override readonly type: string = 'ResetWorldpaySavedCreditCardEvent';
  saved: boolean;
}

/**
 * Set Worldpay Save As Default Credit Card Event
 * @since 6.4.0
 */
export class SetWorldpaySaveAsDefaultCreditCardEvent extends CxEvent {
  static override readonly type: string = 'SetWorldpaySaveAsDefaultCreditCardEvent';
  saved: boolean;
}
