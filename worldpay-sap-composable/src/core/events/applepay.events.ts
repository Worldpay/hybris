/* eslint-disable @typescript-eslint/no-explicit-any */
import { CxEvent } from '@spartacus/core';
import { PlaceOrderResponse } from '../interfaces';
import { ApplePayMerchantSession, ApplePayPaymentRequest } from '../models';

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
  merchantSession: ApplePayMerchantSession;
}

/**
 * ApplePayAuthorizationEvent
 * @since 6.4.0
 */
export class ApplePayAuthorizePaymentEvent extends CxEvent {
  static override readonly type: string = 'ApplePayAuthorizePaymentEvent';
  authorizePaymentEvent: PlaceOrderResponse;
}
