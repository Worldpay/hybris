import { Injectable } from '@angular/core';
import { facadeFactory } from '@spartacus/core';
import { WORLDPAY_GOOGLE_PAY_FEATURE } from './worldpay-feature-name';
import { GooglePayMerchantConfiguration, GooglepayPaymentRequest, GooglePayPaymentRequest } from '../interfaces';
import { Observable } from 'rxjs';
import { Cart } from '@spartacus/cart/base/root';

@Injectable({
  providedIn: 'root',
  useFactory: () =>
    facadeFactory({
      facade: WorldpayGooglepayFacade,
      feature: WORLDPAY_GOOGLE_PAY_FEATURE,
      methods: [
        'setGooglepayMerchantConfiguration',
        'getMerchantConfigurationFromState',
        'requestMerchantConfiguration',
        'authoriseOrder',
        'createInitialPaymentRequest',
        'createFullPaymentRequest'
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

  /**
   * Create Initial Payment Request
   * @param merchantConfiguration
   */
  abstract createInitialPaymentRequest(merchantConfiguration: GooglePayMerchantConfiguration): GooglepayPaymentRequest;

  /**
   * Create Full Payment Request
   * @since 6.4.0
   * @param merchantConfiguration
   * @param cart
   */
  abstract createFullPaymentRequest(merchantConfiguration: GooglePayMerchantConfiguration, cart: Cart): GooglepayPaymentRequest;
}
