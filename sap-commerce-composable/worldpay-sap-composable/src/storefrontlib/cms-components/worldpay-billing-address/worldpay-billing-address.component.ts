import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { FormControl, Validators } from '@angular/forms';
import { takeUntil } from 'rxjs/operators';
import { CheckoutBillingAddressFormComponent } from '@spartacus/checkout/base/components';
import { Region } from '@spartacus/core';

@Component({
  selector: 'y-worldpay-billing-address',
  templateUrl: './worldpay-billing-address.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorldpayBillingAddressComponent extends CheckoutBillingAddressFormComponent implements OnInit, OnDestroy {
  private drop: Subject<void> = new Subject<void>();
  jpLabel: string = '';

  /**
   * Getter for the line2 field
   * @since 6.4.0
   * @returns FormControl - line2 field
   */
  get line2Field() {
    return this.billingAddressForm.get('line2') as FormControl;
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.buildForm();
  }

  /*
   * Get region binding label
   * @since 6.4.0
   * @param regions - Region[]
   * @returns string - region binding label
   * @deprecated since 2211.27.0
   */
  getRegionBindingLabel(regions: Region[]): string {
    if (regions?.length) {
      if (regions[0].name) {
        return 'name';
      }
      if (regions[0].isocodeShort) {
        return 'isocodeShort';
      }
    }
    return 'isocode';
  }

  /**
   * Get region binding value
   * @since 6.4.0
   * @param regions - Region[]
   * @returns string - region binding value
   * @deprecated since 2211.27.0
   */
  getRegionBindingValue(regions: Region[]): string {
    if (regions?.length) {
      if (regions[0].isocode) {
        return 'isocode';
      }
      if (regions[0].isocodeShort) {
        return 'isocodeShort';
      }
    }
    return 'isocode';
  }

  /**
   * Build Form
   * @since 4.3.6
   */
  private buildForm(): void {
    this.billingAddressForm = this.billingAddressFormService.getBillingAddressForm();
    this.billingAddressForm.get('country').valueChanges.pipe(
      takeUntil(this.drop)
    ).subscribe({
      next: (value): void => {
        if (value?.isocode?.toLowerCase() === 'jp') {
          this.line2Field.setValidators(Validators.required);
          this.jpLabel = '.jp';
        } else {
          this.line2Field.clearValidators();
          this.jpLabel = '';
        }

        this.line2Field.updateValueAndValidity();
      }
    });
  }

  ngOnDestroy(): void {
    this.drop.next();
    this.drop.complete();
  }
}
