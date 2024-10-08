import { ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, inject, Input, OnDestroy, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';
import { Address } from '@spartacus/core';
import { makeFormErrorsVisible } from '../../../../core/utils/make-form-errors-visible';
import { ApmData, ApmPaymentDetails, PaymentMethod } from '../../../../core/interfaces';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';

@Component({
  selector: 'y-worldpay-apm-ideal',
  templateUrl: './worldpay-apm-ideal.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorldpayApmIdealComponent implements OnDestroy {
  @Input() apm: ApmData;
  @Output() setPaymentDetails = new EventEmitter<{ paymentDetails: ApmPaymentDetails; billingAddress: Address }>();
  @Output() back: EventEmitter<void> = new EventEmitter<void>();

  public billingAddressForm: UntypedFormGroup = new UntypedFormGroup({});
  public submitting$ = new BehaviorSubject<boolean>(false);
  public sameAsShippingAddress: boolean = true;

  /**
   * Form group for iDEAL payment details.
   * @since 6.4.2
   * @property {FormGroup} bank - Nested form group for bank details.
   * @property {FormControl} code - Form control for bank code.
   */
  idealForm: UntypedFormGroup = this.fb.group({
    bank: this.fb.group({ code: [null] }),
  });

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
   * Constructor for WorldpayApmIdealComponent.
   *
   * @param {UntypedFormBuilder} fb - Form builder for creating reactive forms.
   * @param {ChangeDetectorRef} cd - Service to detect and respond to changes in the component.
   */
  constructor(
    protected fb: UntypedFormBuilder,
    protected cd: ChangeDetectorRef
  ) {
  }

  ngOnInit(): void {
    if (this.apm?.bankConfigurations?.length > 0) {
      this.idealForm.get('bank').get('code').addValidators(Validators.required);
      this.cd.detectChanges();
    }

    this.billingAddressForm = this.billingAddressFormService.getBillingAddressForm();
  }

  next(): void {
    const {
      valid,
      value
    } = this.idealForm;

    this.sameAsShippingAddress = this.billingAddressFormService.isBillingAddressSameAsDeliveryAddress();

    if (!this.sameAsShippingAddress && !this.billingAddressFormService.isBillingAddressFormValid()) {
      makeFormErrorsVisible(this.billingAddressForm);
      return;
    }

    if (valid) {
      const paymentDetails: ApmPaymentDetails = { code: PaymentMethod.iDeal };

      if (value.bank?.code) {
        Object.assign(paymentDetails, { shopperBankCode: value.bank.code });
      }

      this.submitting$.next(true);

      let billingAddress = null;
      if (!this.sameAsShippingAddress) {
        billingAddress = this.billingAddressFormService.getBillingAddress();
      }

      this.setPaymentDetails.emit({
        paymentDetails,
        billingAddress
      });
    } else {
      makeFormErrorsVisible(this.idealForm);
    }
  }

  /**
   * Return to previous step
   * @since 6.4.0
   */
  return(): void {
    this.back.emit();
  }

  ngOnDestroy(): void {
    this.submitting$.next(false);
  }
}
