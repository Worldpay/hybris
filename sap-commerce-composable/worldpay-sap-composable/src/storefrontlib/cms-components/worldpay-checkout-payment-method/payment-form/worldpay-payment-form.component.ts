import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, ViewEncapsulation } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { CheckoutPaymentFormComponent } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressFacade, CheckoutPaymentFacade } from '@spartacus/checkout/base/root';
import { GlobalMessageService, LoggerService, TranslationService, UserAddressService, UserPaymentService } from '@spartacus/core';
import { LaunchDialogService } from '@spartacus/storefront';
import { combineLatest, Observable, startWith, Subject } from 'rxjs';
import { distinctUntilChanged, map, withLatestFrom } from 'rxjs/operators';
import { WorldpayBillingAddressFormService, WorldpayFraudsightService } from '../../../../core';

@Component({
  selector: 'wp-payment-form',
  templateUrl: './worldpay-payment-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  standalone: false
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
  sameAsDeliveryAddress$: Observable<boolean>;
  billingAddressFormValid$: Observable<boolean>;
  continueButtonDisabled$: Observable<boolean>;
  protected override billingAddressService: WorldpayBillingAddressFormService = inject(WorldpayBillingAddressFormService);
  sameAsDeliveryAddress: boolean = this.billingAddressService.isBillingAddressSameAsDeliveryAddress();
  private drop: Subject<void> = new Subject<void>();
  private logger: LoggerService = inject(LoggerService);
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

    this.bindBillingAddressChanges();
  }

  ngOnDestroy(): void {
    this.drop.next();
    this.drop.complete();
  }

  protected bindBillingAddressChanges(): void {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const billingAddressForm$: Observable<any> = this.billingAddressService.getBillingAddressForm().valueChanges.pipe(
      startWith(this.billingAddressService.getBillingAddressForm().value),
      takeUntilDestroyed(this.destroyRef)
    );

    this.sameAsDeliveryAddress$ = billingAddressForm$.pipe(
      map((): boolean => this.billingAddressService.isBillingAddressSameAsDeliveryAddress())
    );

    this.billingAddressFormValid$ = billingAddressForm$.pipe(
      map((): boolean => this.billingAddressService.isBillingAddressFormValid())
    );

    this.continueButtonDisabled$ = combineLatest([
      this.billingAddressService.getSameAsDeliveryAddress(),
      this.sameAsDeliveryAddress$,
      this.billingAddressFormValid$
    ]).pipe(
      map(([isBillingAddressSameAsDeliveryAddress, sameAsDeliveryAddress, billingAddressFormValid]: [boolean, boolean, boolean]): boolean =>
        !(isBillingAddressSameAsDeliveryAddress || sameAsDeliveryAddress || billingAddressFormValid)
      ),
      distinctUntilChanged()
    );
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
