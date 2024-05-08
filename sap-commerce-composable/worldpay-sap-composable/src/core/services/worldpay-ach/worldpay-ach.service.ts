import { Injectable } from '@angular/core';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { EventService, OCC_USER_ID_ANONYMOUS, Query, QueryService, QueryState, UserIdService } from '@spartacus/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import { WorldpayACHConnector } from '../../connectors/worldpay-ach/worldpay-ach.connector';
import { WorldpayACHFacade } from '../../facade/worldpay-ach.facade';
import { ACHBankAccountType, ACHPaymentForm, ACHPaymentFormRaw, ApmData } from '../../interfaces';
import { ClearWorldpayACHPaymentFormEvent } from '../../events';

@Injectable({
  providedIn: 'root'
})
export class WorldpayACHService implements WorldpayACHFacade {

  private achPaymentFormValue$: BehaviorSubject<ACHPaymentForm> = new BehaviorSubject<ACHPaymentForm>(null);

  /**
   * Checks if the conditions for checkout are met
   * @since 6.4.0
   * @returns Observable<[string, string]> - Observable with userId and cartId
   */
  checkoutPreconditions(): Observable<[string, string]> {
    return combineLatest([
      this.userIdService.takeUserId(),
      this.activeCartFacade.takeActiveCartId(),
      this.activeCartFacade.isGuestCart(),
    ]).pipe(
      take(1),
      map(([userId, cartId, isGuestCart]): [string, string] => {
        if (
          !userId ||
          !cartId ||
          (userId === OCC_USER_ID_ANONYMOUS && !isGuestCart)
        ) {
          throw new Error('Checkout conditions not met');
        }
        return [userId, cartId];
      })
    );
  }

  /**
   * Query used to get available APMs
   * @since 6.4.0
   * @params ACHBankAccountType[] - ACHBankAccountType[]
   * @returns Query<ApmData[]> - Query with ApmData[]
   */
  protected getAvailableApmQuery$: Query<ACHBankAccountType[]> =
    this.queryService.create<ApmData[]>(
      () => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]) =>
          this.worldpayACHConnector.getACHBankAccountTypes(userId, cartId)
        )
      )
    );

  constructor(
    protected userIdService: UserIdService,
    protected activeCartFacade: ActiveCartFacade,
    protected queryService: QueryService,
    protected eventService: EventService,
    protected worldpayACHConnector: WorldpayACHConnector
  ) {
  }

  /**
   * Get ACH Bank Account Types
   * @since 6.4.2
   * @returns - ACHBankAccountType as Observable
   */
  getACHBankAccountTypesState(): Observable<QueryState<ACHBankAccountType[]>> {
    return this.getAvailableApmQuery$.getState();
  }

  /**
   * Set ACH Payment Form Value
   * @since 6.4.2
   * @param achPaymentForm
   * @returns - void
   */
  setACHPaymentFormValue(achPaymentForm: ACHPaymentFormRaw): void {
    this.achPaymentFormValue$.next({
      ...achPaymentForm,
      accountType: achPaymentForm.accountType.code
    });
  }

  /**
   * Get ACH Payment Form Value
   * @since 6.4.2
   * @returns - ACHBankAccountType as Observable
   */
  getACHPaymentFormValue(): Observable<ACHPaymentForm> {
    return this.achPaymentFormValue$.asObservable();
  }

  /**
   * Clear Worldpay ACH Payment Form Event
   * @since 6.4.2
   */
  clearWorldpayACHPaymentFormEvent(): void {
    this.eventService.get(ClearWorldpayACHPaymentFormEvent).pipe(
      take(1)
    ).subscribe({
      next: (): void => {
        this.achPaymentFormValue$.next(null);
      }
    });
  }
}
