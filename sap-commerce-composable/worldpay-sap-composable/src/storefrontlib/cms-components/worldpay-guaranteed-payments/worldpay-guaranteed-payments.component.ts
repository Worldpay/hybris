import { Component, OnDestroy, OnInit } from '@angular/core';
import { concatMap, distinctUntilChanged, filter, map, takeUntil } from 'rxjs/operators';
import { combineLatest, Observable, Subject } from 'rxjs';
import { UserAccountService } from '@spartacus/user/account/core';
import { WorldpayGuaranteedPaymentsService } from '../../../core/services/worldpay-guaranteed-payments/worldpay-guaranteed-payments.service';
import { GlobalMessageService, GlobalMessageType, HttpErrorModel, QueryState, UserIdService } from '@spartacus/core';
import { ActiveCartService } from '@spartacus/cart/base/core';

@Component({
  selector: 'worldpay-guaranteed-payments',
  template: '',
})
export class WorldpayGuaranteedPaymentsComponent implements OnInit, OnDestroy {

  private drop: Subject<void> = new Subject<void>();
  sessionId: string = '';
  firstLoad: boolean = true;

  ngOnInit(): void {
    this.getSessionId();

    this.worldpayGuaranteedPaymentsService.isGuaranteedPaymentsEnabledState().pipe(
      takeUntil(this.drop),
    ).subscribe({
      next: (response: QueryState<boolean>): void => {
        this.worldpayGuaranteedPaymentsService.setGuaranteedPaymentsEnabledEvent(response.data);
      },
      error: (error: unknown): void => {
        this.showErrorMessage(error as HttpErrorModel);
        this.worldpayGuaranteedPaymentsService.setGuaranteedPaymentsEnabledEvent(false);
      }
    });

    this.updateSessionId().pipe(
      takeUntil(this.drop),
    ).subscribe({
      next: (sessionId: string): void => {
        if (sessionId) {
          this.worldpayGuaranteedPaymentsService.setSessionId(sessionId);
        }
      },
      error: (error: unknown): void => {
        this.showErrorMessage(error as HttpErrorModel);
        this.worldpayGuaranteedPaymentsService.setGuaranteedPaymentsEnabledEvent(false);
      }
    });
  }

  /**
   * Constructor
   * @param activeCartService
   * @param userId - User IDService
   * @param userAccountService
   * @param globalMessage
   * @param worldpayGuaranteedPaymentsService
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
   * Get FraudSight session id
   * @since @since 4.3.6
   */
  getSessionId(): void {
    this.worldpayGuaranteedPaymentsService.getSessionId()
      .pipe(
        distinctUntilChanged(),
        takeUntil(this.drop),
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
          this.worldpayGuaranteedPaymentsService.setGuaranteedPaymentsEnabledEvent(false);
        }
      }
      );
  }

  /**
   * Update FraudSight session id
   * @since @since 4.3.6
   */
  updateSessionId(): Observable<string> {
    return this.worldpayGuaranteedPaymentsService.isGuaranteedPaymentsEnabledState()
      .pipe(
        concatMap(() => combineLatest([
          this.activeCartService.getActive(),
          this.userAccountService.get(),
          this.userIdService.getUserId(),
        ]).pipe(
          distinctUntilChanged(),
          filter(([cart, user, userId]) => (!!user?.uid || !!userId) && !!cart?.guid),
          map(([cart, userAccount, userId]): string =>
            userId === 'anonymous' ?
              `${userId}_${cart?.guid}` :
              `${userAccount?.customerId}_${cart?.guid}`
          ),
        )),
      );
  }

  showErrorMessage(error: HttpErrorModel): void {
    const errorMessage = error?.details?.[0]?.message || ' ';
    this.globalMessage.add({ key: errorMessage }, GlobalMessageType.MSG_TYPE_ERROR);
  }

  /**
   * Unsubscribe from all subscriptions
   * @since @since 4.3.6
   */
  ngOnDestroy(): void {
    this.drop.next();
    this.drop.complete();
  }

}
