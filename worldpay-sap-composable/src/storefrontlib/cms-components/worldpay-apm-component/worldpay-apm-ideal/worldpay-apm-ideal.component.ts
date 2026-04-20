import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormGroup, Validators } from '@angular/forms';
import { ApmPaymentDetails, IdealFormValue, makeFormErrorsVisible, PaymentMethod } from '../../../../core';
import { WorldpayApmBaseComponent } from '../worldpay-apm-base/worldpay-apm-base.component';

@Component({
  selector: 'y-worldpay-apm-ideal',
  templateUrl: './worldpay-apm-ideal.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false
})
export class WorldpayApmIdealComponent extends WorldpayApmBaseComponent {
  /**
   * Form group for iDEAL payment details.
   * @since 6.4.2
   * @property {UntypedFormGroup} bank - Nested form group for bank details.
   * @property {UntypedFormGroup} code - Form control for bank code.
   */
  idealForm: UntypedFormGroup = this.fb.group({
    bank: this.fb.group({ code: [null] }),
  });

  /**
   * Initializes the component and adds a required validator to the bank code form control
   * if bank configurations are available for the selected APM.
   * This ensures that the bank code is mandatory when applicable.
   * @since 2211.43.0
   */
  override ngOnInit(): void {
    super.ngOnInit();
    if (this.apm?.bankConfigurations?.length > 0) {
      this.idealForm.get('bank').get('code').addValidators(Validators.required);
    }
  }

  /**
   * Handles the next step in the iDEAL payment process.
   * Validates the billing address and the iDEAL form before proceeding.
   * If the billing address is invalid, the method exits early.
   * If the iDEAL form is valid, constructs the payment details and emits them.
   * If the form is invalid, makes the form errors visible.
   * @since 2211.43.0
   */
  next(): void {
    if (this.idealForm.valid) {
      const value: IdealFormValue = this.idealForm.value;
      const paymentDetails: ApmPaymentDetails = { code: PaymentMethod.iDeal };

      if (value.bank?.code) {
        Object.assign(paymentDetails, { shopperBankCode: value.bank.code });
      }

      this.createPaymentDetails(paymentDetails);
    } else {
      makeFormErrorsVisible(this.idealForm);
    }
  }
}
