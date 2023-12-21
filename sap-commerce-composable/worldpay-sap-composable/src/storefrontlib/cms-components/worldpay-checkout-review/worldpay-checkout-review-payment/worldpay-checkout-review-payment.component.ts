import { Component, ViewEncapsulation } from '@angular/core';
import { PaymentDetails } from '@spartacus/cart/base/root';
import { combineLatest, Observable } from 'rxjs';
import { Card, ICON_TYPE } from '@spartacus/storefront';
import { filter, map } from 'rxjs/operators';
import { paymentMethodCard } from '@spartacus/order/root';
import { CheckoutStepType } from '@spartacus/checkout/base/root';
import { CheckoutStepService } from '@spartacus/checkout/base/components';
import { QueryState, TranslationService } from '@spartacus/core';
import { WorldpayCheckoutPaymentFacade } from '../../../../core/facade/worldpay-checkout-payment.facade';
import { generateBillingAddressCard } from '../../../../core/utils';
import { WorldpayApmPaymentInfo } from '../../../../core/interfaces';

@Component({
  selector: 'y-worldpay-checkout-review-payment',
  templateUrl: './worldpay-checkout-review-payment.component.html',
  styleUrls: ['./worldpay-checkout-review-payment.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class WorldpayCheckoutReviewPaymentComponent {

  iconTypes = ICON_TYPE;

  paymentDetailsStepRoute = this.checkoutStepService.getCheckoutStepRoute(
    CheckoutStepType.PAYMENT_DETAILS
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
   * Get payment details
   * @since 6.4.0
   */
  paymentDetails$: Observable<PaymentDetails | undefined> =
    this.checkoutPaymentFacade.getPaymentDetailsState().pipe(
      filter((state: QueryState<PaymentDetails>) => !state.loading && !state.error),
      map((state: QueryState<PaymentDetails>) => state.data)
    );

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
      map(([textTitle, textExpires]) =>
        paymentMethodCard(textTitle, textExpires, paymentDetails)
      )
    );
  }

  /**
   * Get payment details line translation
   * @since 6.4.0
   * @param paymentDetails WorldpayApmPaymentInfo
   */
  protected getPaymentDetailsLineTranslation(paymentDetails: WorldpayApmPaymentInfo): Observable<string> {
    let paymentDetailsTranslation;
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
      map(([billingAddress, billTo]) =>
        generateBillingAddressCard(billingAddress, billTo, paymentDetails.billingAddress)
      )
    );
  }
}
