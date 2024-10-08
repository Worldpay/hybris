import { ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { filter, takeUntil } from 'rxjs/operators';
import { Order, OrderFacade } from '@spartacus/order/root';
import { Observable, of, Subject, Subscription } from 'rxjs';
import { WorldpayApplepayService } from '../../../../core/services/worldpay-applepay/worldpay-applepay.service';
import { RoutingService } from '@spartacus/core';
import { ApmData, ApplePayAuthorization, ApplePayPaymentRequest, PlaceOrderResponse } from '../../../../core/interfaces';

@Component({
  selector: 'worldpay-applepay',
  templateUrl: './worldpay-applepay.component.html',
  styleUrls: ['worldpay-applepay.component.scss'],
})
export class WorldpayApplepayComponent implements OnInit, OnDestroy {
  @Input() apm: ApmData;
  private paymentRequest$: Subscription;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private applePaySession: any;
  private enableApplePayButton$: Subscription;
  private drop: Subject<void> = new Subject<void>();
  isApplePayAvailable$: Observable<boolean> = of(false);

  constructor(
    protected orderFacade: OrderFacade,
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
    this.drop.complete();
  }

  placeApplePayOrder(): void {
    this.paymentRequest$ = this.worldpayApplepayService.getPaymentRequestFromState()
      .pipe(
        filter((paymentRequest: ApplePayPaymentRequest) => !!paymentRequest && Object.keys(paymentRequest).length > 0),
        takeUntil(this.drop)
      )
      .subscribe({
        next: (paymentRequest: ApplePayPaymentRequest): void => {
          this.applePaySession = this.worldpayApplepayService.createSession(paymentRequest);
        }
      });

    this.worldpayApplepayService.getMerchantSessionFromState()
      .pipe(
        filter((session: PlaceOrderResponse) => !!session && Object.keys(session).length > 0),
        takeUntil(this.drop)
      )
      .subscribe({
        next: (session: PlaceOrderResponse): void => {
          this.applePaySession.completeMerchantValidation(session);
        }
      });

    this.worldpayApplepayService.getPaymentAuthorizationFromState()
      .pipe(
        filter((authorization: ApplePayAuthorization) => !!authorization && Object.keys(authorization).length > 0),
        takeUntil(this.drop)
      )
      .subscribe({
        next: (authorization: ApplePayAuthorization): void => {
          const ApplePaySession = this.worldpayApplepayService.getApplePaySessionFromWindow();
          const statusCode =
            authorization.transactionStatus === 'AUTHORISED'
              ? ApplePaySession.STATUS_SUCCESS
              : ApplePaySession.STATUS_FAILURE;

          this.applePaySession.completePayment({
            status: statusCode
          });
          this.orderFacade.setPlacedOrder(authorization.order);
        }
      });

    this.orderFacade.getOrderDetails()
      .pipe(
        filter((order: Order): boolean => order && Object.keys(order)?.length !== 0),
        takeUntil(this.drop)
      )
      .subscribe({
        next: (): void => {
          this.routingService.go({ cxRoute: 'orderConfirmation' });
        }
      });
  }

  /**
   * If ApplePay is available, we request the payment request and wait for response.
   * When the response arrives, we show the ApplePay button
   */
  private initializeApplePay(): void {
    if (this.worldpayApplepayService.applePayButtonAvailable()) {
      this.enableApplePayButton$ = this.worldpayApplepayService.enableApplePayButton()
        .pipe(
          filter((paymentRequest: ApplePayPaymentRequest) => !!paymentRequest && Object.keys(paymentRequest).length > 0),
          takeUntil(this.drop)
        )
        .subscribe({
          next: (): void => {
            this.isApplePayAvailable$ = of(true);
            this.cd.detectChanges();
          }
        });
    }
  }
}
