import { Injectable } from '@angular/core';
import { Cart } from '@spartacus/cart/base/root';
import { facadeFactory, HttpErrorModel, QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import { ApmData, ApmPaymentDetails, APMRedirectResponse, PaymentMethod } from '../interfaces';
import { WORLDPAY_APM_FEATURE } from './worldpay-feature-name';

@Injectable({
  providedIn: 'root',
  useFactory: (): WorldpayApmFacade =>
    facadeFactory({
      facade: WorldpayApmFacade,
      feature: WORLDPAY_APM_FEATURE,
      methods: [
        'resetSelectedAPMEvent',
        'checkoutPreconditions',
        'getSaveApm',
        'setSaveApm',
        'getLoading',
        'getApmComponentById',
        'selectAPM',
        'getSelectedAPMFromState',
        'getSelectedAPMEvent',
        'getWorldpayAPMRedirectUrl',
        'setWorldpayAPMRedirectUrl',
        'setWorldpayAPMRedirectUrlEvent',
        'getWorldpayAPMRedirectUrlFromState',
        'getWorldpayAvailableApms',
        'getWorldpayAvailableApmsLoading',
        'requestAvailableApmsState',
        'setApmPaymentDetails',
        'getAPMRedirectUrl',
        'showErrorMessage',
        'setWorldpaySavedCreditCardEvent',
        'setWorldpaySaveAsDefaultCreditCardEvent'
      ],
    }),
})
export abstract class WorldpayApmFacade {
  /**
   * Resets the selected APM event in the state.
   * @since 2211.43.0
   */
  abstract resetSelectedAPMEvent(): void;

  /**
   * Returns an observable with the userId and cartId required for checkout operations.
   * @since 2211.43.0
   */
  abstract checkoutPreconditions(): Observable<[string, string]>;

  /**
   * Returns an observable indicating whether the APM should be saved for future use.
   * @since 2211.43.0
   */
  abstract getSaveApm(): Observable<boolean>;

  /**
   * Sets the flag to save the APM for future use.
   * @param value Boolean indicating whether to save the APM
   * @since 2211.43.0
   */
  abstract setSaveApm(value: boolean): void;

  /**
   * Returns an observable indicating the loading state for APM operations.
   * @since 6.4.0
   */
  abstract getLoading(): Observable<boolean>;

  /**
   * Retrieves the APM component data by its unique identifier and payment method code.
   * @param componentUid Unique identifier for the component
   * @param code Payment method code
   * @returns Observable emitting the APM component data
   * @since 4.3.6
   */
  abstract getApmComponentById(componentUid: string, code: PaymentMethod): Observable<ApmData>;

  /**
   * Selects the given APM as the current payment method.
   * @param apm The APM payment details to select
   * @since 4.3.6
   */
  abstract selectAPM(apm: ApmPaymentDetails): void;

  /**
   * Returns an observable of the currently selected APM payment details from the state.
   * @since 4.3.6
   */
  abstract getSelectedAPMFromState(): Observable<ApmPaymentDetails>;

  /**
   * Returns an observable that emits when the selected APM event occurs.
   * @since 6.4.0
   */
  abstract getSelectedAPMEvent(): Observable<ApmData>;

  /**
   * Requests a redirect URL for the given APM and save flag.
   * @param apm The APM payment details
   * @param save Boolean indicating whether to save the APM
   * @returns Observable emitting the APM redirect response
   * @since 4.3.6
   */
  abstract getWorldpayAPMRedirectUrl(apm: ApmPaymentDetails, save: boolean): Observable<APMRedirectResponse>;

  /**
   * Sets the Worldpay APM redirect URL in the state.
   * @param apmRedirectUrl The APM redirect response to set
   * @since 6.4.0
   */
  abstract setWorldpayAPMRedirectUrl(apmRedirectUrl: APMRedirectResponse): void;

  /**
   * Dispatches an event to set the Worldpay APM redirect URL.
   * @param apmRedirectUrl The APM redirect response to dispatch
   * @since 6.4.0
   */
  abstract setWorldpayAPMRedirectUrlEvent(apmRedirectUrl: APMRedirectResponse): void;

  /**
   * Returns an observable of the Worldpay APM redirect URL from the state.
   * @since 6.4.0
   */
  abstract getWorldpayAPMRedirectUrlFromState(): Observable<APMRedirectResponse>;

  /**
   * Requests the available APMs and returns their query state as an observable.
   * @returns Observable emitting the query state of available APMs
   * @since 6.4.0
   */
  abstract requestAvailableApmsState(): Observable<QueryState<ApmData[]>>;

  /**
   * Returns an observable indicating the loading status for available APMs.
   * @since 6.4.0
   */
  abstract getWorldpayAvailableApmsLoading(): Observable<boolean>;

  /**
   * Returns an observable of the available Worldpay APMs.
   * @since 4.3.6
   */
  abstract getWorldpayAvailableApms(): Observable<ApmData[]>;

  /**
   * Sets the payment details for the selected APM and returns the updated cart as an observable.
   * @param apmPaymentDetails The APM payment details to set
   * @returns Observable emitting the updated cart
   * @since 4.3.6
   */
  abstract setApmPaymentDetails(apmPaymentDetails: ApmPaymentDetails): Observable<Cart>;

  /**
   * Initiates the process to retrieve the redirect URL for the given APM.
   * @param apm The APM data for which to get the redirect URL
   * @since 6.4.0
   */
  abstract getAPMRedirectUrl(apm: ApmData): void;

  /**
   * Displays an error message using the GlobalMessageService.
   * @param error The error model to display
   * @since 6.4.0
   */
  abstract showErrorMessage(error: HttpErrorModel): void;

  /**
   * Dispatches an event to set the Worldpay saved credit card flag.
   * @since 2211.43.0
   */
  abstract setWorldpaySavedCreditCardEvent(): void;

  /**
   * Dispatches an event to set the Worldpay save as default credit card flag.
   * @since 2211.43.0
   */
  abstract setWorldpaySaveAsDefaultCreditCardEvent(): void;
}
