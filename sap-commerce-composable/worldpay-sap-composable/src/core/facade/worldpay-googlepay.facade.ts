import { Injectable } from '@angular/core';
import { facadeFactory } from '@spartacus/core';
import { Observable } from 'rxjs';
import { GooglePayMerchantConfiguration, GooglePayPaymentRequest } from '../interfaces';
import { WORLDPAY_GOOGLE_PAY_FEATURE } from './worldpay-feature-name';

@Injectable({
  providedIn: 'root',
  useFactory: (): WorldpayGooglepayFacade =>
    facadeFactory({
      facade: WorldpayGooglepayFacade,
      feature: WORLDPAY_GOOGLE_PAY_FEATURE,
      methods: [
        'setGooglepayMerchantConfiguration',
        'getMerchantConfigurationFromState',
        'requestMerchantConfiguration',
        'authoriseOrder',
      ],
    }),
})
export abstract class WorldpayGooglepayFacade {

  /**
   * Set Googlepay Merchant Configuration
   * @since 6.4.0
   */
  abstract setGooglepayMerchantConfiguration(googlePayMerchantConfiguration: GooglePayMerchantConfiguration): void;

  /**
   * Get Google Pay Merchant Configuration
   * @since 6.4.0
   */
  abstract getMerchantConfigurationFromState(): Observable<GooglePayMerchantConfiguration>;

  /**
   * Request Merchant Configuration
   * @since 6.4.0
   */
  abstract requestMerchantConfiguration(): void;

  /**
   * Authorise Order
   * @param paymentRequest
   * @param savePaymentMethod
   */
  abstract authoriseOrder(paymentRequest: GooglePayPaymentRequest, savePaymentMethod: boolean): void;
}
