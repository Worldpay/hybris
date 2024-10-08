import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Cart } from '@spartacus/cart/base/root';
import { backOff, CmsComponent, ConverterService, isJaloError, LoggerService, normalizeHttpError, OccEndpointsService, PaymentDetails } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Observable, throwError } from 'rxjs';
import { catchError, pluck } from 'rxjs/operators';
import { WorldpayApmAdapter } from '../../../connectors/worldpay-apm/worldpay-apm.adapter';
import { ApmData, ApmPaymentDetails, APMRedirectRequestBody, APMRedirectResponse, WorldpayApmPaymentInfo } from '../../../interfaces';
import { APM_NORMALIZER } from '../../converters';

@Injectable()
export class OccWorldpayApmAdapter implements WorldpayApmAdapter {
  /**
   * Constructor
   * @param http
   * @param occEndpoints
   * @param converter
   * @param loggerService
   */
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected converter: ConverterService,
    protected loggerService: LoggerService
  ) {
  }

  /**
   * Redirect authorise APM payment
   * @since 4.3.6
   * @param userId - User ID
   * @param cartId
   * @param apm
   * @param save
   */
  authoriseApmRedirect(
    userId: string,
    cartId: string,
    apm: ApmPaymentDetails,
    save: boolean
  ): Observable<APMRedirectResponse> {
    const body: APMRedirectRequestBody = {
      paymentMethod: apm.code,
      save,
    };

    if (apm.shopperBankCode) {
      body.shopperBankCode = apm.shopperBankCode;
    }

    const url = this.occEndpoints.buildUrl(
      'authoriseApmRedirect',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.post<APMRedirectResponse>(
      url,
      body,
      {}
    );
  }

  /**
   * Get a list of all available APM's for the given cart
   * @since 4.3.6
   * @param userId - User ID
   * @param cartId
   */
  getAvailableApms(
    userId: string,
    cartId: string
  ): Observable<ApmData[]> {
    const url: string = this.occEndpoints.buildUrl(
      'getAvailableApms',
      {
        urlParams: {
          userId,
          cartId
        }
      });
    return this.http.get<CmsComponent[]>(
      url,
    ).pipe(
      // @ts-ignore
      pluck('apmComponents'),
      this.converter.pipeableMany(APM_NORMALIZER)
    );
  }

  /**
   * Try placing the order after the user has been sent back from PSP to Spartacus
   * @since 4.3.6
   * @param userId - User ID
   * @param cartId
   */
  placeRedirectOrder(
    userId: string,
    cartId: string
  ): Observable<Order> {
    const url: string = this.occEndpoints.buildUrl(
      'placeRedirectOrder',
      {
        urlParams: {
          userId,
          cartId,
        }
      }
    );
    return this.http.post<Order>(url, {}).pipe(
      catchError((error: unknown) => throwError(() => normalizeHttpError(error, this.loggerService))),
      backOff({
        shouldRetry: isJaloError,
      })
    );
  }

  /**
   * Try placing the order after the user has been sent back from PSP to Spartacus
   * @since 4.3.6
   * @param userId - User ID
   * @param cartId
   * @param orderId
   */
  placeBankTransferOrderRedirect(
    userId: string,
    cartId: string,
    orderId: string
  ): Observable<Order> {
    const url: string = this.occEndpoints.buildUrl(
      'placeBankTransferRedirectOrder',
      {
        urlParams: {
          userId,
          cartId,
        },
        queryParams: {
          orderId
        }
      }
    );
    return this.http.post<Order>(url, {}).pipe(
      catchError((error: unknown) => throwError(() => normalizeHttpError(error, this.loggerService))),
      backOff({
        shouldRetry: isJaloError,
      })
    );
  }

  /**
   * Set APM Payment Information
   * @since 4.3.6
   * @param userId - User ID
   * @param cartId
   * @param apmPaymentDetails
   */
  setAPMPaymentInfo(
    userId: string,
    cartId: string,
    apmPaymentDetails: ApmPaymentDetails
  ): Observable<Cart> {

    const {
      billingAddress,
      code,
      name,
      shopperBankCode
    } = apmPaymentDetails;

    const body: WorldpayApmPaymentInfo = {
      billingAddress,
      apmName: shopperBankCode || name,
      apmCode: code
    };

    const url: string = this.occEndpoints.buildUrl('setAPMPaymentInfo', {
      urlParams: {
        userId,
        cartId,
      },
    });

    return this.http.post<Cart>(url, body, {}).pipe(
      catchError((error: unknown) => throwError(() => normalizeHttpError(error, this.loggerService))),
      backOff({
        shouldRetry: isJaloError,
      })
    );
  }

  /**
   * Use existing payment details
   * @since 4.3.6
   * @param userId - User ID
   * @param cartId
   * @param paymentDetails
   */
  useExistingPaymentDetails(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails
  ): Observable<PaymentDetails> {

    const url: string = this.occEndpoints.buildUrl(
      'useExistingPaymentDetails',
      {
        urlParams: {
          userId,
          cartId,
        },
        queryParams: {
          paymentDetailsId: paymentDetails.id
        }
      });
    const body = { ...paymentDetails };

    return this.http.put<PaymentDetails>(url, body).pipe(
      catchError((error: unknown) => throwError(() => normalizeHttpError(error, this.loggerService))),
      backOff({
        shouldRetry: isJaloError,
      })
    );
  }
}
