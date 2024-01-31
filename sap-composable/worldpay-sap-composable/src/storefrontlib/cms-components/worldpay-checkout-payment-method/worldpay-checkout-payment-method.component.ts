import { Component, HostBinding, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Address, getLastValueSync, GlobalMessageService, GlobalMessageType, TranslationService, UserPaymentService } from '@spartacus/core';
import { Card } from '@spartacus/storefront';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { distinctUntilChanged, filter, switchMap, takeUntil } from 'rxjs/operators';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { WorldpayApmService } from '../../../core/services/worldpay-apm/worldpay-apm.service';
import { ApmData, ApmPaymentDetails, PaymentMethod } from '../../../core/interfaces';
import { CheckoutPaymentMethodComponent, CheckoutStepService } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressFacade, CheckoutStep, CheckoutStepType } from '@spartacus/checkout/base/root';
import { ActiveCartFacade, PaymentDetails } from '@spartacus/cart/base/root';
import { WorldpayCheckoutPaymentService } from '../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';

@Component({
  selector: 'y-worldpay-payment',
  templateUrl: './worldpay-checkout-payment-method.component.html',
  styleUrls: ['worldpay-checkout-payment-method.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class WorldpayCheckoutPaymentMethodComponent extends CheckoutPaymentMethodComponent implements OnInit, OnDestroy {
  @HostBinding('class.d-none') hidden: boolean = false;
  apms$: Observable<ApmData[]> = this.worldpayApmService.getWorldpayAvailableApms();
  shouldRedirect: boolean;

  cvnForm: UntypedFormGroup = this.fb.group({
    cvn: [null, [Validators.required, Validators.minLength(3)]]
  });
  selectedPayment$: BehaviorSubject<PaymentDetails> = new BehaviorSubject<PaymentDetails>(null);
  isCardPayment: boolean;

  private drop: Subject<void> = new Subject<void>();
  public processing$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  /**
   * Constructor
   * @param userPaymentService UserPaymentService
   * @param checkoutDeliveryAddressFacade CheckoutDeliveryAddressFacade
   * @param checkoutPaymentFacade WorldpayCheckoutPaymentService
   * @param activatedRoute ActivatedRoute
   * @param translationService TranslationService
   * @param activeCartFacade ActiveCartFacade
   * @param checkoutStepService CheckoutStepService
   * @param globalMessageService GlobalMessageService
   * @param fb UntypedFormBuilder
   * @param worldpayApmService WorldpayApmService
   */
  constructor(
    protected override userPaymentService: UserPaymentService,
    protected override checkoutDeliveryAddressFacade: CheckoutDeliveryAddressFacade,
    protected override checkoutPaymentFacade: WorldpayCheckoutPaymentService,
    protected override activatedRoute: ActivatedRoute,
    protected override translationService: TranslationService,
    protected override activeCartFacade: ActiveCartFacade,
    protected override checkoutStepService: CheckoutStepService,
    protected override globalMessageService: GlobalMessageService,
    protected fb: UntypedFormBuilder,
    protected worldpayApmService: WorldpayApmService,
  ) {
    super(
      userPaymentService,
      checkoutDeliveryAddressFacade,
      checkoutPaymentFacade,
      activatedRoute,
      translationService,
      activeCartFacade,
      checkoutStepService,
      globalMessageService,
    );
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.shouldRedirect = false;
    this.checkoutPaymentFacade.getPublicKey().pipe(takeUntil(this.drop)).subscribe();

    this.worldpayApmService.getSelectedAPMFromState().pipe(
      filter(Boolean),
      distinctUntilChanged(),
      takeUntil(this.drop)
    ).subscribe({
      next: (apm: ApmData): void => {
        this.isCardPayment = apm.code === PaymentMethod.Card;
      },
      error: (error: unknown): void => {
        this.globalMessageService.add({ raw: error as string }, GlobalMessageType.MSG_TYPE_ERROR);
      }
    });

    this.listenToAvailableApmsAndProtectSelectedApm();
  }

  /**
   * Set payment details
   * @since 4.3.6
   * @param paymentDetails
   * @param billingAddress
   */
  override setPaymentDetails({
    paymentDetails,
    billingAddress
  }: {
    paymentDetails: PaymentDetails;
    billingAddress?: Address;
  }): void {
    this.busy$.next(true);
    this.shouldRedirect = true;

    const details: PaymentDetails = { ...paymentDetails };
    details.billingAddress = billingAddress || this.deliveryAddress;

    this.checkoutPaymentFacade.createPaymentDetails(details)
      .pipe(takeUntil(this.drop))
      .subscribe({
        next: (response: PaymentDetails): void => {
          if (!response) {
            this.globalMessageService.add({ key: 'checkoutReview.tokenizationFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
            this.onError();
            return;
          }
          this.checkoutPaymentFacade.setSaveCreditCardValue(details.save);
          this.checkoutPaymentFacade.setSaveAsDefaultCardValue(details.defaultPayment);
          // we don't call onSuccess here, because it can cause a spinner flickering
          this.hideNewPaymentForm();
          this.next();
        },
        error: (): void => {
          this.globalMessageService.add({ key: 'paymentForm.invalid.applicationError' }, GlobalMessageType.MSG_TYPE_ERROR);
          this.onError();
        },
      });
  }

  /**
   * Create card
   * @since 4.3.6
   * @param paymentDetails
   * @param cardLabels
   * @param selected
   */
  protected override createCard(
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
      role: 'region',
      title: paymentDetails.defaultPayment ? cardLabels.textDefaultPaymentMethod : '',
      textBold: paymentDetails.accountHolderName,
      text: [paymentDetails.cardNumber, cardLabels.textExpires],
      img: this.getCardIcon(paymentDetails?.cardType?.code),
      actions: [{
        name: cardLabels.textUseThisPayment,
        event: 'send'
      }],
      header: selected?.id === paymentDetails.id ? cardLabels.textSelected : undefined,
      label: paymentDetails.defaultPayment ? 'paymentCard.defaultPaymentLabel' : 'paymentCard.additionalPaymentLabel',
    };
  }

  setSelectedPayment(paymentDetails: PaymentDetails): void {
    if (paymentDetails?.save) {
      this.checkoutPaymentFacade.setSaveCreditCardValue(paymentDetails.save);
    } else {
      this.checkoutPaymentFacade.setSaveCreditCardValue(paymentDetails.saved);
    }
    this.checkoutPaymentFacade.setSaveAsDefaultCardValue(paymentDetails.defaultPayment);
    this.selectedPayment$.next(paymentDetails);
  }

  /**
   * Select payment method
   * @since 4.3.6
   * @param paymentDetails
   */
  override selectPaymentMethod(paymentDetails: PaymentDetails): void {

    const details: PaymentDetails = {
      ...paymentDetails,
      cvn: this.cvnForm.value.cvn
    };

    if (details.defaultPayment === true) {
      details.save = true;
    }

    if (paymentDetails?.id === getLastValueSync(this.selectedMethod$)?.id) {
      this.setSelectedPayment(details);
      return;
    }

    this.checkoutPaymentFacade.setPaymentAddress(details.billingAddress)
      .pipe(takeUntil(this.drop))
      .subscribe();

    this.checkoutPaymentFacade.useExistingPaymentDetails(details)
      .pipe(takeUntil(this.drop))
      .subscribe();

    this.setSelectedPayment(details);
  }

  getCheckoutStepUrl(stepType: CheckoutStepType): string {
    const step: CheckoutStep = this.checkoutStepService.getCheckoutStep(stepType);
    return step && step.routeName;
  }

  setApmPaymentDetails($event: { paymentDetails: ApmPaymentDetails; billingAddress: Address }): void {
    this.shouldRedirect = true;

    const billingAddress: Address = $event.billingAddress ?? this.deliveryAddress;

    const apmPaymentDetails = {
      ...$event.paymentDetails,
      billingAddress
    };

    this.busy$.next(true);
    this.checkoutPaymentFacade.setPaymentAddress(billingAddress)
      .pipe(
        switchMap(() => this.worldpayApmService.setApmPaymentDetails(apmPaymentDetails)),
        takeUntil(this.drop),
      )
      .subscribe({
        next: (): void => {
          this.onSuccess();
          this.next();
        },
        error: () => this.onError(),
      });
  }

  /**
   * Prevent selected APM not be in the list of available APM's.
   * Will rollback to Card if current selected APM is not available in new context
   */
  protected listenToAvailableApmsAndProtectSelectedApm(): void {
    combineLatest([
      this.worldpayApmService.getWorldpayAvailableApms(),
      this.worldpayApmService.getSelectedAPMFromState()
    ])
      .pipe(
        filter(([apms, selectedApm]) =>
          !!apms && apms.length > 0 &&
          !!selectedApm &&
          selectedApm.code !== PaymentMethod.Card &&
          selectedApm.code !== PaymentMethod.ApplePay &&
          selectedApm.code !== PaymentMethod.GooglePay &&
          selectedApm.code !== PaymentMethod.PayPal
        ),
        distinctUntilChanged(),
        takeUntil(this.drop),
      )
      .subscribe({
        next: ([apms, selectedApm]) => {
          const apm: ApmData = apms.find(({ code }): boolean => code === selectedApm.code);
          if (!apm) {
            this.globalMessageService.add({ key: 'paymentForm.apmChanged' }, GlobalMessageType.MSG_TYPE_ERROR);
            this.worldpayApmService.selectAPM({
              code: PaymentMethod.Card
            });
          }
        },
        error: (error: unknown): void => {
          this.globalMessageService.add({ raw: error as string }, GlobalMessageType.MSG_TYPE_ERROR);
        }
      });
  }

  override selectDefaultPaymentMethod(
    paymentMethods: { payment: PaymentDetails; expiryTranslation: string }[],
    selectedMethod: PaymentDetails | undefined
  ): void {
    if (
      !this.doneAutoSelect &&
      paymentMethods?.length &&
      (!selectedMethod || Object.keys(selectedMethod).length === 0)
    ) {
      const defaultPaymentMethod = paymentMethods.find(
        (paymentMethod) => paymentMethod.payment.defaultPayment
      );
      if (defaultPaymentMethod) {
        selectedMethod = defaultPaymentMethod.payment;
        this.selectPaymentMethod(selectedMethod);
        this.savePaymentMethod(selectedMethod);
      }
      this.doneAutoSelect = true;
    }
    this.selectPaymentMethod(selectedMethod);
  }

  override ngOnDestroy(): void {
    super.ngOnDestroy();
    this.drop.next();
    this.drop.complete();
  }
}
