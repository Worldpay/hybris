import { Injectable } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { ActiveCartService, Address, PaymentDetails, StateWithProcess, UserIdService, UserPaymentService } from '@spartacus/core';
import {
  getWorldpayCsePublicKey,
  getWorldpayCseToken,
  getWorldpayLoading,
  getWorldpayPaymentAddress,
  getWorldpaySelectedAPM,
  getWorldpayThreeDsChallengeIframeUrl,
  getWorldpayThreeDsChallengeInfo,
  getWorldpayThreeDsDDCIframeUrl,
  getWorldpayThreeDsDDCInfo
} from '../../store/worldpay.selectors';

import * as WorldpayActions from '../../store/worldpay.action';
import { Observable } from 'rxjs';
import { StateWithWorldpay } from '../../store/worldpay.state';
import { take } from 'rxjs/operators';
import { ThreeDsDDCInfo, ThreeDsInfo } from '../../connectors/worldpay.adapter';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ApmData, ApmPaymentDetails } from '../../interfaces';
import { CheckoutActions, CheckoutPaymentService, StateWithCheckout } from '@spartacus/checkout/core';
import { Router } from '@angular/router';

declare let Worldpay: any;

@Injectable({
  providedIn: 'root'
})
export class WorldpayCheckoutPaymentService extends CheckoutPaymentService {
  constructor(
    protected checkoutStore: Store<StateWithCheckout>,
    protected processStateStore: Store<StateWithProcess<void>>,
    protected activeCartService: ActiveCartService,
    protected userIdService: UserIdService,
    protected worldpayStore: Store<StateWithWorldpay>,
    protected sanitizer: DomSanitizer,
    protected userPaymentService: UserPaymentService,
    private router: Router,
  ) {
    super(
      checkoutStore,
      processStateStore,
      activeCartService,
      userIdService
    );
  }

  getPublicKey(): void {
    this.worldpayStore.dispatch(new WorldpayActions.GetWorldpayPublicKey());
  }

  getThreeDsDDCJwt(): void {
    this.worldpayStore.dispatch(new WorldpayActions.GetWorldpayDDCJwt());
  }

  getPublicKeyFromState(): Observable<string> {
    return this.worldpayStore.pipe(select(getWorldpayCsePublicKey));
  }

  getCseTokenFromState(): Observable<string> {
    return this.worldpayStore.pipe(select(getWorldpayCseToken));
  }

  getDDCInfoFromState(): Observable<ThreeDsDDCInfo> {
    return this.worldpayStore.pipe(select(getWorldpayThreeDsDDCInfo));
  }

  getThreeDsChallengeInfoFromState(): Observable<ThreeDsInfo> {
    return this.worldpayStore.pipe(select(getWorldpayThreeDsChallengeInfo));
  }

  getThreeDsDDCIframeUrlFromState(): Observable<SafeResourceUrl> {
    return this.worldpayStore.pipe(select(getWorldpayThreeDsDDCIframeUrl));
  }

  getThreeDsChallengeIframeUrlFromState(): Observable<SafeResourceUrl> {
    return this.worldpayStore.pipe(
      select(getWorldpayThreeDsChallengeIframeUrl)
    );
  }

  createPaymentDetails(paymentDetails: PaymentDetails): void {
    this.getPublicKeyFromState()
      .pipe(take(1))
      .subscribe((key: string) => {
        Worldpay.setPublicKey(key);
      });

    if (this.actionAllowed()) {
      const cseToken = Worldpay.encrypt({
        cvc: paymentDetails.cvn,
        cardHolderName: paymentDetails.accountHolderName,
        cardNumber: paymentDetails.cardNumber,
        expiryMonth: paymentDetails.expiryMonth,
        expiryYear: paymentDetails.expiryYear
      });

      const userId = this.getUserId();
      const cartId = this.getCartId();
      this.worldpayStore.dispatch(
        new WorldpayActions.CreateWorldpayPaymentDetails({
          userId,
          cartId,
          paymentDetails,
          cseToken
        })
      );
    }
  }

  useExistingPaymentDetails(paymentDetails: PaymentDetails): void {
    const cartId = this.getCartId();
    const userId = this.getUserId();

    this.worldpayStore.dispatch(
      new WorldpayActions.UseExistingWorldpayPaymentDetails({
        userId,
        cartId,
        paymentDetails
      })
    );
  }

  setPaymentAddress(address: Address): Observable<Address> {
    const cartId = this.getCartId();
    const userId = this.getUserId();

    this.worldpayStore.dispatch(
      new WorldpayActions.SetPaymentAddress({
        userId,
        cartId,
        address
      })
    );

    return this.worldpayStore.pipe(
      select(getWorldpayPaymentAddress)
    );
  }

  setApmPaymentDetails(apmPaymentDetails: ApmPaymentDetails): Observable<PaymentDetails> {
    const cartId = this.getCartId();
    const userId = this.getUserId();

    this.checkoutStore.dispatch(
      new WorldpayActions.SetAPMPaymentInfo({
        userId,
        cartId,
        apmPaymentDetails
      })
    );

    return this.getPaymentDetails();
  }

  private getCartId(): string {
    let cartId: string;
    this.activeCartService
      .getActiveCartId()
      .pipe(take(1))
      .subscribe((res: string) => (cartId = res))
      .unsubscribe();
    return cartId;
  }

  private getUserId(): string {
    let userId: string;
    this.userIdService
      .getUserId()
      .pipe(take(1))
      .subscribe((res: string) => (userId = res))
      .unsubscribe();
    return userId;
  }

  private getApm(): ApmData {
    let apm: ApmData;
    this.worldpayStore.pipe(select(getWorldpaySelectedAPM))
      .pipe(take(1))
      .subscribe(selectedAPM => apm = selectedAPM)
      .unsubscribe();
    return apm;
  }

  private getSerializedUrl(url, params): string {
    const parameters = `${this.router.serializeUrl(this.router.createUrlTree([url], { queryParams: params }))}`;
    return parameters.length > 1 ? parameters : '/';
  }

  setThreeDsDDCIframeUrl(
    ddcUrl: string,
    cardNumber: string,
    jwt: string
  ): void {
    const url = this.getSerializedUrl('worldpay-3ds-device-detection', {
      action: ddcUrl,
      bin: cardNumber,
      jwt
    });

    this.worldpayStore.dispatch(
      new WorldpayActions.SetWorldpayDDCIframeUrl(
        this.sanitizer.bypassSecurityTrustResourceUrl(url)
      )
    );
  }

  setThreeDsChallengeIframeUrl(
    challengeUrl: string,
    jwt: string,
    merchantData: string
  ): void {
    const url = this.getSerializedUrl('worldpay-3ds-challenge', {
      action: challengeUrl,
      md: merchantData,
      jwt
    });

    this.worldpayStore.dispatch(
      new WorldpayActions.SetWorldpayChallengeIframeUrl(
        this.sanitizer.bypassSecurityTrustResourceUrl(url)
      )
    );
  }

  isLoading(): Observable<boolean> {
    return this.worldpayStore.pipe(select(getWorldpayLoading));
  }
}