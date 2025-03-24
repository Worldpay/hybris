import { Component, ViewEncapsulation } from '@angular/core';
import { CheckoutStepService } from '@spartacus/checkout/base/components';
import { CheckoutStepType } from '@spartacus/checkout/base/root';
import { PaymentDetails, QueryState, TranslationService } from '@spartacus/core';
import { paymentMethodCard } from '@spartacus/order/root';
import { Card, ICON_TYPE } from '@spartacus/storefront';
import { WorldpayCheckoutPaymentFacade } from '@worldpay-facade/worldpay-checkout-payment.facade';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { WorldpayApmPaymentInfo } from '../../../../core/interfaces';
import { generateBillingAddressCard } from '../../../../core/utils';

@Component({
  selector: 'y-worldpay-checkout-review-payment',
  templateUrl: './worldpay-checkout-review-payment.component.html',
  encapsulation: ViewEncapsulation.None,
})
export class WorldpayCheckoutReviewPaymentComponent {

  iconTypes: typeof ICON_TYPE = ICON_TYPE;

  paymentDetailsStepRoute: string = this.checkoutStepService.getCheckoutStepRoute(
    CheckoutStepType.PAYMENT_DETAILS
  );
  /**
   * Get payment details
   * @since 6.4.0
   */
  paymentDetails$: Observable<PaymentDetails | undefined> =
    this.checkoutPaymentFacade.getPaymentDetailsState().pipe(
      filter((state: QueryState<PaymentDetails>) => !state.loading && !state.error),
      map((state: QueryState<PaymentDetails>) => state.data)
    );

  /**
   * Constructor
   * @since 6.4.0
   * @param checkoutStepService CheckoutStepService
   * @param checkoutPaymentFacade WorldpayCheckoutPaymentFacade
   * @param translationService TranslationService
   */
  constructor(
    protected checkoutStepService: CheckoutStepService,
    protected checkoutPaymentFacade: WorldpayCheckoutPaymentFacade,
    protected translationService: TranslationService
  ) {
  }

  /**
   * Get payment method card
   * @since 6.4.0
   * @param paymentDetails WorldpayApmPaymentInfo
   */
  getPaymentMethodCard(paymentDetails: WorldpayApmPaymentInfo): Observable<Card> {
    return combineLatest([
      this.translationService.translate(paymentDetails.apmCode ? 'paymentForm.paymentApm' : 'paymentForm.payment'),
      this.getPaymentDetailsLineTranslation(paymentDetails),
    ]).pipe(
      map(([textTitle, textExpires]: [string, string]): Card =>
        paymentMethodCard(textTitle, textExpires, paymentDetails)
      )
    );
  }

  /**
   * Get card for billing address
   * @since 6.4.0
   * @param paymentDetails PaymentDetails
   */
  getBillingAddressCard(paymentDetails: PaymentDetails): Observable<Card> {
    return combineLatest([
      this.translationService.translate('paymentForm.billingAddress'),
      this.translationService.translate('addressCard.billTo'),
    ]).pipe(
      map(([billingAddress, billTo]: [string, string]): Card =>
        generateBillingAddressCard(billingAddress, billTo, paymentDetails.billingAddress)
      )
    );
  }

  /**
   * Get payment details line translation
   * @since 6.4.0
   * @param paymentDetails WorldpayApmPaymentInfo
   */
  protected getPaymentDetailsLineTranslation(paymentDetails: WorldpayApmPaymentInfo): Observable<string> {
    let paymentDetailsTranslation: Observable<string>;
    if (paymentDetails.expiryYear) {
      paymentDetailsTranslation = this.translationService.translate('paymentCard.expires', {
        month: paymentDetails.expiryMonth,
        year: paymentDetails.expiryYear,
      });
    } else {
      paymentDetailsTranslation = this.translationService.translate('paymentCard.apm', { apm: paymentDetails.name });
    }
    return paymentDetailsTranslation;
  }
}
