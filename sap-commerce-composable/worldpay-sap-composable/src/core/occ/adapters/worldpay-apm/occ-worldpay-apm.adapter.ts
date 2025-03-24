import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Cart } from '@spartacus/cart/base/root';
import { backOff, CmsComponent, ConverterService, isJaloError, LoggerService, OccEndpointsService, PaymentDetails, tryNormalizeHttpError } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { Observable, throwError } from 'rxjs';
import { catchError, pluck } from 'rxjs/operators';
import { WorldpayApmAdapter } from '../../../connectors/worldpay-apm/worldpay-apm.adapter';
import { ApmData, ApmPaymentDetails, APMRedirectRequestBody, APMRedirectResponse, WorldpayApmPaymentInfo } from '../../../interfaces';
import { APM_NORMALIZER } from '../../converters';

@Injectable()
export class OccWorldpayApmAdapter implements WorldpayApmAdapter {
  /**
   * Constructor for OccWorldpayApmAdapter
   *
   * @param http - HttpClient for making HTTP requests
   * @param occEndpoints - Service for building OCC endpoint URLs
   * @param converter - Service for converting data
   * @param loggerService - Service for logging errors
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
   *
   * This method sends a POST request to authorize an APM (Alternative Payment Method) redirect.
   * It constructs the request body with the payment method code and an optional shopper bank code.
   * The request URL is built using the user ID and cart ID.
   *
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param apm - APM payment details
   * @param save - Boolean flag to save the payment method
   * @returns Observable<APMRedirectResponse> - Observable emitting the APM redirect response
   * @since 4.3.6
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

    const url: string = this.occEndpoints.buildUrl(
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
   *
   * This method sends a GET request to retrieve all available Alternative Payment Methods (APMs) for a specific cart.
   * It constructs the request URL using the user ID and cart ID.
   * The response is normalized using the APM_NORMALIZER converter.
   *
   * @param userId - User ID
   * @param cartId - Cart ID
   * @returns Observable<ApmData[]> - Observable emitting the list of available APMs
   * @since 4.3.6
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
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      pluck('apmComponents'), //  Argument of type.
      this.converter.pipeableMany(APM_NORMALIZER)
    );
  }

  /**
   * Try placing the order after the user has been sent back from PSP to Spartacus
   *
   * This method sends a POST request to place an order after the user has been redirected back from the Payment Service Provider (PSP).
   * It constructs the request URL using the user ID and cart ID.
   * The response is handled with error normalization and retry logic for Jalo errors.
   *
   * @param userId - User ID
   * @param cartId - Cart ID
   * @returns Observable<Order> - Observable emitting the placed order
   * @since 4.3.6
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
      catchError((error: unknown) => throwError(() => tryNormalizeHttpError(error, this.loggerService))),
      backOff({
        shouldRetry: isJaloError,
      })
    );
  }

  /**
   * Try placing the order after the user has been sent back from PSP to Spartacus
   *
   * This method sends a POST request to place an order after the user has been redirected back from the Payment Service Provider (PSP).
   * It constructs the request URL using the user ID, cart ID, and order ID.
   * The response is handled with error normalization and retry logic for Jalo errors.
   *
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param orderId - Order ID
   * @returns Observable<Order> - Observable emitting the placed order
   * @since 4.3.6
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
      catchError((error: unknown) => throwError(() => tryNormalizeHttpError(error, this.loggerService))),
      backOff({
        shouldRetry: isJaloError,
      })
    );
  }

  /**
   * Set APM Payment Information
   *
   * This method sends a POST request to set the Alternative Payment Method (APM) payment information for a specific cart.
   * It constructs the request body with the billing address, APM name, and APM code.
   * The request URL is built using the user ID and cart ID.
   * The response is handled with error normalization and retry logic for Jalo errors.
   *
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param apmPaymentDetails - APM payment details including billing address, code, name, and optional shopper bank code
   * @returns Observable<Cart> - Observable emitting the updated cart
   * @since 4.3.6
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
    }: ApmPaymentDetails = apmPaymentDetails;

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
      catchError((error: unknown) => throwError(() => tryNormalizeHttpError(error, this.loggerService))),
      backOff({
        shouldRetry: isJaloError,
      })
    );
  }

  /**
   * Use existing payment details
   *
   * This method sends a PUT request to use existing payment details for a specific cart.
   * It constructs the request body with the provided payment details.
   * The request URL is built using the user ID, cart ID, and payment details ID.
   * The response is handled with error normalization and retry logic for Jalo errors.
   *
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param paymentDetails - Payment details to be used
   * @returns Observable<PaymentDetails> - Observable emitting the updated payment details
   * @since 4.3.6
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
    const body: PaymentDetails = { ...paymentDetails };

    return this.http.put<PaymentDetails>(url, body).pipe(
      catchError((error: unknown) => throwError(() => tryNormalizeHttpError(error, this.loggerService))),
      backOff({
        shouldRetry: isJaloError,
      })
    );
  }
}
