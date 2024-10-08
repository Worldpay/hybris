/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, ViewEncapsulation } from '@angular/core';
import { PaymentDetails } from '@spartacus/core';
import { OrderDetailBillingComponent } from '@spartacus/order/components';
import { Card } from '@spartacus/storefront';
import { combineLatest, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { generateBillingAddressCard } from '../../../../core/utils/format-address';

@Component({
  selector: 'y-worldpay-order-detail-billing',
  templateUrl: './worldpay-order-details-billing.component.html',
  styleUrls: ['./worldpay-order-details-billing.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class WorldpayOrderDetailsBillingComponent extends OrderDetailBillingComponent {

  /**
   * Get payment method card
   * @since 6.4.0
   * @param order any | Order
   */
  override getPaymentMethodCard(order: any): Observable<Card> {
    return combineLatest([
      this.translationService.translate('paymentForm.payment'),
      this.getPaymentDetailsLineTranslation(order),
    ]).pipe(
      map(([textTitle, textExpires]) => ({
        title: textTitle,
        textBold: order?.paymentInfo?.accountHolderName,
        text: [
          order?.paymentInfo?.cardNumber,
          textExpires
        ],
      }) as Card)
    );
  }

  /**
   * Get payment details line translation
   * @since 6.4.0
   * @param order any | Order
   */
  getPaymentDetailsLineTranslation(order: any): Observable<string> {
    let paymentDetailsTranslation: Observable<string>;
    if (order?.paymentInfo?.expiryYear) {
      paymentDetailsTranslation = this.translationService.translate('paymentCard.expires', {
        month: order.paymentInfo.expiryMonth ?? '',
        year: order.paymentInfo.expiryYear ?? '',
      });
    } else if (order?.worldpayAPMPaymentInfo?.name) {
      paymentDetailsTranslation =
        this.translationService.translate(
          'paymentCard.apm', {
            apm: order?.worldpayAPMPaymentInfo?.name
          }
        );
    }
    return paymentDetailsTranslation;
  }

  /**
   * Get card for billing address
   * @since 6.4.0
   * @param paymentDetails PaymentDetails
   */
  override getBillingAddressCard(paymentDetails: PaymentDetails): Observable<Card> {
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
