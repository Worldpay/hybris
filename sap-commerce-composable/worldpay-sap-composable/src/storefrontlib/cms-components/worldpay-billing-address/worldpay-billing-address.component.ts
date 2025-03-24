import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, Validators } from '@angular/forms';
import { CheckoutBillingAddressFormComponent } from '@spartacus/checkout/base/components';
import { Address, Region } from '@spartacus/core';

@Component({
  selector: 'y-worldpay-billing-address',
  templateUrl: './worldpay-billing-address.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class WorldpayBillingAddressComponent extends CheckoutBillingAddressFormComponent implements OnInit {
  jpLabel: string = '';
  @Output() emitSameAsDeliveryAddress: EventEmitter<boolean> = new EventEmitter<boolean>();
  private destroyRef: DestroyRef = inject(DestroyRef);

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

  override toggleSameAsDeliveryAddress(): void {
    this.sameAsDeliveryAddress = !this.sameAsDeliveryAddress;
    if (this.sameAsDeliveryAddress) {
      this.deliveryAddress$.pipe(
        takeUntilDestroyed(this.destroyRef)
      ).subscribe(
        {
          next: (address: Address): void => {
            this.billingAddressFormService.setDeliveryAddressAsBillingAddress(
              address
            );
          }
        });
    } else {
      this.billingAddressFormService.setDeliveryAddressAsBillingAddress(
        undefined
      );
    }
    this.emitSameAsDeliveryAddress.emit(this.billingAddressFormService.isBillingAddressSameAsDeliveryAddress());
  }

  /**
   * Build Form
   * @since 4.3.6
   */
  private buildForm(): void {
    this.billingAddressForm = this.billingAddressFormService.getBillingAddressForm();
    this.billingAddressForm.get('country')?.valueChanges?.pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      // eslint-disable-next-line  @typescript-eslint/no-explicit-any
      next: (value: any): void => {
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
}
