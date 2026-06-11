import { AsyncPipe } from '@angular/common';
import { Component, inject, ViewEncapsulation } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CheckoutStepService } from '@spartacus/checkout/base/components';
import { CheckoutStepType } from '@spartacus/checkout/base/root';
import { PaymentDetails, QueryState, TranslatePipe, TranslationService, UrlPipe } from '@spartacus/core';
import { paymentMethodCard } from '@spartacus/order/root';
import { Card, CardComponent, ICON_TYPE, IconComponent } from '@spartacus/storefront';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { generateBillingAddressCard, WorldpayApmPaymentInfo, WorldpayCheckoutPaymentFacade } from '../../../../core';

@Component({
  selector: 'y-worldpay-checkout-review-payment',
  templateUrl: './worldpay-checkout-review-payment.component.html',
  encapsulation: ViewEncapsulation.None,
  imports: [
    CardComponent,
    RouterLink,
    IconComponent,
    AsyncPipe,
    TranslatePipe,
    UrlPipe
  ]
})
export class WorldpayCheckoutReviewPaymentComponent {
  public iconTypes: typeof ICON_TYPE = ICON_TYPE;
  protected checkoutStepService: CheckoutStepService = inject(CheckoutStepService);
  public paymentDetailsStepRoute: string = this.checkoutStepService.getCheckoutStepRoute(
    CheckoutStepType.PAYMENT_DETAILS
  );
  protected checkoutPaymentFacade: WorldpayCheckoutPaymentFacade = inject(WorldpayCheckoutPaymentFacade);
  /**
   * Get payment details
   * @since 6.4.0
   */
  paymentDetails$: Observable<PaymentDetails | undefined> =
    this.checkoutPaymentFacade.getPaymentDetailsState().pipe(
      filter((state: QueryState<PaymentDetails>): boolean => !state.loading && !state.error),
      map((state: QueryState<PaymentDetails>): PaymentDetails => state.data)
    );
  protected translationService: TranslationService = inject(TranslationService);

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
