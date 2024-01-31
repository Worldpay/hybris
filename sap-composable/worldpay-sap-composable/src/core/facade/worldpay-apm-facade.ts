import { Injectable } from '@angular/core';
import { facadeFactory, QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import { WORLDPAY_APM_FEATURE } from './worldpay-feature-name';
import { ApmData, ApmPaymentDetails, APMRedirectResponse, PaymentMethod } from '../interfaces';
import { Cart } from '@spartacus/cart/base/root';

@Injectable({
  providedIn: 'root',
  useFactory: () =>
    facadeFactory({
      facade: WorldpayApmFacade,
      feature: WORLDPAY_APM_FEATURE,
      methods: [
        'getLoading',
        'getApmComponentById',
        'selectAPM',
        'getSelectedAPMFromState',
        'getWorldpayAPMRedirectUrl',
        'getWorldpayAvailableApms',
        'getWorldpayAvailableApmsLoading',
        'requestAvailableApmsState',
        'setApmPaymentDetails',
        'getAPMRedirectUrl',
      ],
    }),
})
export abstract class WorldpayApmFacade {

  /**
   * Abstract method used to get loading state
   * @since 6.4.0
   */
  abstract getLoading(): Observable<boolean>;

  /**
   * Method used to get Apm Component by Id
   * @since 4.3.6
   * @param componentUid
   * @param code
   */
  abstract getApmComponentById(componentUid: string, code: PaymentMethod): Observable<ApmData>;

  /**
   * Method used to set APM Payment Details
   * @since 4.3.6
   * @param apm ApmPaymentDetails
   */
  abstract selectAPM(apm: ApmPaymentDetails): void;

  /**
   * Method used to get selected APM from state
   * @since 4.3.6
   */
  abstract getSelectedAPMFromState(): Observable<ApmPaymentDetails>;

  /**
   * Method used to get Worldpay Redirect URL
   * @since 4.3.6
   * @param apm
   * @param save
   */
  abstract getWorldpayAPMRedirectUrl(apm: ApmPaymentDetails, save: boolean): Observable<APMRedirectResponse>;

  /**
   * Method used to request Available Apms from State
   * @since 6.4.0
   */
  abstract requestAvailableApmsState(): Observable<QueryState<ApmData[]>>;

  /**
   * Method used to get Worldpay Available APMs loading status
   * @since 6.4.0
   */
  abstract getWorldpayAvailableApmsLoading(): Observable<boolean>;

  /**
   * Method used to get Worldpay Available APMs
   * @since 4.3.6
   */
  abstract getWorldpayAvailableApms(): Observable<ApmData[]>;

  /**
   * Method used to set APM Payment Details
   * @since 4.3.6
   * @param apmPaymentDetails
   */
  abstract setApmPaymentDetails(apmPaymentDetails: ApmPaymentDetails): Observable<Cart>;

  /**
   * Abstract method used to get APM redirect url
   * @since 6.4.0
   * @param apm ApmData
   * @param save boolean
   */
  abstract getAPMRedirectUrl(apm: ApmData, save: boolean): void;
}
