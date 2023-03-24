import { ChangeDetectionStrategy, Component, ViewEncapsulation } from '@angular/core';
import { Card, OrderOverviewComponent } from '@spartacus/storefront';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
  selector: 'y-worldpay-order-overview',
  templateUrl: './worldpay-order-overview.component.html',
  styleUrls: ['./worldpay-order-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
})
export class WorldpayOrderOverviewComponent extends OrderOverviewComponent {
  getPaymentInfoCardContent(order: any): Observable<Card> {
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

  getPaymentDetailsLineTranslation(order: any): any {
    let paymentDetailsTranslation;
    if (order?.paymentInfo?.expiryYear) {
      paymentDetailsTranslation = this.translation.translate('paymentCard.expires', {
        month: order.paymentInfo.expiryMonth,
        year: order.paymentInfo.expiryYear,
      });
    } else if (order?.worldpayAPMPaymentInfo?.name) {
      paymentDetailsTranslation =
        this.translation.translate(
          'paymentCard.apm', {
            apm: order?.worldpayAPMPaymentInfo?.name
          }
        );
    }
    return paymentDetailsTranslation;
  }
}
