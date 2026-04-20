/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component, inject, OnInit } from '@angular/core';
import { CardType, TranslationService, } from '@spartacus/core';
import { Card, PaymentMethodsComponent } from '@spartacus/storefront';
import { combineLatest, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { createCreditCardCard, WorldpayApmPaymentInfo, WorldpayCard, worldpayGetCardIcon } from '../../../core';

@Component({
  selector: 'cx-payment-methods',
  templateUrl: './worldpay-payment-methods.component.html',
  standalone: false,
})
export class WorldpayPaymentMethodsComponent extends PaymentMethodsComponent implements OnInit {
  private translationService: TranslationService = inject(TranslationService);

  override getCardContent(
    paymentDetails: WorldpayApmPaymentInfo,
  ): Observable<Card> {
    return combineLatest([
      this.translationService.translate('paymentCard.setAsDefault'),
      this.translationService.translate('common.delete'),
      this.translationService.translate('paymentCard.deleteConfirmation'),
      this.translationService.translate('paymentCard.expires', {
        month: paymentDetails.expiryMonth,
        year: paymentDetails.expiryYear,
      }),
      this.translationService.translate('paymentCard.defaultPaymentMethod'),
    ]).pipe(
      map(
        ([
          textSetAsDefault,
          textDelete,
          textDeleteConfirmation,
          textExpires,
          textDefaultPaymentMethod,
        ]: [string, string, string, string, string]): Card => {
          const paymentMethodData: WorldpayCard = createCreditCardCard(paymentDetails, textExpires);
          const defaultPayment: boolean = paymentDetails.defaultPayment;
          const cardType: CardType = paymentDetails.cardType;

          const actions: { name: string; event: string }[] = [];

          if (!defaultPayment) {
            actions.push({
              name: textSetAsDefault,
              event: 'default'
            });
          }

          actions.push({
            name: textDelete,
            event: 'edit'
          });

          return {
            role: 'region',
            title: paymentDetails.defaultPayment ? textDefaultPaymentMethod : '',
            actions,
            deleteMsg: textDeleteConfirmation,
            header: defaultPayment ? textDefaultPaymentMethod : undefined,
            img: this.getCardIcon(cardType?.code ?? ''),
            imgLabel: this.getCardIconLabel(cardType?.code),
            label: defaultPayment ? 'paymentCard.defaultPaymentLabel' : 'paymentCard.additionalPaymentLabel',
            ...paymentMethodData,
          };
        }
      )
    );
  }

  override getCardIcon(code: string): string {
    return worldpayGetCardIcon(code);
  }
}
