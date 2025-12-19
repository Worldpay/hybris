import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { PaymentDetails } from '@spartacus/core';
import { Observable, shareReplay } from 'rxjs';
import { distinctUntilChanged, first, map, switchMap, take } from 'rxjs/operators';
import { WorldpayApmService, WorldpayApplepayService, WorldpayGooglepayService } from 'worldpay-sap-composable-services';
import { ApmData, GooglePayMerchantConfiguration, PaymentMethod } from 'worldpay-sap-core';
import { WorldpayApmBaseComponent } from './worldpay-apm-base/worldpay-apm-base.component';

@Component({
  selector: 'y-worldpay-apm-component',
  templateUrl: './worldpay-apm.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false
})
export class WorldpayApmComponent extends WorldpayApmBaseComponent implements OnInit, OnDestroy {
  @Input() apms: Observable<ApmData[]>;
  @Input() processing: boolean = false;
  readonly paymentMethod: typeof PaymentMethod = PaymentMethod;
  protected worldpayApmService: WorldpayApmService = inject(WorldpayApmService);
  isLoading$: Observable<boolean> = this.worldpayApmService.getLoading();
  card$: Observable<ApmData> = this.worldpayApmService.getApmComponentById('creditCardComponent', PaymentMethod.Card).pipe(
    shareReplay({
      bufferSize: 1,
      refCount: true
    }),
  );
  selectedApm$: Observable<ApmData> = this.worldpayApmService.getSelectedAPMFromState().pipe(
    distinctUntilChanged(),
    shareReplay({
      bufferSize: 1,
      refCount: true
    }),
  );
  protected worldpayGooglePayService: WorldpayGooglepayService = inject(WorldpayGooglepayService);
  protected worldpayApplePayService: WorldpayApplepayService = inject(WorldpayApplepayService);
  protected googlePay$: Observable<ApmData> = this.initializeGooglePay();
  protected applePay$: Observable<ApmData | null> = this.initializeApplePay();
  protected paymentDetails: ApmData;

  /**
   * Initialize component
   * Subscribes to selectedApm$ observable to set the default card payment method and billing address.
   * Initializes Google Pay and Apple Pay payment methods.
   * @since 4.3.6
   */
  override ngOnInit(): void {
    super.ngOnInit();
    this.bindSelectedApm();
  }

  /**
   * Subscribes to the selected APM (Alternative Payment Method) observable and updates the payment method.
   * If an error occurs during subscription, it logs the error.
   *
   * @since 2211.43.0
   */
  bindSelectedApm(): void {
    this.selectedApm$.pipe(
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (apm: ApmData): void => this.selectDefaultCardPaymentMethod(apm),
      error: (error: unknown): void => {
        this.logger.error('Failed to initialize FraudSight, check component configuration', error);
      },
    });
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
    if (!this.paymentDetails) {
      this.logger.error('No payment details selected');
      return;
    }

    const paymentDetails: PaymentDetails = {
      code: this.paymentDetails.code,
      name: this.paymentDetails.name,
    };
    this.createPaymentDetails(paymentDetails);
  }

  /**
   * Unsubscribe from all subscriptions
   * @since 4.3.6
   */
  ngOnDestroy(): void {
    this.isSubmitting$.next(false);
  }

  /**
   * Selects the default card payment method if no APM (Alternative Payment Method) is provided.
   * If an APM is provided, it sets the payment details to the provided APM.
   *
   * @param apm - The selected APM data. If null or undefined, the default card payment method is selected.
   * @since 4.3.6
   */
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
  protected initializeGooglePay(): Observable<ApmData> {
    return this.worldpayApmService.getApmComponentById('googlePayComponent', PaymentMethod.GooglePay)
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
  protected initializeApplePay(): Observable<ApmData | null> {
    if (!this.worldpayApplePayService?.applePayButtonAvailable()) {
      return null;
    }

    return this.worldpayApmService
      .getApmComponentById('applePayComponent', PaymentMethod.ApplePay)
      .pipe(
        take(1),
        map((apmData: ApmData): ApmData => apmData),
      );
  }
}
