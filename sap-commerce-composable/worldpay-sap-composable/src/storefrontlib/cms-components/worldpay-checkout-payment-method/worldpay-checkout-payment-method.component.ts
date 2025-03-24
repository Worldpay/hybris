import { Component, DestroyRef, HostBinding, inject, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { CheckoutPaymentMethodComponent, CheckoutStepService } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressFacade, CheckoutStep, CheckoutStepType } from '@spartacus/checkout/base/root';
import { Address, getLastValueSync, GlobalMessageService, GlobalMessageType, PaymentDetails, TranslationService, UserPaymentService } from '@spartacus/core';
import { Card } from '@spartacus/storefront';
import { WorldpayApmService } from '@worldpay-services/worldpay-apm/worldpay-apm.service';
import { WorldpayCheckoutPaymentService } from '@worldpay-services/worldpay-checkout/worldpay-checkout-payment.service';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { distinctUntilChanged, filter, switchMap } from 'rxjs/operators';
import { ApmData, ApmPaymentDetails, PaymentMethod } from '../../../core/interfaces';

@Component({
  selector: 'y-worldpay-payment',
  templateUrl: './worldpay-checkout-payment-method.component.html',
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
  public processing$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  private destroyRef: DestroyRef = inject(DestroyRef);

  /**
   * Constructor for the WorldpayCheckoutPaymentMethodComponent.
   *
   * @param userPaymentService - Service to handle user payment methods.
   * @param checkoutDeliveryAddressFacade - Facade to manage checkout delivery addresses.
   * @param checkoutPaymentFacade - Facade to manage checkout payment methods.
   * @param activatedRoute - Service to handle the current active route.
   * @param translationService - Service to handle translations.
   * @param activeCartFacade - Facade to manage the active cart.
   * @param checkoutStepService - Service to manage checkout steps.
   * @param globalMessageService - Service to handle global messages.
   * @param fb - FormBuilder to create and manage forms.
   * @param worldpayApmService - Service to handle Worldpay APM (Alternative Payment Methods).
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

  /**
   * Initializes the component and sets up necessary observables and subscriptions.
   *
   * @override
   */
  override ngOnInit(): void {
    super.ngOnInit();
    this.shouldRedirect = false;
    this.checkoutPaymentFacade.getPublicKey().pipe(takeUntilDestroyed(this.destroyRef)).subscribe();

    this.worldpayApmService.getSelectedAPMFromState().pipe(
      filter(Boolean),
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef)
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
   * Sets the payment details and handles the response.
   *
   * @param {Object} params - The parameters object.
   * @param {PaymentDetails} params.paymentDetails - The payment details to be set.
   * @param {Address} [params.billingAddress] - The billing address associated with the payment details.
   * @override
   * @since 6.4.0
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
      .pipe(takeUntilDestroyed(this.destroyRef))
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
   * Sets the selected payment details and updates the relevant values.
   *
   * @param {PaymentDetails} paymentDetails - The payment details to be set.
   * @since 6.4.0
   */
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
   * Selects the payment method and updates the relevant values.
   *
   * @param {PaymentDetails} paymentDetails - The payment details to be selected.
   * @override
   * @since 6.4.0
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
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe();

    this.checkoutPaymentFacade.useExistingPaymentDetails(details)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe();

    this.setSelectedPayment(details);
  }

  /**
   * Retrieves the URL for the specified checkout step type.
   *
   * @param {CheckoutStepType} stepType - The type of the checkout step.
   * @returns {string} - The URL of the checkout step.
   * @since 6.4.0
   */
  getCheckoutStepUrl(stepType: CheckoutStepType): string {
    const step: CheckoutStep = this.checkoutStepService.getCheckoutStep(stepType);
    return step && step.routeName;
  }

  /**
   * Sets the APM (Alternative Payment Method) payment details and handles the response.
   *
   * @param {Object} $event - The event object containing payment details and billing address.
   * @param {ApmPaymentDetails} $event.paymentDetails - The APM payment details to be set.
   * @param {Address} $event.billingAddress - The billing address associated with the payment details.
   */
  setApmPaymentDetails($event: { paymentDetails: ApmPaymentDetails; billingAddress: Address }): void {
    this.shouldRedirect = true;

    const billingAddress: Address = $event.billingAddress ?? this.deliveryAddress;

    const apmPaymentDetails: ApmPaymentDetails = {
      ...$event.paymentDetails,
      billingAddress
    };

    this.busy$.next(true);
    this.checkoutPaymentFacade.setPaymentAddress(billingAddress)
      .pipe(
        switchMap(() => this.worldpayApmService.setApmPaymentDetails(apmPaymentDetails)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (): void => {
          this.onSuccess();
          this.next();
        },
        error: () => this.onError(),
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
      const defaultPaymentMethod: { payment: PaymentDetails; expiryTranslation: string } = paymentMethods.find(
        (paymentMethod: { payment: PaymentDetails; expiryTranslation: string }): boolean => paymentMethod.payment.defaultPayment
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

  /**
   * Prevent selected APM not be in the list of available APM's.
   * Will rollback to Card if current selected APM is not available in new context
   * Listens to available APMs (Alternative Payment Methods) and ensures the selected APM is valid.
   * If the selected APM is not available, it rolls back to the default Card payment method.
   *
   * @protected
   * @since 6.4.0
   */
  protected listenToAvailableApmsAndProtectSelectedApm(): void {
    combineLatest([
      this.worldpayApmService.getWorldpayAvailableApms(),
      this.worldpayApmService.getSelectedAPMFromState()
    ])
      .pipe(
        filter(([apms, selectedApm]: [ApmData[], ApmPaymentDetails]): boolean =>
          !!apms && apms.length > 0 &&
          !!selectedApm &&
          selectedApm.code !== PaymentMethod.Card &&
          selectedApm.code !== PaymentMethod.ApplePay &&
          selectedApm.code !== PaymentMethod.GooglePay &&
          selectedApm.code !== PaymentMethod.PayPal
        ),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: ([apms, selectedApm]: [ApmData[], ApmPaymentDetails]): void => {
          const apm: ApmData = apms.find(({ code }: ApmData): boolean => code === selectedApm.code);
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
}
