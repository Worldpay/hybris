import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  inject,
  Input,
  NgZone,
  OnDestroy,
  OnInit,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { filter, first, switchMap, take, takeUntil } from 'rxjs/operators';
import { EventService, GlobalMessageService, GlobalMessageType, RoutingService, WindowRef } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { LoadScriptService } from '../../../../core/utils/load-script.service';
import { ActiveCartService } from '@spartacus/cart/base/core';
import { makeFormErrorsVisible } from '../../../../core/utils/make-form-errors-visible';
import { WorldpayGooglepayService } from '../../../../core/services/worldpay-googlepay/worldpay-googlepay.service';
import { WorldpayCheckoutPaymentService } from '../../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';
import { ApmData, GooglePayMerchantConfiguration, GooglepayPaymentRequest, OCCResponse } from '../../../../core/interfaces';
import { WorldpayOrderService } from '../../../../core/services';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';

@Component({
  selector: 'y-worldpay-apm-googlepay',
  templateUrl: './worldpay-apm-googlepay.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorldpayApmGooglepayComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() apm: ApmData;
  @ViewChild('gpayBtn', { static: false }) gpayBtn: ElementRef = null;

  public nativeWindow: Window = this.winRef.nativeWindow;
  protected error$: BehaviorSubject<string> = new BehaviorSubject<string>(null);
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private paymentsClient: any;
  private drop: Subject<void> = new Subject<void>();

  /**
   * Injects the CheckoutBillingAddressFormService into the component.
   * This service is used to manage the billing address form in the checkout process.
   * @protected
   * @since 2211.27.0
   */
  protected billingAddressFormService = inject(
    CheckoutBillingAddressFormService
  );

  /**
   * Constructor
   * @param activeCartService ActiveCartService
   * @param routingService RoutingService
   * @param globalMessageService GlobalMessageService
   * @param scriptService LoadScriptService
   * @param eventService EventService
   * @param ngZone NgZone
   * @param cd ChangeDetectorRef
   * @param winRef WindowRef
   * @param vcr ViewContainerRef
   * @param worldpayOrderService WorldpayOrderService
   * @param worldpayCheckoutPaymentService WorldpayCheckoutPaymentService
   * @param worldpayGooglePayService WorldpayGooglepayService
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

  ngOnInit(): void {
    this.worldpayOrderService.getOrderDetails().pipe(
      filter((order: Order): boolean => order && Object.keys(order).length !== 0),
      takeUntil(this.drop)
    ).subscribe({
      next: (): void => {
        this.routingService.go({ cxRoute: 'orderConfirmation' });
      }
    });
  }

  ngAfterViewInit(): void {
    if (this.gpayBtn?.nativeElement) {
      this.initBtn();
    }
  }

  /**
   * Initialize Google Pay button
   * @since 4.3.6
   */
  private initBtn(): void {
    this.worldpayGooglePayService.getMerchantConfigurationFromState()
      .pipe(
        take(1),
        takeUntil(this.drop)
      )
      .subscribe({
        next: (merchantConfiguration: GooglePayMerchantConfiguration): void => {
          if (this.paymentsClient) {
            return;
          }
          // @ts-ignore
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
        }
      });
  }

  /**
   * Initialize Google Pay client
   * @param merchantConfiguration GooglePayMerchantConfiguration
   */
  private initPaymentsClient(merchantConfiguration: GooglePayMerchantConfiguration): void {
    // @ts-ignore
    this.paymentsClient = new google.payments.api.PaymentsClient(merchantConfiguration.clientSettings);
    const isReadyToPayRequest: GooglepayPaymentRequest = this.worldpayGooglePayService.createInitialPaymentRequest(merchantConfiguration);
    this.paymentsClient.isReadyToPay(isReadyToPayRequest)
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      .then(({ result }: any): void => {
        if (result) {
          const button = this.paymentsClient.createButton({
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
        console.error('failed to initialize googlepay', err);
        this.error$.next(err.statusMessage);
        this.globalMessageService.add(
          { raw: err.statusMessage },
          GlobalMessageType.MSG_TYPE_ERROR);
        this.cd.detectChanges();
      });
  }

  ngOnDestroy(): void {
    this.drop.next();
    this.drop.complete();
  }

  /**
   * Authorise the payment
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
        .pipe(switchMap(() => configReq));
    }
    combineLatest([
      this.activeCartService.getActive(),
      req
    ]).pipe(
      filter(([cart, merchantConfig]) => !!cart && !!merchantConfig),
      take(1),
      takeUntil(this.drop)
    ).subscribe({
      next: ([cart, merchantConfiguration]): void => {
        this.worldpayOrderService.startLoading(this.vcr);

        const paymentDataRequest: GooglepayPaymentRequest = this.worldpayGooglePayService.createFullPaymentRequest(merchantConfiguration, cart);

        this.paymentsClient.loadPaymentData(paymentDataRequest)
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          .then((paymentRequest: any): void => {
            console.log('this is a test');
            this.worldpayGooglePayService.authoriseOrder(paymentRequest, false);
          })
          .catch((error: unknown): void => {
            console.log('failed processing googlepay', { error });
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
