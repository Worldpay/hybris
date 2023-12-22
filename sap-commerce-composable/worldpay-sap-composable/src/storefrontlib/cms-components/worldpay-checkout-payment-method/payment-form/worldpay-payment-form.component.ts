import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { GlobalMessageService, TranslationService, UserAddressService, UserPaymentService } from '@spartacus/core';
import { LaunchDialogService } from '@spartacus/storefront';
import { Observable, Subject } from 'rxjs';
import { WorldpayFraudsightService } from '../../../../core/services/worldpay-fraudsight/worldpay-fraudsight.service';
import { distinctUntilChanged, takeUntil, withLatestFrom } from 'rxjs/operators';
import { CheckoutPaymentFormComponent } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressFacade, CheckoutPaymentFacade } from '@spartacus/checkout/base/root';

@Component({
  selector: 'wp-payment-form',
  templateUrl: './worldpay-payment-form.component.html',
  styleUrls: ['./worldpay-payment-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class WorldpayPaymentFormComponent extends CheckoutPaymentFormComponent implements OnInit, OnDestroy {
  override paymentForm: UntypedFormGroup = this.fb.group({
    defaultPayment: [false],
    save: [false],
    saved: [false],
    accountHolderName: ['', Validators.required],
    cardNumber: ['', Validators.required],
    cardType: this.fb.group({
      code: ['', Validators.required]
    }),
    expiryMonth: ['', Validators.required],
    expiryYear: ['', Validators.required],
    cvn: ['', Validators.required]
  });
  isFraudSightEnabled$: Observable<boolean> = this.worldpayFraudsightService.isFraudSightEnabledFromState();
  private drop: Subject<void> = new Subject<void>();

  constructor(
    protected override checkoutPaymentFacade: CheckoutPaymentFacade,
    protected override checkoutDeliveryAddressFacade: CheckoutDeliveryAddressFacade,
    protected override userPaymentService: UserPaymentService,
    protected override globalMessageService: GlobalMessageService,
    protected override fb: UntypedFormBuilder,
    protected override userAddressService: UserAddressService,
    protected override launchDialogService: LaunchDialogService,
    protected override translationService: TranslationService,
    protected worldpayFraudsightService: WorldpayFraudsightService,
  ) {
    super(
      checkoutPaymentFacade,
      checkoutDeliveryAddressFacade,
      userPaymentService,
      globalMessageService,
      fb,
      userAddressService,
      launchDialogService,
      translationService,
    );
  }

  override ngOnInit(): void {
    super.ngOnInit();

    this.toggleDateOfBirthControl();

    this.paymentForm.get('defaultPayment').valueChanges.pipe(
      distinctUntilChanged(),
      takeUntil(this.drop)
    ).subscribe({
      next: (value: boolean): void => {
        if (value === true) {
          this.paymentForm.get('save').setValue(true);
        }
      }
    });

    this.paymentForm.get('save').valueChanges.pipe(
      withLatestFrom(this.paymentForm.get('defaultPayment').valueChanges),
      takeUntil(this.drop)
    ).subscribe({
      next: ([save, defaultPayment]: [boolean, boolean]): void => {
        if (save === false && defaultPayment === true) {
          this.paymentForm.get('defaultPayment').setValue(false);
        }
      }
    });
  }

  protected toggleDateOfBirthControl(): void {
    this.isFraudSightEnabled$
      .pipe(takeUntil(this.drop))
      .subscribe({
        next: (enabled) => {
          if (enabled) {
            this.paymentForm.addControl('dateOfBirth', this.fb.control('', [
              Validators.required
            ]));
          } else if (this.paymentForm.contains('dateOfBirth')) {
            this.paymentForm.removeControl('dateOfBirth');
          }
        },
        error: (err: unknown) => console.error(err)
      });
  }

  ngOnDestroy(): void {
    this.drop.next();
    this.drop.complete();
  }
}
