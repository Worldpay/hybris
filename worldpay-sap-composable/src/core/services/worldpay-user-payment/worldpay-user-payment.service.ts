import { inject, Injectable } from '@angular/core';
import { select } from '@ngrx/store';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import {
  CheckoutDeliveryAddressCreatedEvent,
  CheckoutDeliveryAddressSetEvent,
  CheckoutPaymentDetailsCreatedEvent,
  CheckoutPaymentDetailsSetEvent
} from '@spartacus/checkout/base/root';
import {
  CurrencySetEvent,
  LoggerService,
  Query,
  QueryNotifier,
  QueryService,
  QueryState,
  tryNormalizeHttpError,
  UserActions,
  UserPaymentService,
  UsersSelectors
} from '@spartacus/core';
import { OrderPlacedEvent } from '@spartacus/order/root';
import { combineLatest, Observable } from 'rxjs';
import { map, switchMap, take } from 'rxjs/operators';
import {
  CreateWorldpayPaymentDetailsEvent,
  WorldpayBillingAddressCreatedEvent,
  WorldpayBillingAddressSameAsDeliveryAddressSetEvent,
  WorldpayBillingAddressUpdatedEvent
} from '../../events';
import { ApmData, ApmPaymentDetails, WorldpayUserPaymentConnector } from '../../index';

@Injectable()
export class WorldpayUserPaymentService extends UserPaymentService {
  protected activeCartFacade: ActiveCartFacade = inject(ActiveCartFacade);
  protected queryService: QueryService = inject(QueryService);
  protected logger: LoggerService = inject(LoggerService);
  protected userPaymentMethodConnector: WorldpayUserPaymentConnector = inject(WorldpayUserPaymentConnector);

  /**
   * Query used to get available APMs
   * @since 2211.34.0
   * @params ACHBankAccountType[] - ACHBankAccountType[]
   * @returns Query<ApmPaymentDetails[]> - Query with ApmData[]
   */
  protected loadAllPaymentMethodsQuery$: Query<ApmPaymentDetails[]> =
    this.queryService.create<ApmPaymentDetails[]>(
      (): Observable<ApmData[]> => this.checkoutPreconditions().pipe(
        switchMap(([userId, cartId]: [string, string]): Observable<ApmPaymentDetails[]> => {
          if (cartId && cartId !== '') {
            return this.userPaymentMethodConnector.loadAllForCart(userId, cartId);
          }
          super.loadPaymentMethods();
          return this.store.pipe(select(UsersSelectors.getPaymentMethods));
        })
      ),
      {
        reloadOn: this.reloadSavedPaymentMethodsEvents(),
        resetOn: [OrderPlacedEvent]
      }
    );

  /**
   * Triggers the loading of all payment methods and retrieves the current state of the query.
   *
   *  @since 2211.43.0
   * @returns {Observable<QueryState<ApmPaymentDetails[]>>} - An observable emitting the current state of the query for loading all payment methods.
   */
  triggerLoadAllPaymentMethods(): Observable<QueryState<ApmPaymentDetails[]>> {
    return this.loadAllPaymentMethodsQuery$.getState();
  }

  /**
   * Loads all user's payment methods.
   */
  override loadPaymentMethods(): void {
    this.triggerLoadAllPaymentMethods().subscribe({
      next: (queryState: QueryState<ApmPaymentDetails[]>): void => {
        if (queryState.error !== false) {
          this.dispatchError(queryState.error);
        } else {
          this.store.dispatch(new UserActions.LoadUserPaymentMethodsSuccess(queryState.data));
        }
      },
      error: (error: unknown): void => {
        this.dispatchError(error);
      }
    });
  }

  /**
   * Checks if the payment methods are currently being loaded.
   *
   * @override
   * @since 6.4.0
   * @returns {Observable<boolean>} - An observable that emits `true` if the payment methods are being loaded, otherwise `false`.
   */
  override getPaymentMethodsLoading(): Observable<boolean> {
    return this.triggerLoadAllPaymentMethods().pipe(
      map((queryState: QueryState<ApmPaymentDetails[]>): boolean => queryState.loading)
    );
  }

  /**
   * Checks if the conditions for checkout are met
   * @since 2211.43.0
   * @returns Observable<[string, string]> - Observable with userId and cartId
   */
  checkoutPreconditions(): Observable<[string, string]> {
    return combineLatest([
      this.userIdService.takeUserId(),
      this.activeCartFacade.getActiveCartId(),
    ]).pipe(
      take(1),
      map(([userId, cartId]: [string, string]): [string, string] => {
        if (!userId) {
          throw new Error('Checkout conditions not met');
        }
        return [userId, cartId];
      })
    );
  }

  /**
   * Dispatches an action to indicate that loading user payment methods has failed.
   *
   * @since 2211.43.0
   * @param {unknown} error - The error object or message to be dispatched.
   * Dispatches a `LoadUserPaymentMethodsFail` action with the normalized error.
   */
  dispatchError(error: unknown): void {
    this.store.dispatch(new UserActions.LoadUserPaymentMethodsFail(tryNormalizeHttpError(error, this.logger)));
  }

  /**
   * Retrieves the list of events that trigger a reload of saved payment methods.
   * These events are used as notifiers to refresh the payment methods data.
   *
   * @since 2211.43.0
   * @returns {QueryNotifier[]} - An array of QueryNotifier events that trigger a reload.
   */
  protected reloadSavedPaymentMethodsEvents(): QueryNotifier[] {
    return [
      CurrencySetEvent,
      CheckoutDeliveryAddressSetEvent,
      CheckoutPaymentDetailsSetEvent,
      CheckoutDeliveryAddressCreatedEvent,
      CheckoutPaymentDetailsCreatedEvent,
      CreateWorldpayPaymentDetailsEvent,
      WorldpayBillingAddressCreatedEvent,
      WorldpayBillingAddressUpdatedEvent,
      WorldpayBillingAddressSameAsDeliveryAddressSetEvent
    ];
  }
}
