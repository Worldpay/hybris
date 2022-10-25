import { ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { filter, takeUntil } from 'rxjs/operators';
import { ApplePayAuthorization, ApplePayPaymentRequest } from '../../../../core/connectors/worldpay.adapter';
import { BehaviorSubject, Observable, of, Subject, Subscription } from 'rxjs';
import { WorldpayApplepayService } from '../../../../core/services/worldpay-applepay/worldpay-applepay.service';
import { RoutingService } from '@spartacus/core';
import { CheckoutService } from '@spartacus/checkout/core';
import { FormGroup } from '@angular/forms';
import { ApmData } from '../../../../core/interfaces';

@Component({
  selector: 'worldpay-applepay',
  templateUrl: './worldpay-applepay.component.html',
  styleUrls: ['worlpay-applepay.component.scss'],
})
export class WorldpayApplepayComponent implements OnInit, OnDestroy {
  @Input() apm: ApmData;
  @Input() billingAddressForm: FormGroup = new FormGroup({});
  public sameAsShippingAddress$ = new BehaviorSubject<boolean>(true);

  private paymentRequest$: Subscription;

  private applePaySession: any;

  private enableApplePayButton$: Subscription;
  private drop = new Subject<void>();

  isApplePayAvailable$: Observable<boolean> = of(false);

  constructor(
    protected checkoutService: CheckoutService,
    protected routingService: RoutingService,
    protected worldpayApplepayService: WorldpayApplepayService,
    protected cd: ChangeDetectorRef
  ) {
  }

  ngOnInit(): void {
    this.initializeApplePay();
  }

  ngOnDestroy(): void {
    this.drop.next();
  }

  placeApplePayOrder(): void {
    this.paymentRequest$ = this.worldpayApplepayService
      .getPaymentRequestFromState()
      .pipe(
        filter(
          paymentRequest =>
            !!paymentRequest && Object.keys(paymentRequest).length > 0
        ),
        takeUntil(this.drop)
      )
      .subscribe((paymentRequest: ApplePayPaymentRequest) => {
        this.applePaySession = this.worldpayApplepayService.createSession(
          paymentRequest
        );
      });

    this.worldpayApplepayService
      .getMerchantSesssionFromState()
      .pipe(
        filter((session: any) => !!session && Object.keys(session).length > 0),
        takeUntil(this.drop)
      )
      .subscribe((session: any) => {
        this.applePaySession.completeMerchantValidation(session);
      });

    this.worldpayApplepayService
      .getPaymentAuthorizationFromState()
      .pipe(
        filter(
          (authorization: ApplePayAuthorization) =>
            !!authorization && Object.keys(authorization).length > 0
        ),
        takeUntil(this.drop)
      )
      .subscribe((authorization: ApplePayAuthorization) => {
        const ApplePaySession = this.worldpayApplepayService.getApplePaySessionFromWindow();
        const statusCode =
          authorization.transactionStatus === 'AUTHORISED'
            ? ApplePaySession.STATUS_SUCCESS
            : ApplePaySession.STATUS_FAILURE;

        this.applePaySession.completePayment({
          status: statusCode
        });
      });

    this.checkoutService
      .getOrderDetails()
      .pipe(
        filter(order => Object.keys(order).length !== 0),
        takeUntil(this.drop)
      )
      .subscribe(() => {
        this.routingService.go({ cxRoute: 'orderConfirmation' });
      });
  }

  /**
   * If ApplePay is available, we request the payment request and wait for response.
   * When the response arrives, we show the ApplePay button
   */
  private initializeApplePay(): void {
    if (this.worldpayApplepayService.applePayButtonAvailable()) {
      this.enableApplePayButton$ = this.worldpayApplepayService
        .enableApplePayButton()
        .pipe(
          filter(paymentRequest => !!paymentRequest && Object.keys(paymentRequest).length > 0),
          takeUntil(this.drop)
        )
        .subscribe(_ => {
          this.isApplePayAvailable$ = of(true);
          this.cd.detectChanges();
        });
    }
  }
}
