import { Order } from '@spartacus/order/root';
import { Observable } from 'rxjs';
import { AccountTypes, ACHPaymentForm } from '../../interfaces';

export abstract class WorldpayACHAdapter {

  /**
   * Get ACH Bank Account Types
   * @since 6.4.2
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @returns {Observable<AccountTypes>} - AccountTypes as Observable
   */
  abstract getACHBankAccountTypes(
    userId: string,
    cartId: string,
  ): Observable<AccountTypes>;

  /**
   * Place order using ACH payment
   * @since 6.4.2
   * @param {string} userId - User ID
   * @param {string} cartId - Cart ID
   * @param {achPaymentForm} ACHPaymentForm - ACH Payment Form
   * @returns {Observable<ApmData[]>} - ApmData as Observable
   */
  abstract placeACHOrder(userId: string, cartId: string, achPaymentForm: ACHPaymentForm): Observable<Order>;
}
