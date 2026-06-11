import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActiveCartFacade, Cart } from '@spartacus/cart/base/root';
import { GlobalMessageService, GlobalMessageType, HttpErrorModel, LoggerService, QueryState, User, UserIdService } from '@spartacus/core';
import { UserAccountFacade } from '@spartacus/user/account/root';
import { combineLatest, Observable } from 'rxjs';
import { concatMap, distinctUntilChanged, filter, map } from 'rxjs/operators';
import { WorldpayGuaranteedPaymentsFacade } from '../../../core';

@Component({
  selector: 'worldpay-guaranteed-payments',
  template: ''
})
export class WorldpayGuaranteedPaymentsComponent implements OnInit {

  sessionId: string = '';
  firstLoad: boolean = true;
  protected activeCartFacade: ActiveCartFacade = inject(ActiveCartFacade);
  protected userIdService: UserIdService = inject(UserIdService);
  protected userAccountFacade: UserAccountFacade = inject(UserAccountFacade);
  protected globalMessage: GlobalMessageService = inject(GlobalMessageService);
  protected worldpayGuaranteedPaymentsFacade: WorldpayGuaranteedPaymentsFacade = inject(WorldpayGuaranteedPaymentsFacade);
  private logger: LoggerService = inject(LoggerService);
  private destroyRef: DestroyRef = inject(DestroyRef);

  /**
   * Angular lifecycle hook that is called after the component's view has been initialized.
   * It retrieves the session id, checks if guaranteed payments are enabled, and updates the session id.
   * It also handles any errors that occur during these processes.
   *
   * @since 6.4.0
   */
  ngOnInit(): void {
    this.getSessionId();
    this.isGuaranteedPaymentsEnabledState();
    this.updateSessionId().pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (sessionId: string): void => {
        if (sessionId) {
          this.worldpayGuaranteedPaymentsFacade.setSessionId(sessionId);
        }
      },
      error: (error: unknown): void => {
        this.showErrorMessage(error as HttpErrorModel);
        this.logger.error('Failed to update Guaranteed Payments session Id, check component configuration', error);
        this.worldpayGuaranteedPaymentsFacade.setGuaranteedPaymentsEnabledEvent(false);
      }
    });
  }

  /**
   * Get FraudSight session id
   *
   * This method retrieves the session id from the WorldpayGuaranteedPaymentsService.
   * If the session id is different from the current session id or if it is the first load,
   * it updates the session id and generates the script for the session.
   *
   * @since 4.3.6
   */
  getSessionId(): void {
    this.worldpayGuaranteedPaymentsFacade.getSessionId().pipe(
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (sessionId: string): void => {
        if (sessionId !== this.sessionId || this.firstLoad) {
          this.sessionId = sessionId;
          if (this.sessionId) {
            this.worldpayGuaranteedPaymentsFacade.generateScript(sessionId);
          }
          this.firstLoad = false;
        }
      },
      error: (error: unknown): void => {
        this.showErrorMessage(error as HttpErrorModel);
        this.logger.error('Failed to get Guaranteed Payments session Id, check component configuration', error);
        this.worldpayGuaranteedPaymentsFacade.setGuaranteedPaymentsEnabledEvent(false);
      }
    });
  }

  /**
   * Check if Guaranteed Payments are enabled
   *
   * This method checks if the guaranteed payments are enabled by subscribing to the
   * isGuaranteedPaymentsEnabledState observable from the WorldpayGuaranteedPaymentsService.
   * It sets the guaranteed payments enabled event based on the response data.
   * If an error occurs, it displays an error message and logs the error.
   *
   * @since 2211.32.1
   */
  isGuaranteedPaymentsEnabledState(): void {
    this.worldpayGuaranteedPaymentsFacade.isGuaranteedPaymentsEnabledState().pipe(
      filter((state: QueryState<boolean>): boolean => !state.loading),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe({
      next: (response: QueryState<boolean>): void => {
        this.worldpayGuaranteedPaymentsFacade.setGuaranteedPaymentsEnabledEvent(response.data);
      },
      error: (error: unknown): void => {
        this.showErrorMessage(error as HttpErrorModel);
        this.logger.error('Failed to initialize Guaranteed Payments, check component configuration', error);
        this.worldpayGuaranteedPaymentsFacade.setGuaranteedPaymentsEnabledEvent(false);
      }
    });
  }

  /**
   * Update FraudSight session id
   *
   * This method updates the session id by combining the active cart, user account, and user id.
   * It first checks if the guaranteed payments are enabled, then retrieves the active cart,
   * user account, and user id. If the user is anonymous, it combines the user id with the cart guid.
   * Otherwise, it combines the user account customer id with the cart guid.
   *
   * @returns {Observable<string>} An observable that emits the updated session id.
   * @since 4.3.6
   */
  updateSessionId(): Observable<string> {
    return this.worldpayGuaranteedPaymentsFacade.isGuaranteedPaymentsEnabledState().pipe(
      concatMap((): Observable<string> => combineLatest([
        this.activeCartFacade.getActive(),
        this.userAccountFacade.get(),
        this.userIdService.getUserId()
      ]).pipe(
        distinctUntilChanged(),
        filter(([cart, user, userId]: [Cart, User, string]): boolean => (!!user?.uid || !!userId) && !!cart?.guid),
        map(([cart, userAccount, userId]: [Cart, User, string]): string =>
          userId === 'anonymous' ?
            `${userId}_${cart?.guid}` :
            `${userAccount?.customerId}_${cart?.guid}`
        )
      ))
    );
  }

  /**
   * Display an error message
   *
   * This method extracts the error message from the provided HttpErrorModel
   * and displays it using the GlobalMessageService.
   *
   * @param {HttpErrorModel} error - The error model containing the error details.
   */
  showErrorMessage(error: HttpErrorModel): void {
    const errorMessage: string = error?.details?.[0]?.message || ' ';
    this.globalMessage.add({ key: errorMessage }, GlobalMessageType.MSG_TYPE_ERROR);
  }
}
