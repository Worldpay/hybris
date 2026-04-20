import { inject } from '@angular/core';
import { FormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Address } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { WorldpayBillingAddressFormService } from '../../core';

const mockCountries = [{
  isocode: 'US',
  name: 'United States'
}];

export class MockWorldpayBillingAddressFormService implements Partial<WorldpayBillingAddressFormService> {
  form: UntypedFormGroup;
  fb = inject(FormBuilder);

  getBillingAddress() {
    return undefined;
  }

  compareAddresses(a: any, b: any): boolean {
    return a === b;
  }

  getSameAsDeliveryAddress(): Observable<boolean> {
    return of(true);
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

  setBillingAddressForm(address: any): void {
  }

  resetBillingAddressForm(): void {
  }

  loadCountries(): void {
  }

  getCountries(): Observable<any> {
    return of(mockCountries);
  }

  isBillingAddressSameAsDeliveryAddress(): boolean {
    return true;
  }

  setSameAsDeliveryAddress(value: boolean): void {
  }

  setBillingAddress(address: any): void {

  }

  markAllAsTouched(): void {
  }

  updateSameAsDeliveryAddressFormData(address: any): void {
  }

  validateAndGetBillingAddress(): { isValid: boolean; address?: Address } {
    return { isValid: true };
  }

  updateDeliveryAddress(address: any): void {
  }
}
