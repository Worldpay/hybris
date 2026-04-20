import { ChangeDetectorRef, Component, DestroyRef, EventEmitter, inject, OnInit, Output } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RoutingService } from '@spartacus/core';
import { Order, OrderFacade } from '@spartacus/order/root';
import { Observable, of, Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { ApplePayAuthorization, ApplePayPaymentRequest, PlaceOrderResponse, WorldpayApplepayService } from '../../../../core';

@Component({
  selector: 'worldpay-applepay',
  templateUrl: './worldpay-applepay.component.html',
  styleUrls: ['worldpay-applepay.component.scss'],
  standalone: false
})
export class WorldpayApplepayComponent implements OnInit {
  @Output() back: EventEmitter<void> = new EventEmitter<void>();
  isApplePayAvailable$: Observable<boolean> = of(false);
  private paymentRequest$: Subscription;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private applePaySession: any;
  private enableApplePayButton$: Subscription;
  private destroyRef: DestroyRef = inject(DestroyRef);

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

  placeApplePayOrder(): void {
    this.paymentRequest$ = this.worldpayApplepayService.getPaymentRequestFromState()
      .pipe(
        filter((paymentRequest: ApplePayPaymentRequest): boolean => !!paymentRequest && Object.keys(paymentRequest).length > 0),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (paymentRequest: ApplePayPaymentRequest): void => {
          this.applePaySession = this.worldpayApplepayService.createSession(paymentRequest);
        }
      });

    this.worldpayApplepayService.getMerchantSessionFromState()
      .pipe(
        filter((session: PlaceOrderResponse): boolean => !!session && Object.keys(session).length > 0),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (session: PlaceOrderResponse): void => {
          this.applePaySession.completeMerchantValidation(session);
        }
      });

    this.worldpayApplepayService.getPaymentAuthorizationFromState()
      .pipe(
        filter((authorization: ApplePayAuthorization): boolean => !!authorization && Object.keys(authorization).length > 0),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (authorization: ApplePayAuthorization): void => {
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          const ApplePaySession: any = this.worldpayApplepayService.getApplePaySessionFromWindow();
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          const statusCode: any =
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
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (): void => {
          this.routingService.go({ cxRoute: 'orderConfirmation' });
        }
      });
  }

  /**
   * Emits an event to navigate back to the previous step or screen.
   * This method triggers the `back` event emitter.
   * @since 2211.43.0
   */
  onBack(): void {
    this.back.emit();
  }

  /**
   * If ApplePay is available, we request the payment request and wait for response.
   * When the response arrives, we show the ApplePay button
   */
  private initializeApplePay(): void {
    if (this.worldpayApplepayService.applePayButtonAvailable()) {
      this.enableApplePayButton$ = this.worldpayApplepayService.enableApplePayButton()
        .pipe(
          filter((paymentRequest: ApplePayPaymentRequest): boolean => !!paymentRequest && Object.keys(paymentRequest).length > 0),
          takeUntilDestroyed(this.destroyRef)
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
