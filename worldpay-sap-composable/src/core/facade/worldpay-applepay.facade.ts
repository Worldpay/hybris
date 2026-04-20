/* eslint-disable @typescript-eslint/no-explicit-any */
/**
 * Facade for Worldpay Apple Pay integration.
 *
 * This abstract class defines the contract for Apple Pay operations within the Worldpay integration,
 * including session management, payment authorization, and state management for Apple Pay requests and responses.
 *
 * ### Usage:
 * - Use this facade to interact with Apple Pay features in the Worldpay payment flow.
 * - Methods include session creation, merchant validation, payment authorization, and state selectors.
 *
 * @since 2211.43.0
 */
import { Injectable } from '@angular/core';
import { facadeFactory, QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import { ApplePayAuthorization, ApplePayPaymentRequest, PlaceOrderResponse } from '../interfaces';
import { WORLDPAY_APPLE_PAY_FEATURE } from './worldpay-feature-name';

@Injectable({
  providedIn: 'root',
  useFactory: (): WorldpayApplepayFacade =>
    facadeFactory({
      facade: WorldpayApplepayFacade,
      feature: WORLDPAY_APPLE_PAY_FEATURE,
      methods: [
        'applepayMerchantSessionEvent',
        'authorizePaymentEvent',
        'enableApplePayButton',
        'createSession',
        'requestApplePayPaymentRequest',
        'getPaymentRequestFromState',
        'getMerchantSessionFromState',
        'getPaymentAuthorizationFromState',
        'checkoutPreconditions',
        'onValidateMerchant',
        'onPaymentAuthorized',
        'onPaymentError',
        'setMerchantSession',
        'setPaymentAuthorization'
      ],
    }),
})
export abstract class WorldpayApplepayFacade {
  /**
   * Dispatches an event to request the Apple Pay merchant session from the backend.
   * Used to initiate merchant validation with Apple Pay.
   * @since 2211.43.0
   */
  abstract applepayMerchantSessionEvent(): void;

  /**
   * Dispatches an event to request payment authorization from Apple Pay.
   * Used to trigger the payment authorization process.
   * @since 2211.43.0
   */
  abstract authorizePaymentEvent(): void;

  /**
   * Enables the Apple Pay button and returns the payment request configuration.
   * @returns Observable emitting the ApplePayPaymentRequest configuration.
   * @since 2211.43.0
   */
  abstract enableApplePayButton(): Observable<ApplePayPaymentRequest>;

  /**
   * Creates an Apple Pay session using the provided payment request.
   * @param paymentRequest The Apple Pay payment request object.
   * @returns The created Apple Pay session instance.
   * @since 2211.43.0
   */
  abstract createSession(paymentRequest: ApplePayPaymentRequest): any;

  /**
   * Requests the Apple Pay payment request from the backend or state.
   * @returns Observable emitting the QueryState of ApplePayPaymentRequest.
   * @since 2211.43.0
   */
  abstract requestApplePayPaymentRequest(): Observable<QueryState<ApplePayPaymentRequest>>;

  /**
   * Gets the Apple Pay payment request from the state.
   * @returns Observable emitting the ApplePayPaymentRequest.
   * @since 2211.43.0
   */
  abstract getPaymentRequestFromState(): Observable<ApplePayPaymentRequest>;

  /**
   * Gets the Apple Pay merchant session from the state.
   * @returns Observable emitting the PlaceOrderResponse for the merchant session.
   * @since 2211.43.0
   */
  abstract getMerchantSessionFromState(): Observable<PlaceOrderResponse>;

  /**
   * Gets the Apple Pay payment authorization from the state.
   * @returns Observable emitting the ApplePayAuthorization.
   * @since 2211.43.0
   */
  abstract getPaymentAuthorizationFromState(): Observable<ApplePayAuthorization>;

  /**
   * Checks checkout preconditions and returns user and cart IDs.
   * @returns Observable emitting a tuple of [userId, cartId].
   * @since 2211.43.0
   */
  abstract checkoutPreconditions(): Observable<[string, string]>;

  /**
   * Handles the merchant validation event from Apple Pay.
   * @param event Object containing the validationURL for merchant validation.
   * @since 2211.43.0
   */
  abstract onValidateMerchant(event: { validationURL: string }): void;

  /**
   * Handles the payment authorized event from Apple Pay.
   * @param event Object containing the payment data.
   * @since 2211.43.0
   */
  abstract onPaymentAuthorized(event: { payment: any }): void;

  /**
   * Handles payment error events from Apple Pay.
   * @since 2211.43.0
   */
  abstract onPaymentError(): void;

  /**
   * Sets the Apple Pay merchant session in the state.
   * @param paymentRequest The Apple Pay payment request object.
   * @since 2211.43.0
   */
  abstract setMerchantSession(paymentRequest: ApplePayPaymentRequest | null): void;

  /**
   * Sets the Apple Pay payment authorization in the state.
   * @param payment The payment authorization data.
   * @since 2211.43.0
   */
  abstract setPaymentAuthorization(payment: any): void;
}
