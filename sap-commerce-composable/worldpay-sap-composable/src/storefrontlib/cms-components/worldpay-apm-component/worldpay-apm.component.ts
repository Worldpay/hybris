import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, EventEmitter, inject, Input, Output } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormGroup } from '@angular/forms';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { Address, LoggerService } from '@spartacus/core';
import { WorldpayApmService } from '@worldpay-services/worldpay-apm/worldpay-apm.service';
import { WorldpayApplepayService } from '@worldpay-services/worldpay-applepay/worldpay-applepay.service';
import { WorldpayGooglepayService } from '@worldpay-services/worldpay-googlepay/worldpay-googlepay.service';
import { makeFormErrorsVisible } from '@worldpay-utils/make-form-errors-visible';
import { BehaviorSubject, Observable } from 'rxjs';
import { distinctUntilChanged, first, map, switchMap, take } from 'rxjs/operators';
import { ApmData, ApmPaymentDetails, GooglePayMerchantConfiguration, PaymentMethod } from '../../../core/interfaces';
import { WorldpayOrderService } from '../../../core/services';

@Component({
  selector: 'y-worldpay-apm-component',
  templateUrl: './worldpay-apm.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorldpayApmComponent implements AfterViewInit {

  @Input() apms: Observable<ApmData[]>;
  @Input() processing: boolean = false;
  @Output() setPaymentDetails: EventEmitter<{ paymentDetails: ApmPaymentDetails; billingAddress: Address }> = new EventEmitter<{
    paymentDetails: ApmPaymentDetails;
    billingAddress: Address;
  }>();
  @Output() back: EventEmitter<void> = new EventEmitter<void>();
  isLoading$: Observable<boolean> = this.worldpayApmService.getLoading();
  card$: Observable<ApmData> = this.worldpayApmService.getApmComponentById('creditCardComponent', PaymentMethod.Card);
  googlePay$: Observable<ApmData>;
  applePay$: Observable<ApmData>;

  selectedApm$: Observable<ApmData> = this.worldpayApmService.getSelectedAPMFromState();
  paymentMethod: typeof PaymentMethod = PaymentMethod;
  sameAsDeliveryAddress: boolean = true;
  billingAddressForm: UntypedFormGroup = new UntypedFormGroup({});
  submitting$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  protected paymentDetails: ApmData;
  /**
   * Injects the CheckoutBillingAddressFormService into the component.
   * This service is used to manage the billing address form in the checkout process.
   * @protected
   * @since 2211.27.0
   */
  protected billingAddressFormService: CheckoutBillingAddressFormService = inject(
    CheckoutBillingAddressFormService
  );
  protected logger: LoggerService = inject(LoggerService);
  private destroyRef: DestroyRef = inject(DestroyRef);

  /**
   * Constructor
   * @param {WorldpayApmService} worldpayApmService - Service for handling APM (Alternative Payment Methods)
   * @param {WorldpayOrderService} worldpayOrderService - Service for handling Worldpay orders
   * @param {WorldpayGooglepayService} worldpayGooglePayService - Service for handling Google Pay
   * @param {WorldpayApplepayService} worldpayApplePayService - Service for handling Apple Pay
   * @param {ChangeDetectorRef} cd - Change detector reference for triggering change detection
   * @since 4.3.6
   */
  constructor(
    protected worldpayApmService: WorldpayApmService,
    protected worldpayOrderService: WorldpayOrderService,
    protected worldpayGooglePayService: WorldpayGooglepayService,
    protected worldpayApplePayService: WorldpayApplepayService,
    protected cd: ChangeDetectorRef,
  ) {
  }

  /**
   * Initialize component
   * Subscribes to selectedApm$ observable to set the default card payment method and billing address.
   * Initializes Google Pay and Apple Pay payment methods.
   * @since 4.3.6
   */
  ngAfterViewInit(): void {
    this.selectedApm$.pipe(
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (apm: ApmData): void => {
        this.selectDefaultCardPaymentMethod(apm);
        this.cd.detectChanges();
        this.sameAsDeliveryAddress = this.billingAddressFormService.isBillingAddressSameAsDeliveryAddress();
      },
      error: (error: unknown) => {
        this.logger.error('Failed to initialize FraudSight, check component configuration', error);
      },
    });
    this.billingAddressForm = this.billingAddressFormService.getBillingAddressForm();
    this.initializeGooglePay();
    this.initializeApplePay();
  }

  /**
   * Sets the sameAsDeliveryAddress property.
   * @param value - boolean indicating whether the billing address is the same as the delivery address.
   */
  setSameAsDeliveryAddress(value: boolean): void {
    this.sameAsDeliveryAddress = value;
  }

  /**
   * Determines whether to show the billing form and continue button based on the payment method code.
   * @param {string} code - The payment method code.
   * @returns {boolean} - Returns true if the billing form and continue button should be shown, otherwise false.
   * @since 4.3.6
   */
  showBillingFormAndContinueButton(code: string): boolean {
    switch (code) {
    case PaymentMethod.Card:
    case PaymentMethod.GooglePay:
    case PaymentMethod.ApplePay:
    case PaymentMethod.iDeal:
    case PaymentMethod.ACH:
      return false;

    default:
      return true;
    }
  }

  /**
   * Selects the APM payment details and emits the payment details and billing address.
   * If the billing address is not the same as the delivery address, it validates the billing address form.
   * If the form is valid, it retrieves the billing address; otherwise, it makes form errors visible and returns.
   * Sets the submitting state to true and emits the payment details and billing address.
   * @since 4.3.6
   */
  selectApmPaymentDetails(): void {
    let billingAddress: Address;
    if (!this.billingAddressFormService.isBillingAddressSameAsDeliveryAddress()) {
      if (this.billingAddressFormService.isBillingAddressFormValid()) {
        billingAddress = this.billingAddressFormService.getBillingAddress();
      } else {
        makeFormErrorsVisible(this.billingAddressFormService.getBillingAddressForm());
        return;
      }
    }

    this.submitting$.next(true);
    this.setPaymentDetails.emit({
      paymentDetails: {
        code: this.paymentDetails.code,
        name: this.paymentDetails.name,
      },
      billingAddress
    });
  }

  /**
   * Return to previous step by emitting the back event.
   * @since 6.4.0
   */
  return(): void {
    this.back.emit();
  }

  protected selectDefaultCardPaymentMethod(apm: ApmData): void {
    if (!apm) {
      this.worldpayApmService.selectAPM({ code: PaymentMethod.Card } as ApmData);
    } else {
      this.paymentDetails = apm;
    }
  }

  /**
   * Initialize Google Pay payment method.
   * Retrieves the Google Pay component by its ID and requests the merchant configuration.
   * Waits for the merchant configuration to be available before returning the Google Pay component data.
   * @since 4.3.6
   */
  protected initializeGooglePay(): void {
    this.googlePay$ = this.worldpayApmService.getApmComponentById('googlePayComponent', PaymentMethod.GooglePay)
      .pipe(
        take(1),
        switchMap((apmData: ApmData): Observable<ApmData> => {
          this.worldpayGooglePayService.requestMerchantConfiguration();
          return this.worldpayGooglePayService.getMerchantConfigurationFromState().pipe(
            first((googlepayMerchantConfiguration: GooglePayMerchantConfiguration): boolean => !!googlepayMerchantConfiguration),
            map((): ApmData => apmData)
          );
        }),
      );
  }

  /**
   * Initialize Apple Pay payment method.
   * Checks if the Apple Pay button is available and retrieves the Apple Pay component by its ID.
   * Sets the applePay$ observable with the retrieved Apple Pay component data.
   * @since 4.3.6
   */
  protected initializeApplePay(): void {
    if (this.worldpayApplePayService.applePayButtonAvailable()) {
      this.applePay$ = this.worldpayApmService
        .getApmComponentById('applePayComponent', PaymentMethod.ApplePay)
        .pipe(
          take(1),
          map((apmData: ApmData): ApmData => apmData),
        );
    }
  }
}
