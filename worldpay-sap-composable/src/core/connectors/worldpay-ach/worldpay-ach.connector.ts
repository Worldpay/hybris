import { inject, Injectable } from '@angular/core';
import { Order } from '@spartacus/order/root';
import { Observable } from 'rxjs';
import { AccountTypes, ACHPaymentForm } from '../../interfaces';
import { WorldpayACHAdapter } from './worldpay-ach.adapter';

@Injectable({
  providedIn: 'root'
})
export class WorldpayACHConnector {
  protected adapter: WorldpayACHAdapter = inject(WorldpayACHAdapter);

  /**
   * Get ACH Bank Account Types
   * @since 6.4.2
   * @param userId - User ID
   * @param cartId - Cart ID
   * @returns - ACHBankAccountType as Observable
   */
  getACHBankAccountTypes(
    userId: string,
    cartId: string,
  ): Observable<AccountTypes> {
    return this.adapter.getACHBankAccountTypes(
      userId,
      cartId,
    );
  }

  /**
   * Place order using ACH payment
   * @since 6.4.2
   * @param userId - User ID
   * @param cartId - Cart ID
   * @param achPaymentForm - ACH Payment Form
   * @returns - Order as Observable
   */
  placeACHOrder(userId: string, cartId: string, achPaymentForm: ACHPaymentForm): Observable<Order> {
    return this.adapter.placeACHOrder(userId, cartId, achPaymentForm);
  }
}
