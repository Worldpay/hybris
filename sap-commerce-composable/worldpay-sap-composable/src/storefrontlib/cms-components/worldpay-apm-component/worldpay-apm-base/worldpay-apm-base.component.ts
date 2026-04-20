import { DestroyRef, Directive, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, UntypedFormGroup } from '@angular/forms';
import { LoggerService, PaymentDetails } from '@spartacus/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { distinctUntilChanged, map } from 'rxjs/operators';
import { ApmData, ApmPaymentDetails, BillingAddressFormValidation, makeFormErrorsVisible, PaymentFormData, WorldpayBillingAddressFormService } from '../../../../core';

@Directive()
export abstract class WorldpayApmBaseComponent implements OnInit {
  @Input() apm: ApmData;
  @Output() setPaymentDetails: EventEmitter<PaymentFormData> = new EventEmitter<PaymentFormData>();
  @Output() back: EventEmitter<void> = new EventEmitter<void>();
  readonly isSubmitting$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public sameAsDeliveryAddress$: Observable<boolean>;
  public disableContinueButton$: Observable<boolean>;
  protected billingAddressFormService: WorldpayBillingAddressFormService = inject(WorldpayBillingAddressFormService);
  protected fb: FormBuilder = inject(FormBuilder);
  protected destroyRef: DestroyRef = inject(DestroyRef);
  protected logger: LoggerService = inject(LoggerService);
  protected billingAddressForm: UntypedFormGroup;

  /**
   * Initializes the component by retrieving the billing address form from the service.
   * This method is called automatically when the component is initialized.
   * It assigns the retrieved form to the `billingAddressForm` property.
   *
   * @see WorldpayBillingAddressFormService#getBillingAddressForm
   * @since 2211.43.0
   */

  ngOnInit(): void {
    this.billingAddressForm = this.billingAddressFormService.getBillingAddressForm();
    this.sameAsDeliveryAddress$ = this.billingAddressFormService.getSameAsDeliveryAddress();
    this.disableContinueButton$ = this.disableContinueButton();
  }

  /**
   * Determines whether the "Continue" button should be disabled based on the state of the form.
   *
   * This method combines the observables for `sameAsDeliveryAddress$` and `isSubmitting$` to compute
   * the button's disabled state. The button is disabled if:
   * - The "same as delivery address" option is not selected and the billing address form is invalid, or
   * - The form submission process is currently in progress.
   *
   * The result is emitted as an observable that updates whenever the relevant state changes.
   *
   * @returns {Observable<boolean>} An observable that emits `true` if the "Continue" button should be disabled,
   * and `false` otherwise.
   * @since 2211.43.0
   */
  disableContinueButton(): Observable<boolean> {
    return combineLatest([
      this.sameAsDeliveryAddress$,
      this.isSubmitting$,
    ]).pipe(
      map(([sameAsDeliveryAddress, isSubmitting]: [boolean, boolean]): boolean => (!sameAsDeliveryAddress && this.billingAddressForm.invalid) || isSubmitting),
      distinctUntilChanged()
    );
  }

  /**
   * Validates the billing address form and determines if it is valid.
   * If the "same as delivery address" option is selected, it returns a valid status without an address.
   * If the billing address form is valid, it returns the form's address value.
   * If the form is invalid, it makes the form errors visible and returns an invalid status.
   *
   * @returns {BillingAddressFormValidation} An object containing:
   *  - `isValid` (boolean): Indicates whether the billing address is valid.
   *  - `address` (Address | undefined): The billing address if the form is valid, otherwise undefined.
   *  @since 2211.43.0
   */
  protected validateAndGetBillingAddress(): BillingAddressFormValidation {
    if (this.billingAddressFormService.isBillingAddressSameAsDeliveryAddress()) {
      return { isValid: true };
    }

    if (this.billingAddressFormService.isBillingAddressFormValid()) {
      return {
        isValid: true,
        billingAddress: this.billingAddressFormService.getBillingAddressForm()?.value,
      };
    }

    makeFormErrorsVisible(this.billingAddressFormService.getBillingAddressForm());
    return { isValid: false };
  }

  /**
   * Emits an event to navigate back to the previous step or screen.
   * This method triggers the `back` event emitter.
   * @since 2211.43.0
   */
  protected return(): void {
    this.back.emit();
  }

  /**
   * Creates and emits payment details along with the associated billing address.
   * Sets the `isSubmitting` signal to `true` to indicate the submission process has started.
   * Emits the `setPaymentDetails` event with the provided payment details and billing address.
   *
   * @param {PaymentDetails | ApmPaymentDetails} paymentDetails - The payment details to be emitted.
   * @since 2211.43.0
   */
  protected createPaymentDetails(paymentDetails: PaymentDetails | ApmPaymentDetails): void {

    const {
      isValid,
      billingAddress
    }: BillingAddressFormValidation = this.validateAndGetBillingAddress();

    if (!isValid) {
      return;
    }

    this.isSubmitting$.next(true);
    this.setPaymentDetails.emit({
      paymentDetails,
      billingAddress
    });
  }
}
