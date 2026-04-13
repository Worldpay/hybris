import { Component, DestroyRef, HostBinding, inject, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ActiveCartFacade, Cart } from '@spartacus/cart/base/root';
import { CheckoutPaymentMethodComponent, CheckoutStepService } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressFacade, CheckoutStep, CheckoutStepType } from '@spartacus/checkout/base/root';
import { Address, getLastValueSync, GlobalMessageService, GlobalMessageType, PaymentDetails, TranslationService, UserPaymentService } from '@spartacus/core';
import { Card } from '@spartacus/storefront';
import { BehaviorSubject, combineLatest, Observable, startWith } from 'rxjs';
import { distinctUntilChanged, filter, map, switchMap } from 'rxjs/operators';
import {
  ApmData,
  ApmNormalizer,
  ApmPaymentDetails,
  createCreditCardCard,
  PaymentFormData,
  PaymentMethod,
  WorldpayApmPaymentInfo,
  WorldpayApmService,
  WorldpayBillingAddressFormService,
  WorldpayCard,
  WorldpayCheckoutPaymentService,
  worldpayGetCardIcon
} from '../../../core';

@Component({
  selector: 'y-worldpay-payment-method',
  templateUrl: './worldpay-checkout-payment-method.component.html',
  encapsulation: ViewEncapsulation.None,
  standalone: false,
})
export class WorldpayCheckoutPaymentMethodComponent extends CheckoutPaymentMethodComponent implements OnInit, OnDestroy {
  @HostBinding('class.d-none') hidden: boolean = false;
  apms$: Observable<ApmData[]> = this.worldpayApmService.getWorldpayAvailableApms();
  shouldRedirect: boolean;
  cvnForm: UntypedFormGroup = this.fb.group({
    cvn: [null, [Validators.required, Validators.minLength(3)]]
  });
  selectedPayment$: BehaviorSubject<ApmPaymentDetails> = this.worldpayApmService.selectedApm$;
  isCardPayment: boolean;
  public processing$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  protected apmNormalizer: ApmNormalizer = inject(ApmNormalizer);
  protected worldpayBillingAddressFormService: WorldpayBillingAddressFormService = inject(WorldpayBillingAddressFormService);
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

    const details: PaymentDetails = {
      ...paymentDetails,
      saved: paymentDetails.save
    };
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

    if (paymentDetails.billingAddress) {
      this.worldpayBillingAddressFormService.updateSameAsDeliveryAddressFormData(paymentDetails.billingAddress, this.deliveryAddress);
    }

    this.worldpayApmService.selectAPM(paymentDetails);
  }

  /**
   * Selects the payment method and updates the relevant values.
   *
   * @param {PaymentDetails} paymentDetails - The payment details to be selected.
   * @override
   * @since 6.4.0
   */
  override selectPaymentMethod(paymentDetails: WorldpayApmPaymentInfo): void {
    const normalizedPaymentDetails: ApmPaymentDetails = this.apmNormalizer.normalizeApmData(paymentDetails);
    const details: WorldpayApmPaymentInfo = {
      ...normalizedPaymentDetails,
      cvn: this.cvnForm.value.cvn
    };

    if (details.defaultPayment === true) {
      details.save = true;
    }

    const lastValueSync: WorldpayApmPaymentInfo = getLastValueSync(this.selectedMethod$);
    if (
      paymentDetails?.id === lastValueSync?.id ||
      !paymentDetails?.id && paymentDetails?.code === lastValueSync?.code
    ) {
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
  setApmPaymentDetails($event: PaymentFormData): void {
    this.shouldRedirect = true;

    interface ApmPaymentDetailsWithAddress extends ApmPaymentDetails {
      billingAddress: Address;
    }

    const billingAddress: Address = $event.billingAddress ?? this.deliveryAddress;

    const apmPaymentDetails: ApmPaymentDetailsWithAddress = {
      ...$event.paymentDetails,
      billingAddress
    };

    this.busy$.next(true);
    this.checkoutPaymentFacade.setPaymentAddress(billingAddress)
      .pipe(
        switchMap((): Observable<Cart> => this.worldpayApmService.setApmPaymentDetails(apmPaymentDetails)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (): void => {
          this.onSuccess();
          this.next();
        },
        error: (): void => this.onError(),
      });
  }

  override selectDefaultPaymentMethod(
    paymentMethods: { payment: ApmPaymentDetails; expiryTranslation: string }[],
    selectedMethod: ApmPaymentDetails | undefined
  ): void {
    if (!this.doneAutoSelect) {
      if (paymentMethods?.length && (!selectedMethod || Object.keys(selectedMethod).length === 0)) {
        const defaultPaymentMethod: { payment: ApmPaymentDetails; expiryTranslation: string } = paymentMethods.find(
          (paymentMethod: { payment: ApmPaymentDetails; expiryTranslation: string }): boolean => paymentMethod.payment.defaultPayment
        );
        if (defaultPaymentMethod) {
          selectedMethod = defaultPaymentMethod.payment;
          this.savePaymentMethod(selectedMethod);
        }
      }
      if (selectedMethod && Object.keys(selectedMethod)?.length > 0) {
        if (!selectedMethod.cardType) {
          selectedMethod = this.apmNormalizer.normalizeApmData(selectedMethod);
        }
        this.selectPaymentMethod(selectedMethod);
      }
      this.doneAutoSelect = true;
    }
  }

  disableContinueButton(): Observable<boolean> {
    return combineLatest([
      this.worldpayApmService.getWorldpayAvailableApms(),
      this.selectedMethod$,
      this.selectedPayment$,
      this.isUpdating$,
      this.cvnForm.valueChanges.pipe(
        startWith(this.cvnForm.value),
      )
    ]).pipe(
      // eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/no-unused-vars
      map(([availableApms, selectedMethod, selectedPayment, isUpdating, _cvnFormValue]: [ApmData[], ApmPaymentDetails, ApmPaymentDetails, boolean, any]): boolean => {
        const selectedMethodCode: string = selectedMethod?.code?.toLowerCase();
        const selectedPaymentCode: string = selectedPayment?.code?.toLowerCase();
        const creditCardCode: string = PaymentMethod.Card.toLowerCase();
        const isSelectedMethodAvailable: boolean = !selectedMethodCode || selectedMethodCode === creditCardCode ||
                                                   availableApms?.some((apm: ApmData): boolean => apm?.code?.toLowerCase() === selectedMethodCode);

        if (isUpdating) {
          return true;
        }

        if (!isSelectedMethodAvailable) {
          return true;
        }

        if (
          (selectedMethodCode === creditCardCode || selectedPaymentCode === creditCardCode) &&
          selectedMethod?.isAPM !== true
        ) {
          return !this.cvnForm.valid;
        }

        return !(selectedMethodCode || selectedPaymentCode);
      })
    );
  }

  /**
   * Create card
   * @since 4.3.6
   * @param paymentDetails
   * @param cardLabels
   * @param selected
   */
  protected override createCard(
    paymentDetails: WorldpayApmPaymentInfo,
    cardLabels: {
      textDefaultPaymentMethod: string;
      textExpires: string;
      textUseThisPayment: string;
      textSelected: string;
    },
    selected: WorldpayApmPaymentInfo
  ): Card {
    const paymentMethodCard: WorldpayCard = createCreditCardCard(paymentDetails, cardLabels.textExpires);

    return {
      role: 'region',
      title: paymentDetails.defaultPayment ? cardLabels.textDefaultPaymentMethod : '',
      actions: [{
        name: cardLabels.textUseThisPayment,
        event: 'send'
      }],
      img: this.getCardIcon(paymentDetails?.cardType?.code ?? ''),
      header: selected?.id === paymentDetails.id ? cardLabels.textSelected : undefined,
      label: paymentDetails.defaultPayment ? 'paymentCard.defaultPaymentLabel' : 'paymentCard.additionalPaymentLabel',
      ...paymentMethodCard,
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
          !!apms &&
          !!selectedApm &&
          selectedApm.code !== PaymentMethod.Card &&
          selectedApm.code !== PaymentMethod.ApplePay &&
          selectedApm.code !== PaymentMethod.GooglePay &&
          selectedApm.code !== PaymentMethod.PayPal &&
          selectedApm.code !== PaymentMethod.PayPalSSL
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

  protected override getCardIcon(code: string): string {
    return worldpayGetCardIcon(code);
  }
}
