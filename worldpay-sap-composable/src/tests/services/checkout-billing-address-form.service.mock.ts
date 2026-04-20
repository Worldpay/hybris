import { inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { Address } from '@spartacus/core';

const mockBillingAddress: Address = {
  firstName: 'John',
  lastName: 'Doe',
  line1: 'Green Street',
  line2: '420',
  town: 'Montreal',
  postalCode: 'H3A',
  country: { isocode: 'CA' },
  region: { isocodeShort: 'QC' },
};

export class MockCheckoutBillingAddressFormService implements Partial<CheckoutBillingAddressFormService> {
  protected fb: UntypedFormBuilder = inject(UntypedFormBuilder);
  protected billingAddress: Address | undefined = undefined;
  private form: UntypedFormGroup;

  getBillingAddress(): Address {
    return mockBillingAddress;
  }

  isBillingAddressSameAsDeliveryAddress(): boolean {
    return true;
  }

  isBillingAddressFormValid(): boolean {
    return true;
  }

  getBillingAddressForm(): UntypedFormGroup {
    if (!this.form) {
      this.form = this.fb.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        line1: ['', Validators.required],
        line2: [''],
        town: ['', Validators.required],
        region: this.fb.group({
          isocodeShort: [null, Validators.required],
        }),
        country: this.fb.group({
          isocode: [null, Validators.required],
        }),
        postalCode: ['', Validators.required],
      });
    }
    return this.form;
  }
}
