import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  ElementRef,
  inject,
  Input,
  NgZone,
  OnInit,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActiveCartService } from '@spartacus/cart/base/core';
import { Cart } from '@spartacus/cart/base/root';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { EventService, GlobalMessageService, GlobalMessageType, LoggerService, RoutingService, WindowRef } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { WorldpayCheckoutPaymentService } from '@worldpay-services/worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayGooglepayService } from '@worldpay-services/worldpay-googlepay/worldpay-googlepay.service';
import { WorldpayOrderService } from '@worldpay-services/worldpay-order/worldpay-order.service';
import { LoadScriptService } from '@worldpay-utils/load-script.service';
import { makeFormErrorsVisible } from '@worldpay-utils/make-form-errors-visible';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { filter, first, switchMap, take } from 'rxjs/operators';
import { ApmData, GooglePayMerchantConfiguration, GooglepayPaymentRequest, OCCResponse } from '../../../../core/interfaces';

@Component({
  selector: 'y-worldpay-apm-googlepay',
  templateUrl: './worldpay-apm-googlepay.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorldpayApmGooglepayComponent implements OnInit, AfterViewInit {
  @Input() apm: ApmData;
  @ViewChild('gpayBtn', { static: false }) gpayBtn: ElementRef = null;

  public nativeWindow: Window = this.winRef.nativeWindow;
  protected error$: BehaviorSubject<string> = new BehaviorSubject<string>(null);
  /**
   * Injects the CheckoutBillingAddressFormService into the component.
   * This service is used to manage the billing address form in the checkout process.
   * @protected
   * @since 2211.27.0
   */
  protected billingAddressFormService: CheckoutBillingAddressFormService = inject(
    CheckoutBillingAddressFormService
  );
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private paymentsClient: any;
  private destroyRef: DestroyRef = inject(DestroyRef);
  private logger: LoggerService = inject(LoggerService);

  /**
   * Constructor
   *
   * Initializes the WorldpayApmGooglepayComponent with the necessary services.
   *
   * @param {ActiveCartService} activeCartService - Service to manage the active cart.
   * @param {RoutingService} routingService - Service to handle routing.
   * @param {GlobalMessageService} globalMessageService - Service to display global messages.
   * @param {LoadScriptService} scriptService - Service to load external scripts.
   * @param {EventService} eventService - Service to handle events.
   * @param {NgZone} ngZone - Service to execute code inside or outside of Angular's zone.
   * @param {ChangeDetectorRef} cd - Service to detect changes.
   * @param {WindowRef} winRef - Service to access the window object.
   * @param {ViewContainerRef} vcr - Service to access the view container.
   * @param {WorldpayOrderService} worldpayOrderService - Service to manage Worldpay orders.
   * @param {WorldpayCheckoutPaymentService} worldpayCheckoutPaymentService - Service to manage Worldpay checkout payments.
   * @param {WorldpayGooglepayService} worldpayGooglePayService - Service to manage Worldpay Google Pay.
   */
  constructor(
    protected activeCartService: ActiveCartService,
    protected routingService: RoutingService,
    protected globalMessageService: GlobalMessageService,
    protected scriptService: LoadScriptService,
    protected eventService: EventService,
    private ngZone: NgZone,
    protected cd: ChangeDetectorRef,
    protected winRef: WindowRef,
    protected vcr: ViewContainerRef,
    protected worldpayOrderService: WorldpayOrderService,
    protected worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService,
    protected worldpayGooglePayService: WorldpayGooglepayService,
  ) {
  }

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
                onloadCallback: () => {
                  this.ngZone.run(() => {
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
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        if (result) {
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          const button: any = this.paymentsClient.createButton({
            onClick: () => {
              this.ngZone.run(() => {
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
      .pipe(first((googlePayMerchantConfiguration: GooglePayMerchantConfiguration) => !!googlePayMerchantConfiguration));
    let req: Observable<GooglePayMerchantConfiguration>;

    if (this.billingAddressFormService.isBillingAddressSameAsDeliveryAddress()) {
      req = configReq;
    } else {
      if (!this.billingAddressFormService.isBillingAddressFormValid()) {
        makeFormErrorsVisible(this.billingAddressFormService.getBillingAddressForm());
        return;
      }
      req = this.worldpayCheckoutPaymentService.setPaymentAddress(this.billingAddressFormService.getBillingAddress())
        .pipe(switchMap((): Observable<GooglePayMerchantConfiguration> => configReq));
    }
    combineLatest([
      this.activeCartService.getActive(),
      req
    ]).pipe(
      filter(([cart, merchantConfig]: [Cart, GooglePayMerchantConfiguration]): boolean => !!cart && !!merchantConfig),
      take(1),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: ([cart, merchantConfiguration]: [Cart, GooglePayMerchantConfiguration]): void => {
        this.worldpayOrderService.startLoading(this.vcr);

        const paymentDataRequest: GooglepayPaymentRequest = this.worldpayGooglePayService.createFullPaymentRequest(merchantConfiguration, cart);

        this.paymentsClient.loadPaymentData(paymentDataRequest)
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          .then((paymentRequest: any): void => {
            this.worldpayGooglePayService.authoriseOrder(paymentRequest, false);
          })
          .catch((error: unknown): void => {
            this.logger.error('failed processing googlepay', { error });
            this.globalMessageService.add({ key: 'paymentForm.googlepay.authorisationFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
            this.worldpayOrderService.clearLoading();
          });
      },
      error: (): void => {
        this.worldpayOrderService.clearLoading();
      }
    });
  }
}
