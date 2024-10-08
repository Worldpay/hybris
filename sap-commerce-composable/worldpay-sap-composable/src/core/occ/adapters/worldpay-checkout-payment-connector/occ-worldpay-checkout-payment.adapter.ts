import { Injectable } from '@angular/core';
import { PAYMENT_DETAILS_SERIALIZER } from '@spartacus/checkout/base/core';
import { OccCheckoutPaymentAdapter } from '@spartacus/checkout/base/occ';
import { backOff, isJaloError, normalizeHttpError, PaymentDetails } from '@spartacus/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { WorldpayCheckoutPaymentAdapter } from '../../../connectors/worldpay-payment-connector/worldpay-checkout-payment.adapter';

@Injectable()
export class OccWorldpayCheckoutPaymentAdapter extends OccCheckoutPaymentAdapter implements WorldpayCheckoutPaymentAdapter {

  /**
   * Method used to create Worldpay Payment Details.
   * @since 4.3.6
   * @param userId - User ID
   * @param cartId
   * @param paymentDetails
   * @param cseToken
   */
  createWorldpayPaymentDetails(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails,
    cseToken: string
  ): Observable<PaymentDetails> {
    let body = {
      ...paymentDetails,
      cseToken
    };
    delete body.cardNumber;
    delete body.dateOfBirth;
    body = this.converter.convert(body, PAYMENT_DETAILS_SERIALIZER);
    const url: string = this.occEndpoints.buildUrl(
      'createWorldpayPaymentDetails',
      {
        urlParams: {
          userId,
          cartId
        }
      }
    );
    return this.http.post<PaymentDetails>(url, body, {}).pipe(
      catchError((error: unknown) => throwError(() => normalizeHttpError(error, this.logger))),
      backOff({
        shouldRetry: isJaloError,
      }),
    );
  }

  /**
   * Method used to use Existing Payment Details.
   * @since 4.3.6
   * @param userId - User ID
   * @param cartId
   * @param paymentDetails
   */
  public useExistingPaymentDetails(
    userId: string,
    cartId: string,
    paymentDetails: PaymentDetails
  ): Observable<PaymentDetails> {

    const url = this.occEndpoints.buildUrl(
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
      catchError((error: unknown) => throwError(() => normalizeHttpError(error, this.logger))),
      backOff({
        shouldRetry: isJaloError,
      }),
    );
  }
}
