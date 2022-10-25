import { Injectable } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { ActiveCartService, CartActions, GlobalMessage, GlobalMessageActions, GlobalMessageType, PaymentDetails, StateWithProcess, UserIdService } from '@spartacus/core';
import { combineLatest, Observable } from 'rxjs';
import { getWorldpayCvn, getWorldpayLoading } from '../../store/worldpay.selectors';
import { StateWithWorldpay } from '../../store/worldpay.state';
import { take } from 'rxjs/operators';
import * as WorldpayActions from '../../store/worldpay.action';
import { WorldpayApmService } from '../worldpay-apm/worldpay-apm.service';
import { WorldpayCheckoutPaymentService } from './worldpay-checkout-payment.service';
import { ApmPaymentDetails, PaymentMethod } from '../../interfaces';
import { CheckoutService, StateWithCheckout } from '@spartacus/checkout/core';

@Injectable({
  providedIn: 'root'
})
export class WorldpayCheckoutService extends CheckoutService {
  constructor(
    protected checkoutStore: Store<StateWithCheckout>,
    protected processStateStore: Store<StateWithProcess<void>>,
    protected userIdService: UserIdService,
    protected activeCartService: ActiveCartService,
    protected worldpayStore: Store<StateWithWorldpay>,
    protected worldpayApmService: WorldpayApmService,
    protected worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService,
  ) {
    super(
      checkoutStore,
      processStateStore,
      activeCartService,
      userIdService
    );
  }

  getCvnFromState(): Observable<string> {
    return this.worldpayStore.pipe(select(getWorldpayCvn));
  }

  getLoading(): Observable<boolean> {
    return this.worldpayStore.pipe(select(getWorldpayLoading));
  }

  placeOrder(save: boolean = false): void {
    combineLatest([
      this.worldpayApmService.getSelectedAPMFromState(),
      this.worldpayCheckoutPaymentService.getPaymentDetails(),
      this.userIdService.getUserId(),
      this.activeCartService.getActiveCartId(),
    ]).pipe(take(1))
      .subscribe(([apm, paymentDetails, userId, cartId]) => {
        if (!apm || apm?.code === PaymentMethod.Card) {
          this.worldpayStore.dispatch(new WorldpayActions.GetWorldpayDDCJwt());
        } else {
          this.worldpayStore.dispatch(
            new WorldpayActions.GetAPMRedirectUrl({
              userId,
              cartId,
              apm: paymentDetails as ApmPaymentDetails,
              save
            })
          );
        }
      });
  }

  initialPaymentRequest(
    unsafePaymentDetails: PaymentDetails,
    dfReferenceId: string,
    cseToken: string,
    acceptedTermsAndConditions: boolean,
    deviceSession: string,
  ): void {

    combineLatest([
      this.userIdService.getUserId(),
      this.activeCartService.getActiveCartId(),
    ])
      .pipe(take(1))
      .subscribe(([userId, cartId]) => {
        const paymentDetails = { ...unsafePaymentDetails };
        delete paymentDetails.cardNumber;

        const challengeWindowSize =
          window.innerWidth >= 620 ? '600x400' : '390x400';

        this.checkoutStore.dispatch(
          new WorldpayActions.InitialPaymentRequest({
            paymentDetails,
            cseToken,
            userId,
            cartId,
            dfReferenceId,
            challengeWindowSize,
            acceptedTermsAndConditions,
            deviceSession,
          })
        );
      })
      .unsubscribe();
  }

  challengeAccepted(code: string): void {
    combineLatest([
      this.userIdService.getUserId(),
      this.activeCartService.getActiveCartId()
    ])
      .pipe(take(1))
      .subscribe(([userId, cartId]) => {
        this.worldpayStore.dispatch(
          new WorldpayActions.ChallengeAccepted({
            userId,
            code
          })
        );
        this.worldpayStore.dispatch(new WorldpayActions.ClearPaymentDetails());
        this.checkoutStore.dispatch(new CartActions.RemoveCart({ cartId }));
      })
      .unsubscribe();
  }

  challengeFailed(): void {
    const failMessage: GlobalMessage = {
      text: {
        key: 'checkoutReview.challengeFailed'
      },
      type: GlobalMessageType.MSG_TYPE_ERROR
    };
    this.checkoutStore.dispatch(
      new GlobalMessageActions.AddMessage(failMessage)
    );
    this.worldpayStore.dispatch(
      new WorldpayActions.ChallengeAcceptedFail(null)
    );
    this.worldpayStore.dispatch(new WorldpayActions.ClearPaymentDetails());
  }

  startLoading(): void {
    this.worldpayStore.dispatch(new WorldpayActions.StartLoader());
  }
}
