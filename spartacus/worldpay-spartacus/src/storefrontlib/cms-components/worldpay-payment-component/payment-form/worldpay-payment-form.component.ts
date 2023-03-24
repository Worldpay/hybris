import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GlobalMessageService, UserAddressService, UserPaymentService } from '@spartacus/core';
import { ModalService } from '@spartacus/storefront';
import { Observable, Subject } from 'rxjs';
import { WorldpayFraudsightService } from '../../../../core/services/worldpay-fraudsight/worldpay-fraudsight.service';
import { filter, takeUntil } from 'rxjs/operators';
import { PaymentFormComponent } from '@spartacus/checkout/components';
import { CheckoutDeliveryService, CheckoutPaymentService } from '@spartacus/checkout/core';

type monthType = { id: number; name: string };
type yearType = { id: number; name: number };

@Component({
  selector: 'wp-payment-form',
  templateUrl: './worldpay-payment-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorldpayPaymentFormComponent extends PaymentFormComponent implements OnInit, OnDestroy {
  paymentForm: FormGroup = this.formBuilder.group({
    defaultPayment: [false],
    saved: [false],
    accountHolderName: ['', Validators.required],
    cardNumber: ['', Validators.required],
    cardType: this.formBuilder.group({
      code: ['', Validators.required]
    }),
    expiryMonth: ['', Validators.required],
    expiryYear: ['', Validators.required],
    cvn: ['', Validators.required]
  });
  isFraudSightEnabled$: Observable<boolean> = this.worldpayFraudsightService.isFraudSightEnabledFromState();
  private drop = new Subject<void>();

  constructor(
    protected checkoutPaymentService: CheckoutPaymentService,
    protected checkoutDeliveryService: CheckoutDeliveryService,
    protected userPaymentService: UserPaymentService,
    protected globalMessageService: GlobalMessageService,
    protected formBuilder: FormBuilder,
    protected modalService: ModalService,
    protected userAddressService: UserAddressService,
    protected worldpayFraudsightService: WorldpayFraudsightService,
  ) {
    super(
      checkoutPaymentService,
      checkoutDeliveryService,
      userPaymentService,
      globalMessageService,
      formBuilder,
      modalService,
      userAddressService
    );
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.toggleDateOfBirthControl();
  }

  protected toggleDateOfBirthControl(): void {
    this.isFraudSightEnabled$
      .pipe(
        filter(Boolean),
        takeUntil(this.drop)
      )
      .subscribe((enabled) => {
        if (enabled) {
          this.paymentForm.addControl('dateOfBirth', this.formBuilder.control('', [
            Validators.required
          ]));
        } else if (this.paymentForm.contains('dateOfBirth')) {
          this.paymentForm.removeControl('dateOfBirth');
        }
      });
  }

  public toggleSaveCard(): void {
    this.paymentForm.value.saved = !this.paymentForm.value.saved;
  }

  ngOnDestroy(): void {
    this.drop.next();
    this.drop.complete();
  }
}
