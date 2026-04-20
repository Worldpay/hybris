/* eslint-disable @typescript-eslint/no-explicit-any */
import { CxEvent } from '@spartacus/core';
import { ApplePayPaymentRequest, PlaceOrderResponse } from '../interfaces';

/**
 * RequestApplePayPaymentRequestEvent
 * @since 6.4.0
 */
export class RequestApplePayPaymentRequestEvent extends CxEvent {
  static override readonly type: string = 'RequestApplePayPaymentRequestEvent';
  applePayPaymentRequest: ApplePayPaymentRequest;
}

/**
 * ApplePayMerchantSessionEvent
 * @since 6.4.0
 */
export class ApplePayMerchantSessionEvent extends CxEvent {
  static override readonly type: string = 'ValidateApplePayMerchantEvent';
  merchantSession: any;
}

/**
 * ApplePayAuthorizationEvent
 * @since 6.4.0
 */
export class ApplePayAuthorizePaymentEvent extends CxEvent {
  static override readonly type: string = 'ApplePayAuthorizePaymentEvent';
  authorizePaymentEvent: PlaceOrderResponse;
}
