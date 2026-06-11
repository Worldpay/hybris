import { CdkAccordion, CdkAccordionItem } from '@angular/cdk/accordion';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit, TemplateRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { I18nModule, PaymentDetails } from '@spartacus/core';
import { MediaModule, SpinnerModule } from '@spartacus/storefront';
import { Observable, shareReplay } from 'rxjs';
import { distinctUntilChanged, first, map, switchMap, take } from 'rxjs/operators';
import { ApmData, GooglePayMerchantConfiguration, PaymentMethod, WorldpayApmService, WorldpayApplepayService, WorldpayGooglepayService } from '../../../core';
import { WorldpayBillingAddressComponent } from '../worldpay-billing-address/worldpay-billing-address.component';
import { WorldpayApmAchComponent } from './worldpay-apm-ach/worldpay-apm-ach.component';
import { WorldpayApmBaseComponent } from './worldpay-apm-base/worldpay-apm-base.component';
import { WorldpayApmGooglepayComponent } from './worldpay-apm-googlepay/worldpay-apm-googlepay.component';
import { WorldpayApmIdealComponent } from './worldpay-apm-ideal/worldpay-apm-ideal.component';
import { WorldpayApmSepaComponent } from './worldpay-apm-sepa/worldpay-apm-sepa.component';
import { WorldpayApmSubmitButtonsComponent } from './worldpay-apm-submit-buttons/worldpay-apm-submit-buttons.component';
import { WorldpayApmTileComponent } from './worldpay-apm-tile/worldpay-apm-tile.component';
import { WorldpayApplepayComponent } from './worldpay-applepay/worldpay-applepay.component';

@Component({
  selector: 'y-worldpay-apm-component',
  templateUrl: './worldpay-apm.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    AsyncPipe,
    MediaModule,
    SpinnerModule,
    I18nModule,
    NgTemplateOutlet,
    CdkAccordionItem,
    CdkAccordion,
    WorldpayBillingAddressComponent,
    WorldpayApmIdealComponent,
    WorldpayApmGooglepayComponent,
    WorldpayApplepayComponent,
    WorldpayApmAchComponent,
    WorldpayApmSubmitButtonsComponent,
    WorldpayApmSepaComponent,
    WorldpayApmTileComponent,
  ],
})
export class WorldpayApmComponent extends WorldpayApmBaseComponent implements OnInit, OnDestroy {
  @Input() apms: Observable<ApmData[]>;
  @Input() processing: boolean = false;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  @Input() cardTemplate!: TemplateRef<any>;
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
      case PaymentMethod.SepaDirectDebit:
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
   * Selects the APM payment method and updates the selected APM state.
   * This method ensures that the APM is explicitly selected when the accordion header
   * is clicked.
   *
   * @param apm - The APM data to select
   * @since 2211.43.0
   */
  selectApm(apm: ApmData): void {
    this.worldpayApmService.selectAPM(apm);
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
