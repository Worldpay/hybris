import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Input, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { BehaviorSubject, combineLatest, Observable, of, Subject } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { filter, first, switchMap, take, takeUntil } from 'rxjs/operators';
import { LoadScriptService } from '../../../../core/utils/load-script.service';
import { ActiveCartService, Cart, GlobalMessageService, GlobalMessageType, RoutingService, WindowRef } from '@spartacus/core';
import { makeFormErrorsVisible } from '../../../../core/utils/make-form-errors-visible';
import { WorldpayGooglepayService } from '../../../../core/services/worldpay-googlepay/worldpay-googlepay.service';
import { WorldpayCheckoutPaymentService } from '../../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayCheckoutService } from '../../../../core/services/worldpay-checkout/worldpay-checkout.service';
import { ApmData, GooglePayMerchantConfiguration } from '../../../../core/interfaces';
import { CheckoutService } from '@spartacus/checkout/core';

@Component({
  selector: 'y-worldpay-apm-googlepay',
  templateUrl: './worldpay-apm-googlepay.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorldpayApmGooglepayComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() apm: ApmData;
  @Input() billingAddressForm: FormGroup = new FormGroup({});
  @ViewChild('gpayBtn') gpayBtn: ElementRef = null;

  public nativeWindow = this.winRef.nativeWindow;
  public sameAsShippingAddress$ = new BehaviorSubject<boolean>(true);
  private paymentsClient: any;
  private drop = new Subject();

  error: Observable<string> = of('');

  constructor(
    protected activeCartService: ActiveCartService,
    protected checkoutService: CheckoutService,
    protected routingService: RoutingService,
    protected globalMessageService: GlobalMessageService,
    protected worldpayGooglePayService: WorldpayGooglepayService,
    protected worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService,
    protected worldpayCheckoutService: WorldpayCheckoutService,
    protected scriptService: LoadScriptService,
    private ngZone: NgZone,
    protected cd: ChangeDetectorRef,
    protected winRef: WindowRef,
  ) {

  }

  ngOnInit(): void {
    this.checkoutService.getOrderDetails().pipe(
      filter(order => Object.keys(order).length !== 0), takeUntil(this.drop)
    ).subscribe(() => {
      this.routingService.go({ cxRoute: 'orderConfirmation' });
    });
  }

  ngAfterViewInit(): void {
    if (this.gpayBtn?.nativeElement) {
      this.initBtn();
    }
  }

  private initBtn(): void {
    this.worldpayGooglePayService.getMerchantConfigurationFromState()
      .pipe(
        take(1),
        takeUntil(this.drop)
      )
      .subscribe((merchantConfiguration) => {
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
      });
  }

  private initPaymentsClient(merchantConfiguration): void {
    // @ts-ignore
    this.paymentsClient = new google.payments.api.PaymentsClient(merchantConfiguration.clientSettings);
    const isReadyToPayRequest: any = this.worldpayGooglePayService.createInitialPaymentRequest(merchantConfiguration);
    this.paymentsClient.isReadyToPay(isReadyToPayRequest)
      .then(({ result }) => {
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
      .catch(err => {
        console.error('failed to initialize googlepay', err);
        this.error = of(err.statusMessage);
        this.globalMessageService.add(
          {
            raw: err.statusMessage,
          },
          GlobalMessageType.MSG_TYPE_ERROR);
        this.cd.detectChanges();
      });
  }

  ngOnDestroy(): void {
    this.drop.next();
  }

  private authorisePayment(): void {
    const configReq: Observable<GooglePayMerchantConfiguration> = this.worldpayGooglePayService.getMerchantConfigurationFromState()
      .pipe(first(c => !!c));
    let req;
    if (this.sameAsShippingAddress$.value) {
      req = configReq;
    } else {
      if (!this.billingAddressForm.valid) {
        makeFormErrorsVisible(this.billingAddressForm);
        return;
      }
      req = this.worldpayCheckoutPaymentService.setPaymentAddress(this.billingAddressForm.value)
        .pipe(switchMap(() => configReq));
    }
    combineLatest([
      this.activeCartService.getActive(),
      req
    ]).pipe(
      filter(([cart, merchantConfig]) => !!cart && !!merchantConfig),
      take(1),
      takeUntil(this.drop)
    ).subscribe(([cart, merchantConfiguration]: [Cart, GooglePayMerchantConfiguration]) => {
      this.worldpayCheckoutService.startLoading();

      const paymentDataRequest: any = this.worldpayGooglePayService
        .createFullPaymentRequest(merchantConfiguration, cart);

      this.paymentsClient.loadPaymentData(paymentDataRequest)
        .then((paymentRequest) => {
          this.worldpayGooglePayService.authoriseOrder(paymentRequest, false);
        })
        .catch((error) => {
          console.log('failed processing googlepay', { error });
          this.globalMessageService.add({ key: 'paymentForm.googlepay.authorisationFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
          this.worldpayGooglePayService.canceledPaymentRequest(error);
        });
    });
  }
}
