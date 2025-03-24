import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActiveCartService } from '@spartacus/cart/base/core';
import { Cart } from '@spartacus/cart/base/root';
import { GlobalMessageService, GlobalMessageType, HttpErrorModel, LoggerService, QueryState, User, UserIdService } from '@spartacus/core';
import { UserAccountService } from '@spartacus/user/account/core';
import { WorldpayGuaranteedPaymentsService } from '@worldpay-services/worldpay-guaranteed-payments/worldpay-guaranteed-payments.service';
import { combineLatest, Observable } from 'rxjs';
import { concatMap, distinctUntilChanged, filter, map } from 'rxjs/operators';

@Component({
  selector: 'worldpay-guaranteed-payments',
  template: '',
})
export class WorldpayGuaranteedPaymentsComponent implements OnInit {

  sessionId: string = '';
  firstLoad: boolean = true;
  private logger: LoggerService = inject(LoggerService);
  private destroyRef: DestroyRef = inject(DestroyRef);

  /**
   * Constructor for the WorldpayGuaranteedPaymentsComponent.
   *
   * @param activeCartService - Service to manage the active cart.
   * @param userIdService - Service to manage user IDs.
   * @param userAccountService - Service to manage user accounts.
   * @param globalMessage - Service to display global messages.
   * @param worldpayGuaranteedPaymentsService - Service to manage Worldpay guaranteed payments.
   * @since 6.4.0
   */
  constructor(
    protected activeCartService: ActiveCartService,
    protected userIdService: UserIdService,
    protected userAccountService: UserAccountService,
    protected globalMessage: GlobalMessageService,
    protected worldpayGuaranteedPaymentsService: WorldpayGuaranteedPaymentsService,
  ) {
  }

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
      takeUntilDestroyed(this.destroyRef),
    ).subscribe({
      next: (sessionId: string): void => {
        if (sessionId) {
          this.worldpayGuaranteedPaymentsService.setSessionId(sessionId);
        }
      },
      error: (error: unknown): void => {
        this.showErrorMessage(error as HttpErrorModel);
        this.logger.error('Failed to update Guaranteed Payments session Id, check component configuration', error);
        this.worldpayGuaranteedPaymentsService.setGuaranteedPaymentsEnabledEvent(false);
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
    this.worldpayGuaranteedPaymentsService.getSessionId()
      .pipe(
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (sessionId: string): void => {
          if (sessionId !== this.sessionId || this.firstLoad) {
            this.sessionId = sessionId;
            if (this.sessionId) {
              this.worldpayGuaranteedPaymentsService.generateScript(sessionId);
            }
            this.firstLoad = false;
          }
        },
        error: (error: unknown): void => {
          this.showErrorMessage(error as HttpErrorModel);
          this.logger.error('Failed to get Guaranteed Payments session Id, check component configuration', error);
          this.worldpayGuaranteedPaymentsService.setGuaranteedPaymentsEnabledEvent(false);
        }
      }
      );
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
    this.worldpayGuaranteedPaymentsService.isGuaranteedPaymentsEnabledState().pipe(
      takeUntilDestroyed(this.destroyRef),
    ).subscribe({
      next: (response: QueryState<boolean>): void => {
        this.worldpayGuaranteedPaymentsService.setGuaranteedPaymentsEnabledEvent(response.data);
      },
      error: (error: unknown): void => {
        this.showErrorMessage(error as HttpErrorModel);
        this.logger.error('Failed to initialize Guaranteed Payments, check component configuration', error);
        this.worldpayGuaranteedPaymentsService.setGuaranteedPaymentsEnabledEvent(false);
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
    return this.worldpayGuaranteedPaymentsService.isGuaranteedPaymentsEnabledState()
      .pipe(
        concatMap((): Observable<string> => combineLatest([
          this.activeCartService.getActive(),
          this.userAccountService.get(),
          this.userIdService.getUserId(),
        ]).pipe(
          distinctUntilChanged(),
          filter(([cart, user, userId]: [Cart, User, string]): boolean => (!!user?.uid || !!userId) && !!cart?.guid),
          map(([cart, userAccount, userId]: [Cart, User, string]): string =>
            userId === 'anonymous' ?
              `${userId}_${cart?.guid}` :
              `${userAccount?.customerId}_${cart?.guid}`
          ),
        )),
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
