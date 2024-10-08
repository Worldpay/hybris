import { ChangeDetectionStrategy, Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { WorldpayApmService } from '../../../core/services/worldpay-apm/worldpay-apm.service';
import { Address } from '@spartacus/core';
import { distinctUntilChanged, first, map, switchMap, take, takeUntil } from 'rxjs/operators';
import { makeFormErrorsVisible } from '../../../core/utils/make-form-errors-visible';
import { WorldpayGooglepayService } from '../../../core/services/worldpay-googlepay/worldpay-googlepay.service';
import { ApmData, ApmPaymentDetails, GooglePayMerchantConfiguration, PaymentMethod } from '../../../core/interfaces';
import { WorldpayApplepayService } from '../../../core/services/worldpay-applepay/worldpay-applepay.service';
import { WorldpayOrderService } from '../../../core/services';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { UntypedFormGroup } from '@angular/forms';

@Component({
  selector: 'y-worldpay-apm-component',
  templateUrl: './worldpay-apm.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorldpayApmComponent implements OnInit, OnDestroy {

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
  paymentMethod = PaymentMethod;
  sameAsShippingAddress: boolean = true;
  billingAddressForm: UntypedFormGroup = new UntypedFormGroup({});
  submitting$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  protected paymentDetails: ApmData;
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
   * @since 4.3.6
   * @param worldpayApmService WorldpayApmService
   * @param worldpayOrderService worldpayOrderService
   * @param worldpayGooglePayService WorldpayGooglepayService
   * @param worldpayApplePayService WorldpayApplepayService
   */
  constructor(
    protected worldpayApmService: WorldpayApmService,
    protected worldpayOrderService: WorldpayOrderService,
    protected worldpayGooglePayService: WorldpayGooglepayService,
    protected worldpayApplePayService: WorldpayApplepayService,
  ) {
  }

  /**
   * Initialize component
   * @since 4.3.6
   */
  ngOnInit(): void {
    this.selectedApm$.pipe(
      distinctUntilChanged(),
      takeUntil(this.drop)
    ).subscribe({
      next: (apm: ApmData) => this.selectDefaultCardPaymentMethod(apm),
      error: (error: unknown) => {
        console.error('Failed to initialize FraudSight, check component configuration', error);
      },
    });
    this.billingAddressForm = this.billingAddressFormService.getBillingAddressForm();
    this.sameAsShippingAddress = this.billingAddressFormService.isBillingAddressSameAsDeliveryAddress();
    this.initializeGooglePay();
    this.initializeApplePay();
  }

  /**
   * Show billing form and continue button
   * @since 4.3.6
   * @param code string - payment method code
   * @returns boolean - true if billing form and continue button should be shown
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
   * Select payment method
   * @since 4.3.6
   */
  selectApmPaymentDetails(): void {
    let billingAddress;
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

  protected selectDefaultCardPaymentMethod(apm: ApmData): void {
    if (!apm) {
      this.worldpayApmService.selectAPM({ code: PaymentMethod.Card } as ApmData);
    } else {
      this.paymentDetails = apm;
    }
  }

  /**
   * Return to previous step
   * @since 6.4.0
   */
  return(): void {
    this.back.emit();
  }

  /**
   * Initialize Google Pay payment method
   * @since 4.3.6
   */
  protected initializeGooglePay(): void {
    this.googlePay$ = this.worldpayApmService.getApmComponentById('googlePayComponent', PaymentMethod.GooglePay)
      .pipe(
        take(1),
        switchMap((apmData: ApmData) => {
          this.worldpayGooglePayService.requestMerchantConfiguration();
          return this.worldpayGooglePayService.getMerchantConfigurationFromState().pipe(
            first((googlepayMerchantConfiguration: GooglePayMerchantConfiguration) => !!googlepayMerchantConfiguration),
            map(() => apmData)
          );
        }),
      );
  }

  /**
   * Initialize Apple Pay payment method
   * @since 4.3.6
   */
  protected initializeApplePay(): void {
    if (this.worldpayApplePayService.applePayButtonAvailable()) {
      this.applePay$ = this.worldpayApmService
        .getApmComponentById('applePayComponent', PaymentMethod.ApplePay)
        .pipe(
          take(1),
          map((apmData: ApmData) => apmData),
        );
    }
  }

  /**
   * Unsubscribe from all subscriptions
   * @since 4.3.6
   */
  ngOnDestroy(): void {
    this.drop.next();
    this.drop.complete();
  }
}
