import { Component, OnDestroy, OnInit } from '@angular/core';
import { WorldpayGuaranteedPaymentsService } from '../../../core/services/worldpay-guaranteed-payments/worldpay-guaranteed-payments.service';
import { concatMap, distinctUntilChanged, filter, map, takeUntil } from 'rxjs/operators';
import { combineLatest, Observable, Subject } from 'rxjs';
import { ActiveCartService, UserIdService } from '@spartacus/core';
import { UserAccountService } from '@spartacus/user/account/core';

@Component({
  selector: 'worldpay-guaranteed-payments',
  template: '',
})
export class WorldpayGuaranteedPaymentsComponent implements OnInit, OnDestroy {

  private drop = new Subject<void>();
  sessionId = '';
  firstLoad = true;

  constructor(
    protected worldpayGuaranteedPaymentsService: WorldpayGuaranteedPaymentsService,
    protected activeCartService: ActiveCartService,
    protected userIdService: UserIdService,
    protected userAccountService: UserAccountService,
  ) {
  }

  ngOnInit(): void {
    this.getSessionId();

    this.worldpayGuaranteedPaymentsService.isGuaranteedPaymentsEnabled();

    this.updateSessionId().pipe(
      takeUntil(this.drop),
    ).subscribe((sessionId) => {
      if (sessionId) {
        this.worldpayGuaranteedPaymentsService.setSessionId(sessionId);
      }
    });
  }

  getSessionId(): void {
    this.worldpayGuaranteedPaymentsService.getSessionId()
      .pipe(
        distinctUntilChanged(),
        takeUntil(this.drop),
      )
      .subscribe((sessionId) => {
        if (
          sessionId !== this.sessionId ||
          this.firstLoad
        ) {
          this.sessionId = sessionId;
          if (this.sessionId) {
            this.worldpayGuaranteedPaymentsService.generateScript(sessionId);
          }
          this.firstLoad = false;
        }
      });
  }

  updateSessionId(): Observable<string> {
    return this.worldpayGuaranteedPaymentsService.isGuaranteedPaymentsEnabledFromState()
      .pipe(
        filter(Boolean),
        concatMap(isEnabled => combineLatest([
          this.activeCartService.getActive(),
          this.userAccountService.get(),
          this.userIdService.getUserId(),
        ]).pipe(
          distinctUntilChanged(),
          filter(([cart, user, userId]) => (!!user?.uid || !!userId) && !!cart?.guid),
          map(([cart, userAccount, userId]) =>
            userId === 'anonymous' ?
              `${userId}_${cart?.guid}` :
              `${userAccount?.customerId}_${cart?.guid}`
          ),
        )),
      );
  }

  ngOnDestroy(): void {
    this.drop.next();
    this.drop.complete();
  }

}
