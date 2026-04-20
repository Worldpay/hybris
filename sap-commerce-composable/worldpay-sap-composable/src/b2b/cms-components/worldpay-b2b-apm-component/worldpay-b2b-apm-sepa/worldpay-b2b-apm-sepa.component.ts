import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { ApmPaymentDetails, WorldpayApmService } from '../../../../core';
import { WorldpayApmSepaComponent } from '../../../../storefrontlib';

@Component({
  selector: 'y-worldpay-b2b-apm-sepa',
  templateUrl: './worldpay-b2b-apm-sepa.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false
})
export class WorldpayB2BApmSepaComponent extends WorldpayApmSepaComponent implements OnInit {
  protected worldpayApmService: WorldpayApmService = inject(WorldpayApmService);

  /**
   * Initializes the component and adds the 'save' field to the form.
   *
   * The parent component creates the saveForm in the constructor as an empty
   * FormGroup. In ngOnInit(), we add the B2B-specific 'save' control to it.
   *
   * @since 2211.43.0
   * @override
   */
  override ngOnInit(): void {
    super.ngOnInit();
    this.addSaveFieldToForm();
  }

  /**
   * Adds the 'save' field to the existing saveForm.
   *
   * The parent component initializes saveForm as an empty FormGroup in the constructor.
   * This method adds the B2B-specific 'save' control to it, retrieving the default value
   * from the service.
   *
   * @protected
   * @since 2211.43.0
   */
  protected addSaveFieldToForm(): void {
    this.sepaForm.addControl(
      'save',
      this.fb.control(this.worldpayApmService.getSaveApm() ?? false)
    );
  }

  /**
   * Prepares the payment details before creating them.
   * This method updates the save option in the WorldpayApmService
   * and merges the provided payment details with the form values.
   *
   * @param {ApmPaymentDetails} details - The initial payment details to be updated.
   * @returns {ApmPaymentDetails} - The updated payment details including form values.
   */
  protected override beforeCreatePaymentDetails(details: ApmPaymentDetails): ApmPaymentDetails {
    this.worldpayApmService.setSaveApm(this.sepaForm.get('save').value);
    return {
      ...details,
      ...this.sepaForm.value
    };
  }
}
