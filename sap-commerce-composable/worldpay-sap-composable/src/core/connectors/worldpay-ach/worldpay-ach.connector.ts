import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { WorldpayACHAdapter } from './worldpay-ach.adapter';
import { ACHBankAccountType, ACHPaymentForm } from '../../interfaces';
import { Order } from '@spartacus/order/root';

@Injectable({
  providedIn: 'root'
})
export class WorldpayACHConnector {

  /**
   * Constructor
   * @since 6.4.0
   * @param adapter - WorldpayApmAdapter
   */
  constructor(
    protected adapter: WorldpayACHAdapter
  ) {
  }

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
  ): Observable<ACHBankAccountType[]> {
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
   * @param ACHPaymentForm - ACH Payment Form
   * @returns - Order as Observable
   */
  placeACHOrder(userId: string, cartId: string, achPaymentForm: ACHPaymentForm): Observable<Order> {
    return this.adapter.placeACHOrder(userId, cartId, achPaymentForm);
  }
}
