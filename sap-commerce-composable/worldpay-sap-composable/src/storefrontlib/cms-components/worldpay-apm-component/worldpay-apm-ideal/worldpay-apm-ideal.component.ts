import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';
import { Address } from '@spartacus/core';
import { makeFormErrorsVisible } from '../../../../core/utils/make-form-errors-visible';
import { ApmData, ApmPaymentDetails, PaymentMethod } from '../../../../core/interfaces';

@Component({
  selector: 'y-worldpay-apm-ideal',
  templateUrl: './worldpay-apm-ideal.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorldpayApmIdealComponent implements OnDestroy {
  @Input() apm: ApmData;
  @Input() billingAddressForm: UntypedFormGroup = new UntypedFormGroup({});
  @Output() setPaymentDetails = new EventEmitter<{ paymentDetails: ApmPaymentDetails; billingAddress: Address }>();
  @Output() back: EventEmitter<void> = new EventEmitter<void>();

  public submitting$ = new BehaviorSubject<boolean>(false);
  public sameAsShippingAddress: boolean = true;

  idealForm: UntypedFormGroup = this.fb.group({
    bank: this.fb.group({ code: ['', Validators.required] }),
  });

  constructor(protected fb: UntypedFormBuilder) {
  }

  next(): void {
    const {
      valid,
      value
    } = this.idealForm;
    const {
      invalid: billingInvalid,
      value: billingValue
    } = this.billingAddressForm;

    if (!this.sameAsShippingAddress && billingInvalid) {
      makeFormErrorsVisible(this.billingAddressForm);
      return;
    }

    if (valid) {
      const paymentDetails: ApmPaymentDetails = {
        code: PaymentMethod.iDeal,
        shopperBankCode: value.bank.code
      };

      this.submitting$.next(true);

      let billingAddress = null;
      if (!this.sameAsShippingAddress) {
        billingAddress = billingValue;
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
