import { Injectable } from '@angular/core';
import { facadeFactory } from '@spartacus/core';
import { Observable } from 'rxjs';
import { GooglePayMerchantConfiguration, GooglePayPaymentResponse } from '../models';
import { WORLDPAY_GOOGLE_PAY_FEATURE } from './worldpay-feature-name';

/**
 * Facade for Worldpay Google Pay integration.
 *
 * This abstract class defines the contract for managing Google Pay merchant configuration,
 * requesting merchant configuration, authorising orders, and handling checkout preconditions
 * for the Worldpay Google Pay feature.
 *
 * @since 2211.43.0
 */
@Injectable({
  providedIn: 'root',
  useFactory: (): WorldpayGooglepayFacade =>
    facadeFactory({
      facade: WorldpayGooglepayFacade,
      feature: WORLDPAY_GOOGLE_PAY_FEATURE,
      methods: [
        'setGooglepayMerchantConfiguration',
        'getMerchantConfigurationFromState',
        'executeRequestMerchantConfiguration',
        'requestMerchantConfiguration',
        'authoriseOrder',
        'checkoutPreconditions'
      ],
    }),
})
export abstract class WorldpayGooglepayFacade {

  /**
   * Sets the Google Pay merchant configuration in the state.
   *
   * @param googlePayMerchantConfiguration The merchant configuration to set.
   * @since 6.4.0
   */
  abstract setGooglepayMerchantConfiguration(googlePayMerchantConfiguration: GooglePayMerchantConfiguration): void;

  /**
   * Returns an observable of the current Google Pay merchant configuration from the state.
   *
   * @returns Observable emitting the Google Pay merchant configuration.
   * @since 6.4.0
   */
  abstract getMerchantConfigurationFromState(): Observable<GooglePayMerchantConfiguration>;

  /**
   * Executes a request to fetch the Google Pay merchant configuration.
   *
   * @returns Observable emitting the fetched Google Pay merchant configuration.
   * @since 2211.43.0
   */
  abstract executeRequestMerchantConfiguration(): Observable<GooglePayMerchantConfiguration>

  /**
   * Triggers a request to fetch the Google Pay merchant configuration and updates the state.
   *
   * @since 6.4.0
   */
  abstract requestMerchantConfiguration(): void;

  /**
   * Authorises a Google Pay order with the given payment request and save payment method flag.
   *
   * @param paymentRequest The Google Pay payment request.
   * @param savePaymentMethod Whether to save the payment method for future use.
   * @since 2211.43.0
   */
  abstract authoriseOrder(paymentRequest: GooglePayPaymentResponse, savePaymentMethod: boolean): void;

  /**
   * Returns an observable of the checkout preconditions (userId and cartId).
   *
   * @returns Observable emitting a tuple of [userId, cartId].
   * @since 2211.43.0
   */
  abstract checkoutPreconditions(): Observable<[string, string]>;
}
