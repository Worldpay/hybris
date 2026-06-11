import { Injectable } from '@angular/core';
import { UserPaymentConnector } from '@spartacus/core';
import { Observable } from 'rxjs';
import { ApmPaymentDetails } from '../../interfaces';
import { WorldpayUserPaymentAdapter } from './worldpay-user-payment.adapter';

/* eslint-disable @angular-eslint/prefer-inject */
@Injectable({
  providedIn: 'root',
})
export class WorldpayUserPaymentConnector extends UserPaymentConnector {
  constructor(protected override adapter: WorldpayUserPaymentAdapter) {
    super(adapter);
  }

  loadAllForCart(userId: string, cartId: string): Observable<ApmPaymentDetails[]> {
    return this.adapter.loadAllForCart(userId, cartId);
  }

}
