import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, ViewEncapsulation } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { CheckoutPaymentFormComponent } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressFacade, CheckoutPaymentFacade } from '@spartacus/checkout/base/root';
import { GlobalMessageService, LoggerService, TranslationService, UserAddressService, UserPaymentService } from '@spartacus/core';
import { LaunchDialogService } from '@spartacus/storefront';
import { WorldpayFraudsightService } from '@worldpay-services/worldpay-fraudsight/worldpay-fraudsight.service';
import { Observable } from 'rxjs';
import { distinctUntilChanged, withLatestFrom } from 'rxjs/operators';

@Component({
  selector: 'wp-payment-form',
  templateUrl: './worldpay-payment-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class WorldpayPaymentFormComponent extends CheckoutPaymentFormComponent implements OnInit {
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
  protected logger: LoggerService = inject(LoggerService);
  private destroyRef: DestroyRef = inject(DestroyRef);

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
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (value: boolean): void => {
        if (value === true) {
          this.paymentForm.get('save').setValue(true);
        }
      }
    });

    this.paymentForm.get('save').valueChanges.pipe(
      withLatestFrom(this.paymentForm.get('defaultPayment').valueChanges),
      takeUntilDestroyed(this.destroyRef)
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
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (enabled: boolean): void => {
          if (enabled) {
            this.paymentForm.addControl('dateOfBirth', this.fb.control('', [
              Validators.required
            ]));
          } else if (this.paymentForm.contains('dateOfBirth')) {
            this.paymentForm.removeControl('dateOfBirth');
          }
        },
        error: (err: unknown): void => this.logger.error(err)
      });
  }
}
