import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { takeUntil } from 'rxjs/operators';
import { FormControl, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { AddressFormComponent } from '@spartacus/storefront';

@Component({
  selector: 'y-worldpay-address-form',
  templateUrl: './worldpay-address-form.component.html',
  styleUrls: ['./worldpay-address-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class WorldpayAddressFormComponent extends AddressFormComponent implements OnInit, OnDestroy {
  private drop: Subject<void> = new Subject<void>();
  jpLabel: string = '';

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

  override ngOnDestroy(): void {
    super.ngOnDestroy();
    this.drop.next();
    this.drop.complete();
  }
}
