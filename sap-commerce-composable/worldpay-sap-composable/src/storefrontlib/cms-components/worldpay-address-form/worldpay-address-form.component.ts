import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, Validators } from '@angular/forms';
import { AddressFormComponent } from '@spartacus/user/profile/components';

@Component({
  selector: 'y-worldpay-address-form',
  templateUrl: './worldpay-address-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class WorldpayAddressFormComponent extends AddressFormComponent implements OnInit, OnDestroy {
  jpLabel: string = '';
  private destroyRef: DestroyRef = inject(DestroyRef);

  /**
   * Getter for the line2 field
   * @return - The line2 field
   */
  get line2Field(): FormControl {
    return this.addressForm.get('line2') as FormControl;
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.addressForm.get('country').valueChanges.pipe(
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

  override ngOnDestroy(): void {
    super.ngOnDestroy();
  }
}
