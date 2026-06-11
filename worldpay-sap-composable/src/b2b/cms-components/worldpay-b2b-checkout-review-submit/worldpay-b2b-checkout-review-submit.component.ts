import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, ViewEncapsulation } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { B2BCheckoutReviewSubmitComponent } from '@spartacus/checkout/b2b/components';
import { CheckoutCostCenterFacade, CheckoutPaymentTypeFacade, } from '@spartacus/checkout/b2b/root';
import { CheckoutStepService, } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressFacade, CheckoutDeliveryModesFacade, } from '@spartacus/checkout/base/root';
import { PaymentDetails, QueryState, TranslatePipe, TranslationService, UrlPipe, UserCostCenterService } from '@spartacus/core';
import { billingAddressCard } from '@spartacus/order/root';
import { Card, CardComponent, IconComponent, OutletDirective, PromotionsComponent } from '@spartacus/storefront';
import { combineLatest, Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { WorldpayApmPaymentInfo, WorldpayCheckoutPaymentFacade } from '../../../core';

/* eslint-disable @angular-eslint/prefer-inject */
@Component({
  selector: 'y-worldpay-b2b-review-submit',
  templateUrl: './worldpay-b2b-checkout-review-submit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  imports: [
    NgTemplateOutlet,
    CardComponent,
    RouterLink,
    IconComponent,
    OutletDirective,
    PromotionsComponent,
    AsyncPipe,
    TranslatePipe,
    UrlPipe,
  ],
})
export class WorldpayB2BCheckoutReviewSubmitComponent extends B2BCheckoutReviewSubmitComponent {

  override paymentDetails$: Observable<WorldpayApmPaymentInfo | undefined> =
    this.checkoutPaymentFacade.getPaymentDetailsState().pipe(
      filter((state: QueryState<WorldpayApmPaymentInfo>): boolean => !state.loading && !state.error),
      map((state: QueryState<WorldpayApmPaymentInfo>): PaymentDetails => state.data)
    );

  constructor(
    protected override checkoutDeliveryAddressFacade: CheckoutDeliveryAddressFacade,
    protected override checkoutPaymentFacade: WorldpayCheckoutPaymentFacade,
    protected override activeCartFacade: ActiveCartFacade,
    protected override translationService: TranslationService,
    protected override checkoutStepService: CheckoutStepService,
    protected override checkoutDeliveryModesFacade: CheckoutDeliveryModesFacade,
    protected override checkoutPaymentTypeFacade: CheckoutPaymentTypeFacade,
    protected override checkoutCostCenterFacade: CheckoutCostCenterFacade,
    protected override userCostCenterService: UserCostCenterService
  ) {
    super(
      checkoutDeliveryAddressFacade,
      checkoutPaymentFacade,
      activeCartFacade,
      translationService,
      checkoutStepService,
      checkoutDeliveryModesFacade,
      checkoutPaymentTypeFacade,
      checkoutCostCenterFacade,
      userCostCenterService,
    );
  }

  public override get paymentType$(): Observable<WorldpayApmPaymentInfo | undefined> {
    return this.checkoutPaymentFacade.getPaymentDetailsState().pipe(
      filter((state: QueryState<WorldpayApmPaymentInfo>): boolean => !state.loading && !state.error),
      map((state: QueryState<WorldpayApmPaymentInfo>): WorldpayApmPaymentInfo => state.data)
    );
  }

  public override getPaymentTypeCard(paymentDetails: WorldpayApmPaymentInfo): Observable<Card> {
    let getPaymentTypeTranslation: Observable<string> = this.translationService.translate('paymentTypes.paymentType_' + paymentDetails.code);
    if (paymentDetails.apmCode) {
      getPaymentTypeTranslation = of(paymentDetails.name || paymentDetails.apmName);
    }

    return combineLatest([
      this.translationService.translate('checkoutB2B.progress.methodOfPayment'),
      getPaymentTypeTranslation,
    ]).pipe(
      map(([textTitle, paymentTypeTranslation]: [string, string]): Card => ({
        title: textTitle,
        textBold: paymentTypeTranslation,
      }))
    );
  }

  /**
   * Get payment method card
   * @since 2211.32.1
   * @param paymentDetails WorldpayApmPaymentInfo
   */
  public override getPaymentMethodCard(paymentDetails: WorldpayApmPaymentInfo): Observable<Card> {
    let getPaymentTypeTranslation: Observable<string> = this.getPaymentDetailsLineTranslation(paymentDetails);
    let getTitleTranslation: string = 'paymentForm.payment';

    if (paymentDetails?.apmCode) {
      getPaymentTypeTranslation = of('');
      getTitleTranslation = 'paymentForm.billingAddress';
    }

    return combineLatest([
      this.translationService.translate(getTitleTranslation),
      getPaymentTypeTranslation
    ]).pipe(
      map(([textTitle, textExpires]: [string, string]): Card =>
        billingAddressCard(textTitle, textExpires, paymentDetails)
      )
    );
  }

  /**
   * Get payment details line translation
   * @since 2211.32.1
   * @param paymentDetails WorldpayApmPaymentInfo
   */
  public getPaymentDetailsLineTranslation(paymentDetails: WorldpayApmPaymentInfo): Observable<string> {
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
