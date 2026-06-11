import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { I18nModule } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { ApmPaymentDetails } from '../../../../core';
import { WorldpayBillingAddressComponent } from '../../worldpay-billing-address/worldpay-billing-address.component';
import { WorldpayApmBaseComponent } from '../worldpay-apm-base/worldpay-apm-base.component';
import { WorldpayApmSubmitButtonsComponent } from '../worldpay-apm-submit-buttons/worldpay-apm-submit-buttons.component';

@Component({
  selector: 'y-worldpay-apm-sepa',
  templateUrl: './worldpay-apm-sepa.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    I18nModule,
    FormErrorsModule,
    NgSelectModule,
    WorldpayBillingAddressComponent,
    WorldpayApmSubmitButtonsComponent,
  ]
})
export class WorldpayApmSepaComponent extends WorldpayApmBaseComponent {
  /**
   * Form group for SEPA payment details.
   * @since 6.4.2
   * @property {UntypedFormGroup} saved - Nested form group for bank details.
   */
  sepaForm: UntypedFormGroup;

  /**
   * Initializes the WorldpayApmSepaComponent.
   * Calls the parent constructor and initializes the SEPA form.
   */
  constructor() {
    super();
    this.sepaForm = this.buildSaveForm();
  }

  /**
   * Handles the next step in the SEPA payment process.
   * Validates the billing address and the SEPA form before proceeding.
   * If the billing address is invalid, the method exits early.
   * If the SEPA form is valid, constructs the payment details and emits them.
   * If the form is invalid, makes the form errors visible.
   * @since 2211.43.0
   */
  next(): void {
    let paymentDetails: ApmPaymentDetails = {
      code: this.apm.code,
      name: this.apm.name,
    };

    paymentDetails = this.beforeCreatePaymentDetails(paymentDetails);

    this.createPaymentDetails(paymentDetails);
  }

  /**
   * Builds and returns a form group for saving SEPA payment details.
   * This method initializes the form group structure for the SEPA payment details.
   * @returns {UntypedFormGroup} - The initialized form group.
   */
  protected buildSaveForm(): UntypedFormGroup {
    return this.fb.group({});
  }

  /**
   * Allows modification of payment details before creating them.
   * This method can be overridden to add or modify properties of the payment details.
   * @param {ApmPaymentDetails} details - The initial payment details object.
   * @returns {ApmPaymentDetails} - The modified payment details object.
   */
  protected beforeCreatePaymentDetails(details: ApmPaymentDetails): ApmPaymentDetails {
    return details;
  }
}
