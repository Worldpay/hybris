import { SafeResourceUrl } from '@angular/platform-browser';
import { Address, CxEvent, PaymentDetails } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { ThreeDsDDCInfo, ThreeDsInfo } from '../interfaces';

/**
 * ThreeDsChallengeIframeUrlSetEvent is triggered when setThreeDsChallengeIframeUrl is called
 * @since 6.4.0
 */
export class ThreeDsChallengeIframeUrlSetEvent extends CxEvent {
  static override readonly type: string = 'ThreeDsChallengeIframeUrlSetEvent';
  worldpayChallengeIframeUrl: SafeResourceUrl;
}

/**
 * ThreeDsDDCIframeUrlSetEvent is triggered when setThreeDsDDCIframeUrl is called
 * @since 6.4.0
 */
export class ThreeDsDDCIframeUrlSetEvent extends CxEvent {
  static override readonly type: string = 'ThreeDsDDCIframeUrlSetEvent';
  threeDsDDCIframeUrl: string;
}

/**
 * DDC3dsJwtSetEvent is triggered when the user selects a payment method
 * @since 6.4.0
 */
export class DDC3dsJwtSetEvent extends CxEvent {
  static override readonly type: string = 'DDC3dsJwtSetEvent';
  ddcInfo: ThreeDsDDCInfo;
}

/**
 * Create Worldpay Payment Details Event
 * @since 6.4.0
 */
export class CreateWorldpayPaymentDetailsEvent extends CxEvent {
  static override readonly type: string = 'CreateWorldpayPaymentDetailsEvent';
  paymentDetails: PaymentDetails;
  cseToken: string;
}

/**
 * ThreeDsEvent is triggered when the user selects a payment method
 * @since 6.4.0
 */
export class ThreeDsSetEvent extends CxEvent {
  static override readonly type: string = 'ThreeDsSetEvent';
  threeDsDDCIframeUrl?: SafeResourceUrl;
  threeDsChallengeIframeUrl?: SafeResourceUrl;
  threeDsDDCInfo?: ThreeDsDDCInfo;
  threeDsChallengeInfo?: ThreeDsInfo;
}

/**
 * Clear Initial Payment Request Event
 * @since 6.4.0
 */
export class ClearInitialPaymentRequestEvent extends ThreeDsSetEvent {
  static override readonly type: string = 'ClearInitialPaymentRequestEvent';
}

/**
 * Initial Payment Request Set Event
 * @since 6.4.0
 */
export class InitialPaymentRequestSetEvent extends CxEvent {
  static override readonly type: string = 'InitialPaymentRequestSetEvent';
  threeDSecureNeeded: boolean;
  threeDSecureInfo: ThreeDsInfo;
  transactionStatus: string;
  order: Order;
  returnCode: string;
}

/**
 * Get Worldpay Public Key Event
 * @since 6.4.0
 */
export class SetWorldpayPublicKeyEvent extends CxEvent {
  static override readonly type: string = 'SetWorldpayPublicKeyEvent';
  publicKey: string;
}

/**
 * Get DDC 3ds JWT Event
 * @since 6.4.0
 */
export class GetDDC3dsJwtEvent extends CxEvent {
  static override readonly type: string = 'GetDDC3dsJwtEvent';
  dDC3dsJwt: ThreeDsDDCInfo;
}

/**
 * Set Payment Address Event is triggered when the user selects a payment address
 * @since 6.4.0
 */
export class SetPaymentAddressEvent extends CxEvent {
  static override readonly type: string = 'SetPaymentAddressEvent';
  address: Address;
}
