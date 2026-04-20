import { Injectable } from '@angular/core';
import { CheckoutPaymentFacade } from '@spartacus/checkout/base/root';
import { Address, facadeFactory, PaymentDetails, QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import { ThreeDsDDCInfo, ThreeDsInfo, WorldpayApmPaymentInfo } from '../interfaces';
import { WORLDPAY_CHECKOUT_PAYMENT_FEATURE } from './worldpay-feature-name';

@Injectable({
  providedIn: 'root',
  useFactory: (): WorldpayCheckoutPaymentFacade =>
    facadeFactory({
      facade: WorldpayCheckoutPaymentFacade,
      feature: WORLDPAY_CHECKOUT_PAYMENT_FEATURE,
      methods: [
        'getPaymentCardTypesState',
        'getPaymentCardTypes',
        'setPaymentDetails',
        'getPublicKey',
        'createPaymentDetails',
        'useExistingPaymentDetails',
        'setPaymentAddress',
        'getPaymentDetailsState',
        // Added methods
        'generatePublicKey',
        'getThreeDsDDCJwt',
        'getCseTokenFromState',
        'getThreeDsChallengeInfoFromState',
        'getThreeDsDDCIframeUrlFromState',
        'getThreeDsChallengeIframeUrlFromState',
        'getPublicKeyFromState',
        'setPublicKey',
        'generateCseToken',
        'setCseToken',
        'getSerializedUrl',
        'setThreeDsDDCIframeUrl',
        'setThreeDsChallengeIframeUrl',
        'setThreeDsChallengeInfo',
        'listenSetThreeDsDDCInfoEvent',
        'setThreeDsDDCInfo',
        'getDDCInfoFromState',
        'setSaveCreditCardValue',
        'getSaveCreditCardValueFromState',
        'setSaveAsDefaultCardValue',
        'getSaveAsDefaultCardValueFromState',
        'getSelectedPaymentTypeState',
      ],
    }),
})
export abstract class WorldpayCheckoutPaymentFacade extends CheckoutPaymentFacade {
  /**
   * Abstract method used to get the public key for Worldpay CSE
   * @since 6.4.0
   */
  abstract getPublicKey(): Observable<QueryState<string>>;

  /**
   * Abstract method used to use existing payment details
   * @since 6.4.0
   * @param paymentDetails
   */
  abstract useExistingPaymentDetails(paymentDetails: PaymentDetails): Observable<unknown>;

  /**
   * Abstract method used to set the payment address
   * @since 6.4.0
   * @param address
   */
  abstract setPaymentAddress(address: Address): Observable<Address>;

  /**
   * Abstract method used to get the payment details state
   * @since 6.4.2
   */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  abstract override getPaymentDetailsState(): Observable<QueryState<WorldpayApmPaymentInfo | undefined>>;

  /**
   * Generates the public key for Worldpay CSE.
   * @since 6.4.0
   */
  abstract generatePublicKey(): Observable<string>;

  /**
   * Gets the ThreeDsDDC JWT state.
   * @since 4.3.6
   */
  abstract getThreeDsDDCJwt(): Observable<QueryState<ThreeDsDDCInfo>>;

  /**
   * Gets the CSE token from state.
   * @since 6.4.0
   */
  abstract getCseTokenFromState(): Observable<string>;

  /**
   * Gets the ThreeDs challenge info from state.
   * @since 6.4.0
   */
  abstract getThreeDsChallengeInfoFromState(): Observable<ThreeDsInfo>;

  /**
   * Gets the ThreeDs DDC iframe URL from state.
   * @since 6.4.0
   */
  abstract getThreeDsDDCIframeUrlFromState(): Observable<unknown>;

  /**
   * Gets the ThreeDs challenge iframe URL from state.
   * @since 6.4.0
   */
  abstract getThreeDsChallengeIframeUrlFromState(): Observable<unknown>;

  /**
   * Gets the public key from state.
   * @since 6.4.0
   */
  abstract getPublicKeyFromState(): Observable<string>;

  /**
   * Sets the public key for Worldpay CSE.
   * @since 6.4.0
   */
  abstract setPublicKey(publicKey: string): void;

  /**
   * Generates a CSE token from payment details.
   * @param paymentDetails The payment details to generate the CSE token from.
   * @returns The generated CSE token as a string.
   * @since 2211.43.0
   */
  abstract generateCseToken(paymentDetails: PaymentDetails): Observable<string>;

  /**
   * Sets the CSE token in state.
   * @param cseToken The CSE token string to be set in state.
   * @since 2211.43.0
   */
  abstract setCseToken(cseToken: string): void;

  /**
   * Gets a serialized URL with parameters as an Observable.
   * @param url The base URL to serialize.
   * @param params An object containing key-value pairs to be serialized into the URL.
   * @returns An Observable that emits the serialized URL as a string.
   * @since 2211.43.0
   */
  abstract getSerializedUrl(url: string, params: { [key: string]: string }): Observable<string>;

  /**
   * Sets the ThreeDs DDC iframe URL.
   * @param ddcUrl The URL for the DDC iframe.
   * @param cardNumber The card number associated with the DDC process.
   * @param jwt The JWT token for authentication/authorization purposes.
   * @since 6.4.0
   */
  abstract setThreeDsDDCIframeUrl(ddcUrl: string, cardNumber: string, jwt: string): void;

  /**
   * Sets the ThreeDs challenge iframe URL.
   * @since 6.4.0
   */
  abstract setThreeDsChallengeIframeUrl(challengeUrl: string, jwt: string, merchantData: string): void;

  /**
   * Sets the ThreeDs challenge info.
   * @since 6.4.0
   */
  abstract setThreeDsChallengeInfo(value: ThreeDsInfo): void;

  /**
   * Listens for the SetThreeDsDDCInfo event.
   * @since 6.4.0
   */
  abstract listenSetThreeDsDDCInfoEvent(): void;

  /**
   * Sets the ThreeDsDDCInfo value.
   * @param value The ThreeDsDDCInfo object containing the DDC information to be set in state.
   * @since 6.4.0
   */
  abstract setThreeDsDDCInfo(value: ThreeDsDDCInfo): void;

  /**
   * Gets the ThreeDsDDCInfo from state.
   * @returns An Observable that emits the ThreeDsDDCInfo object from state.
   * @since 6.4.0
   */
  abstract getDDCInfoFromState(): Observable<ThreeDsDDCInfo>;

  /**
   * Sets the save credit card value.
   * @param saveCreditCard A boolean value indicating whether to save the credit card or not.
   * @since 6.4.0
   */
  abstract setSaveCreditCardValue(saveCreditCard: boolean): void;

  /**
   * Gets the save credit card value from state.
   * @since 6.4.0
   */
  abstract getSaveCreditCardValueFromState(): Observable<boolean>;

  /**
   * Sets the save as default credit card value.
   */
  abstract setSaveAsDefaultCardValue(saveAsDefaultCreditCard: boolean): void;

  /**
   * Gets the save as default credit card value from state.
   * @since 6.4.0
   */
  abstract getSaveAsDefaultCardValueFromState(): Observable<boolean>;

  /**
   * Gets the selected payment type state.
   * @since 6.4.0
   */
  abstract getSelectedPaymentTypeState(): Observable<QueryState<unknown>>;
}
