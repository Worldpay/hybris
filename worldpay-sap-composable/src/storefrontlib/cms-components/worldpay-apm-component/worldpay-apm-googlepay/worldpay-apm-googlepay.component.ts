import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  ElementRef,
  EventEmitter,
  inject,
  NgZone,
  OnInit,
  Output,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActiveCartFacade, Cart } from '@spartacus/cart/base/root';
import { EventService, GlobalMessageService, GlobalMessageType, LoggerService, RoutingService, WindowRef } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { BehaviorSubject, EMPTY, finalize, forkJoin, from, Observable } from 'rxjs';
import { catchError, filter, first, map, switchMap, take, tap } from 'rxjs/operators';
import {
  GooglePayMerchantConfiguration,
  GooglePayPaymentRequest,
  GooglepayPaymentRequest,
  LoadScriptService,
  makeFormErrorsVisible,
  OCCResponse,
  WorldpayBillingAddressFormService,
  WorldpayCheckoutPaymentFacade,
  WorldpayGooglepayService,
  WorldpayOrderFacade
} from '../../../../core';
import { ClearGooglepayEvent, GooglePayMerchantConfigurationSetEvent } from '../../../../core/events';

/**
 * Google Pay APM component.
 *
 * Handles Google Pay button initialization, payment authorization flow,
 * billing address handling, and checkout navigation after successful order placement.
 *
 * @implements OnInit
 * @implements AfterViewInit
 * @since 4.3.6
 */
@Component({
  selector: 'y-worldpay-apm-googlepay',
  templateUrl: './worldpay-apm-googlepay.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false
})
export class WorldpayApmGooglepayComponent implements OnInit, AfterViewInit {
  @ViewChild('gpayBtn', { static: false }) gpayBtn: ElementRef = null;
  @Output() back: EventEmitter<void> = new EventEmitter<void>();
  protected activeCartFacade: ActiveCartFacade = inject(ActiveCartFacade);
  protected routingService: RoutingService = inject(RoutingService);
  protected globalMessageService: GlobalMessageService = inject(GlobalMessageService);
  protected scriptService: LoadScriptService = inject(LoadScriptService);
  protected eventService: EventService = inject(EventService);
  protected cd: ChangeDetectorRef = inject(ChangeDetectorRef);
  protected winRef: WindowRef = inject(WindowRef);
  public nativeWindow: Window = this.winRef.nativeWindow;
  protected vcr: ViewContainerRef = inject(ViewContainerRef);
  protected worldpayOrderService: WorldpayOrderFacade = inject(WorldpayOrderFacade);
  protected worldpayCheckoutPaymentFacade: WorldpayCheckoutPaymentFacade = inject(WorldpayCheckoutPaymentFacade);
  protected worldpayGooglePayService: WorldpayGooglepayService = inject(WorldpayGooglepayService);
  protected destroyRef: DestroyRef = inject(DestroyRef);
  protected error$: BehaviorSubject<string> = new BehaviorSubject<string>(null);
  /**
   * Injects the WorldpayBillingAddressFormService into the component.
   * This service is used to manage the billing address form in the checkout process.
   * @protected
   * @since 2211.43.0
   */
  protected billingAddressFormService: WorldpayBillingAddressFormService = inject(WorldpayBillingAddressFormService);
  private ngZone: NgZone = inject(NgZone);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private paymentsClient: any;
  private logger: LoggerService = inject(LoggerService);
  private isProcessingPayment: boolean = false;

  /**
   * Initializes the component and subscribes to order details.
   * If order details are retrieved and not empty, navigates to the order confirmation page.
   * @since 4.3.6
   */
  ngOnInit(): void {
    this.worldpayOrderService.getOrderDetails().pipe(
      filter((order: Order): boolean => order && Object.keys(order).length !== 0),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (): void => {
        this.routingService.go({ cxRoute: 'orderConfirmation' });
      },
      error: (error: unknown): void => {
        this.logger.error('Error while navigating to order confirmation page', error);
      }
    });
    this.onGooglePayMerchantConfigurationSetEvent();
    this.resetGooglePaySessionEvent();
  }

  /**
   * Lifecycle hook that is called after Angular has fully initialized a component's view.
   * Initializes the Google Pay button if the gpayBtn element is available.
   * @since 4.3.6
   */
  ngAfterViewInit(): void {
    if (this.gpayBtn?.nativeElement) {
      this.initBtn();
    }
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
   * Resets the Google Pay session when the `ClearGooglepayEvent` is triggered.
   *
   * This method listens for the `ClearGooglepayEvent` and clears the Google Pay
   * merchant configuration by setting it to `null` in the `googlePayMerchantConfiguration$` observable.
   *
   * The `takeUntilDestroyed` operator ensures that the subscription is automatically
   * unsubscribed when the `DestroyRef` is destroyed, preventing memory leaks.
   * @since 2211.43.0
   */
  resetGooglePaySessionEvent(): void {
    this.eventService.get(ClearGooglepayEvent).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (): void => {
        this.worldpayGooglePayService.setGooglepayMerchantConfiguration(null);
      }
    });
  }

  /**
   * Listen to GooglePayMerchantConfigurationSetEvent
   * @since 2211.43.0
   */
  protected onGooglePayMerchantConfigurationSetEvent(): void {
    this.eventService.get(GooglePayMerchantConfigurationSetEvent).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (event: GooglePayMerchantConfigurationSetEvent): void => {
        this.worldpayGooglePayService.setGooglepayMerchantConfiguration(event.googlePayMerchantConfiguration);
      }
    });
  }

  /**
   * Initializes the Google Pay button.
   * Retrieves the merchant configuration and initializes the PaymentsClient if available.
   * If the PaymentsClient is not available, loads the Google Pay script and initializes the PaymentsClient upon script load.
   * @private
   * @since 4.3.6
   */
  private initBtn(): void {
    this.worldpayGooglePayService.getMerchantConfigurationFromState()
      .pipe(
        take(1),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (merchantConfiguration: GooglePayMerchantConfiguration): void => {
          if (this.paymentsClient) {
            return;
          }
          // @ts-expect-error Property google does not exist on type Window
          if (this.nativeWindow?.google?.payments?.api?.PaymentsClient) {
            this.initPaymentsClient(merchantConfiguration);
          } else {
            this.scriptService.loadScript(
              {
                idScript: 'google-pay',
                src: 'https://pay.google.com/gp/p/js/pay.js',
                onloadCallback: (): void => {
                  this.ngZone.run((): void => {
                    this.initPaymentsClient(merchantConfiguration);
                  });
                }
              }
            );
          }
        },
        error: (error: unknown): void => {
          this.logger.error('Error while initializing Google Pay button', error);
        }
      });
  }

  /**
   * Initializes the Google Pay client with the provided merchant configuration.
   * Creates the Google Pay button and appends it to the gpayBtn element if the client is ready to pay.
   * Handles errors during the initialization process.
   * @param {GooglePayMerchantConfiguration} merchantConfiguration - The configuration for the Google Pay merchant.
   * @private
   * @since 4.3.6
   */
  private initPaymentsClient(merchantConfiguration: GooglePayMerchantConfiguration): void {
    // @ts-expect-error Property google does not exist on type Window
    this.paymentsClient = new google.payments.api.PaymentsClient(merchantConfiguration.clientSettings);
    const isReadyToPayRequest: GooglepayPaymentRequest = this.worldpayGooglePayService.createInitialPaymentRequest(merchantConfiguration);
    this.paymentsClient.isReadyToPay(isReadyToPayRequest)
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      .then(({ result }: any): void => {
        if (result) {
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          const button: any = this.paymentsClient.createButton({
            onClick: (): void => {
              this.ngZone.run((): void => {
                this.authorisePayment();
              });
            }
          });
          this.gpayBtn?.nativeElement.appendChild(button);
        }
      })
      .catch((err: OCCResponse): void => {
        this.logger.error('failed to initialize googlepay', err);
        this.error$.next(err.statusMessage);
        this.globalMessageService.add(
          { raw: err.statusMessage },
          GlobalMessageType.MSG_TYPE_ERROR);
        this.cd.detectChanges();
      });
  }

  /**
   * Authorizes the payment by creating a payment request and loading payment data.
   * If the billing address is not the same as the delivery address, it sets the payment address first.
   * Handles errors during the authorization process.
   * @private
   * @since 4.3.6
   */
  private authorisePayment(): void {
    const configReq: Observable<GooglePayMerchantConfiguration> = this.worldpayGooglePayService.getMerchantConfigurationFromState()
      .pipe(first((googlePayMerchantConfiguration: GooglePayMerchantConfiguration): boolean => !!googlePayMerchantConfiguration));
    let req: Observable<GooglePayMerchantConfiguration>;

    if (this.billingAddressFormService.isBillingAddressSameAsDeliveryAddress()) {
      req = configReq;
    } else {
      if (!this.billingAddressFormService.isBillingAddressFormValid()) {
        makeFormErrorsVisible(this.billingAddressFormService.getBillingAddressForm());
        return;
      }
      req = this.worldpayCheckoutPaymentFacade.setPaymentAddress(this.billingAddressFormService.getBillingAddressForm().value)
        .pipe(switchMap((): Observable<GooglePayMerchantConfiguration> => configReq));
    }

    forkJoin({
      cart: this.activeCartFacade.getActive().pipe(take(1)),
      merchantConfig: req.pipe(take(1))
    }).pipe(
      map(({
        cart,
        merchantConfig
      }: { cart: Cart, merchantConfig: GooglePayMerchantConfiguration }): GooglePayPaymentRequest => {
        if (!cart || !merchantConfig) {
          throw new Error('Checkout conditions not met');
        }
        return this.worldpayGooglePayService.createFullPaymentRequest(merchantConfig, cart);
      }),
      switchMap((paymentDataRequest: GooglePayPaymentRequest): Observable<GooglePayPaymentRequest> => {
        if (!paymentDataRequest || this.isProcessingPayment) {
          return EMPTY;
        }
        this.isProcessingPayment = true;
        return from(this.paymentsClient.loadPaymentData(paymentDataRequest)).pipe(
          tap((paymentRequest: GooglePayPaymentRequest): void => {
            this.worldpayOrderService.startLoading(this.vcr);
            this.worldpayGooglePayService.authoriseOrder(paymentRequest, false);
          }),
          catchError((error: unknown): Observable<never> => {
            this.logger.error('failed processing googlepay', { error });
            this.globalMessageService.add(
              { key: 'paymentForm.googlepay.authorisationFailed' },
              GlobalMessageType.MSG_TYPE_ERROR
            );
            return EMPTY;
          }),
          finalize((): void => {
            this.worldpayOrderService.clearLoading();
            this.isProcessingPayment = false;
          })
        );
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      error: (error: unknown): void => {
        this.worldpayOrderService.clearLoading();
        this.logger.error('authorisePayment with errors', { error });
      }
    });
  }
}
