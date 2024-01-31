/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, ViewEncapsulation } from '@angular/core';
import { OrderOverviewComponent } from '@spartacus/order/components';
import { combineLatest, Observable } from 'rxjs';
import { Card } from '@spartacus/storefront';
import { filter, map } from 'rxjs/operators';
import { DeliveryMode, PaymentDetails } from '@spartacus/cart/base/root';
import { deliveryAddressCard, deliveryModeCard, Order } from '@spartacus/order/root';
import { Address } from '@spartacus/core';

@Component({
  selector: 'y-worldpay-order-overview',
  templateUrl: './worldpay-order-overview.component.html',
  styleUrls: ['./worldpay-order-overview.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class WorldpayOrderOverviewComponent extends OrderOverviewComponent {

  /**
   * Get payment method card
   * @since 6.4.0
   * @param payment PaymentDetails
   */
  override isPaymentInfoCardFull(payment: PaymentDetails): boolean {
    return (
      !!payment?.cardNumber && !!payment?.expiryMonth && !!payment?.expiryYear
    );
  }

  /**
   * Get payment method card
   * @since 6.4.0
   * @param order any | Order
   */
  override getPaymentInfoCardContent(order: any): Observable<Card> {
    return combineLatest([
      this.translation.translate('paymentForm.payment'),
      this.getPaymentDetailsLineTranslation(order),
    ]).pipe(
      filter(() => Boolean(order)),
      map(([textTitle, textExpires]) => ({
        title: textTitle,
        textBold: order?.paymentInfo?.accountHolderName,
        text: [order?.paymentInfo?.cardNumber, textExpires],
      }))
    );
  }

  /**
   * Get payment details line translation
   * @since 6.4.0
   * @param order any | Order
   */
  getPaymentDetailsLineTranslation(order: Order): Observable<string> {
    let paymentDetailsTranslation: Observable<string>;
    if (order?.paymentInfo?.expiryYear) {
      paymentDetailsTranslation = this.translation.translate('paymentCard.expires', {
        month: order.paymentInfo.expiryMonth ?? '',
        year: order.paymentInfo.expiryYear ?? '',
      });
    } else if (order?.worldpayAPMPaymentInfo?.name) {
      paymentDetailsTranslation = this.translation.translate('paymentCard.apm',
        { apm: order?.worldpayAPMPaymentInfo?.name }
      );
    }
    return paymentDetailsTranslation;
  }

  /**
   * Get card for delivery address
   * @since 6.4.0
   * @param deliveryAddress Address
   * @param countryName string
   */
  getDeliveryAddressCard(
    deliveryAddress: Address,
    countryName?: string
  ): Observable<Card> {
    return combineLatest([
      this.translation.translate('addressCard.shipTo'),
      this.translation.translate('addressCard.phoneNumber'),
      this.translation.translate('addressCard.mobileNumber'),
    ]).pipe(
      map(([textTitle, textPhone, textMobile]) =>
        deliveryAddressCard(
          textTitle,
          textPhone,
          textMobile,
          deliveryAddress,
          countryName
        )
      )
    );
  }

  /**
   * Get card for delivery mode
   * @since 6.4.0
   * @param deliveryMode DeliveryMode
   */
  getDeliveryModeCard(deliveryMode: DeliveryMode): Observable<Card> {
    return combineLatest([
      this.translation.translate('checkoutMode.deliveryMethod'),
    ]).pipe(map(([textTitle]) => deliveryModeCard(textTitle, deliveryMode)));
  }
}
