/* eslint-disable @typescript-eslint/no-explicit-any */
import { Injectable } from '@angular/core';
import { facadeFactory } from '@spartacus/core';
import { Observable } from 'rxjs';
import { ApplePayPaymentRequest } from '../interfaces';
import { WORLDPAY_APPLE_PAY_FEATURE } from './worldpay-feature-name';

@Injectable({
  providedIn: 'root',
  useFactory: (): WorldpayApplepayFacade =>
    facadeFactory({
      facade: WorldpayApplepayFacade,
      feature: WORLDPAY_APPLE_PAY_FEATURE,
      methods: [
        'getPaymentRequestFromState',
        'createSession',
      ],
    }),
})
export abstract class WorldpayApplepayFacade {

  /**
   * Get Guaranteed Payments Enabled State
   * @since 6.4.0
   */
  abstract getPaymentRequestFromState(): Observable<ApplePayPaymentRequest>;

  /**
   * Create Apple Pay Session
   * @param paymentRequest
   */
  abstract createSession(paymentRequest: ApplePayPaymentRequest): any;
}
