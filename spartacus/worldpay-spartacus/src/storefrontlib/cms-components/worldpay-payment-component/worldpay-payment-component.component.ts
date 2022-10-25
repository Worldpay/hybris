import { Component, HostBinding, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActiveCartService, Address, CurrencyService, GlobalMessageService, GlobalMessageType, PaymentDetails, TranslationService, UserPaymentService } from '@spartacus/core';
import { Card } from '@spartacus/storefront';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WorldpayCheckoutPaymentService } from '../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';
import { filter, finalize, switchMap, take, takeUntil } from 'rxjs/operators';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { WorldpayApmService } from '../../../core/services/worldpay-apm/worldpay-apm.service';
import { ApmData, ApmPaymentDetails, PaymentMethod } from '../../../core/interfaces';
import { CheckoutStepService, PaymentMethodComponent } from '@spartacus/checkout/components';
import { CheckoutDeliveryService, CheckoutService } from '@spartacus/checkout/core';
import { CheckoutStepType } from '@spartacus/checkout/root';

@Component({
  selector: 'y-worldpay-payment-component',
  templateUrl: './worldpay-payment-component.component.html',
  styleUrls: ['worldpay-payment-component.component.scss']
})
export class WorldpayPaymentComponent extends PaymentMethodComponent implements OnInit, OnDestroy {
  @HostBinding('class.d-none') hidden: boolean = false;
  apms$: Observable<ApmData[]> = this.worldpayApmService.getWorldpayAvailableApmsFromState();

  cvnForm: FormGroup = this.fb.group({
    cvn: ['', Validators.required]
  });
  selectedPayment: PaymentDetails;
  isCardPayment: boolean;

  private worldpayDeliveryAddress: Address;
  private drop = new Subject<void>();
  public processing$ = new BehaviorSubject<boolean>(false);

  constructor(
    protected userPaymentService: UserPaymentService,
    protected checkoutService: CheckoutService,
    protected checkoutDeliveryService: CheckoutDeliveryService,
    protected checkoutPaymentService: WorldpayCheckoutPaymentService,
    protected globalMessageService: GlobalMessageService,
    protected checkoutStepService: CheckoutStepService,
    protected activatedRoute: ActivatedRoute,
    protected translation: TranslationService,
    protected activeCartService: ActiveCartService,
    protected fb: FormBuilder,
    protected worldpayApmService: WorldpayApmService,
    protected currencyService: CurrencyService,
  ) {
    super(
      userPaymentService,
      checkoutService,
      checkoutDeliveryService,
      checkoutPaymentService,
      globalMessageService,
      activatedRoute,
      translation,
      activeCartService,
      checkoutStepService
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.checkoutPaymentService.getPublicKey();

    this.checkoutDeliveryService
      .getDeliveryAddress()
      .pipe(
        take(1),
        takeUntil(this.drop)
      )
      .subscribe((address: Address) => {
        this.worldpayDeliveryAddress = address;
      });

    this.worldpayApmService.getSelectedAPMFromState().pipe(
      filter(Boolean),
      takeUntil(this.drop)
    ).subscribe((apm: ApmData) => {
      this.isCardPayment = apm.code === PaymentMethod.Card;
    });

    this.currencyService.getActive().pipe(
      takeUntil(this.drop)
    ).subscribe(() => {
      this.worldpayApmService.requestAvailableApms();
    });

    this.listenToAvailableApmsAndProtectSelectedApm();
  }

  setPaymentDetails({
    paymentDetails,
    billingAddress
  }: {
    paymentDetails: PaymentDetails;
    billingAddress?: Address;
  }): void {
    this.shouldRedirect = true;

    const details: PaymentDetails = { ...paymentDetails };
    details.billingAddress = billingAddress || this.worldpayDeliveryAddress;

    this.checkoutPaymentService.setPaymentAddress(details.billingAddress)
      .pipe(takeUntil(this.drop))
      .subscribe()
      .unsubscribe();

    this.checkoutPaymentService.createPaymentDetails(details);

    this.hideNewPaymentForm();
  }

  protected createCard(
    paymentDetails: PaymentDetails,
    cardLabels: {
      textDefaultPaymentMethod: string;
      textExpires: string;
      textUseThisPayment: string;
      textSelected: string;
    },
    selected: PaymentDetails
  ): Card {
    return {
      title: paymentDetails.defaultPayment
        ? cardLabels.textDefaultPaymentMethod
        : '',
      textBold: paymentDetails.accountHolderName,
      text: [paymentDetails.cardNumber, cardLabels.textExpires],
      img: this.getCardIcon(paymentDetails?.cardType?.code),
      actions: [{
        name: cardLabels.textUseThisPayment,
        event: 'send'
      }],
      header:
        selected?.id === paymentDetails.id ? cardLabels.textSelected : undefined
    };
  }

  selectPaymentMethod(paymentDetails: PaymentDetails): void {
    const details: PaymentDetails = {
      ...paymentDetails,
      cvn: this.cvnForm.value.cvn
    };
    this.checkoutPaymentService.setPaymentAddress(details.billingAddress)
      .pipe(takeUntil(this.drop))
      .subscribe();
    this.checkoutPaymentService.useExistingPaymentDetails(details);
    this.selectedPayment = details;
  }

  getCheckoutStepUrl(stepType: CheckoutStepType): string {
    const step = this.checkoutStepService.getCheckoutStep(stepType);
    return step && step.routeName;
  }

  setApmPaymentDetails($event: { paymentDetails: ApmPaymentDetails; billingAddress: Address }): void {
    this.shouldRedirect = true;

    let billingAddress = $event.billingAddress;
    if (billingAddress == null) {
      billingAddress = this.worldpayDeliveryAddress;
    }

    const details = {
      ...$event.paymentDetails,
      billingAddress
    };

    this.checkoutPaymentService.setPaymentAddress(billingAddress)
      .pipe(
        switchMap(() => this.checkoutPaymentService.setApmPaymentDetails(details)),
        finalize(() => this.processing$.next(false)),
        takeUntil(this.drop),
      )
      .subscribe();
  }

  /**
   * Prevent selected APM not be in the list of available APM's.
   * Will rollback to Card if current selected APM is not available in new context
   */
  protected listenToAvailableApmsAndProtectSelectedApm(): void {
    combineLatest([
      this.worldpayApmService.getWorldpayAvailableApmsFromState(),
      this.worldpayApmService.getSelectedAPMFromState()
    ])
      .pipe(
        filter(([apms, selectedApm]) =>
          !!apms && apms.length > 0 &&
          !!selectedApm && (
            selectedApm.code !== PaymentMethod.Card
          && selectedApm.code !== PaymentMethod.ApplePay
          && selectedApm.code !== PaymentMethod.GooglePay
          )),
        takeUntil(this.drop),
      )
      .subscribe(([apms, selectedApm]) => {
        const apm = apms.find(({ code }) => code === selectedApm.code);

        if (!apm) {
          this.globalMessageService.add({ key: 'paymentForm.apmChanged' }, GlobalMessageType.MSG_TYPE_ERROR);

          this.worldpayApmService.selectAPM({
            code: PaymentMethod.Card
          });
        }
      });
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
    this.drop.next();
  }
}
