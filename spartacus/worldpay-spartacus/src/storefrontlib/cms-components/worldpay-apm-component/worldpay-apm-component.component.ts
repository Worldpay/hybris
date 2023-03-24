import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { WorldpayApmService } from '../../../core/services/worldpay-apm/worldpay-apm.service';
import { FormGroup } from '@angular/forms';
import { Address } from '@spartacus/core';
import { first, map, switchMap, take, takeUntil } from 'rxjs/operators';
import { makeFormErrorsVisible } from '../../../core/utils/make-form-errors-visible';
import { WorldpayGooglepayService } from '../../../core/services/worldpay-googlepay/worldpay-googlepay.service';
import { WorldpayCheckoutService } from '../../../core/services/worldpay-checkout/worldpay-checkout.service';
import { ApmData, ApmPaymentDetails, PaymentMethod } from '../../../core/interfaces';
import { WorldpayApplepayService } from '../../../core/services/worldpay-applepay/worldpay-applepay.service';

@Component({
  selector: 'y-worldpay-apm-component',
  templateUrl: './worldpay-apm-component.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorldpayApmComponent implements OnInit, OnDestroy {

  @Input() apms: Observable<ApmData[]>;
  @Input() processing = false;
  @Output() setPaymentDetails = new EventEmitter<{ paymentDetails: ApmPaymentDetails; billingAddress: Address }>();

  isLoading$ = this.worldpayCheckoutService.getLoading();
  card$: Observable<ApmData> = this.worldpayApmService
    .getApmComponentById('creditCardComponent', PaymentMethod.Card);
  googlePay$: Observable<ApmData>;
  applePay$: Observable<ApmData>;

  selectedApm$: Observable<ApmData> = this.worldpayApmService.getSelectedAPMFromState();
  paymentMethod = PaymentMethod;
  sameAsShippingAddress$ = new BehaviorSubject<boolean>(true);
  billingAddressForm: FormGroup = new FormGroup({});
  submitting$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  protected paymentDetails: ApmData;
  private drop = new Subject<void>();

  constructor(
    protected worldpayApmService: WorldpayApmService,
    protected worldpayGooglePayService: WorldpayGooglepayService,
    protected worldpayCheckoutService: WorldpayCheckoutService,
    protected worldpayApplePayService: WorldpayApplepayService,
  ) {
  }

  ngOnInit(): void {
    this.selectedApm$.pipe(takeUntil(this.drop)).subscribe((apm) => {
      if (!apm) {
        this.worldpayApmService.selectAPM({
          code: PaymentMethod.Card
        } as ApmData);
      } else {
        this.paymentDetails = apm;
      }
    });

    this.initializeGooglePay();
    this.initializeApplePay();
  }

  showBillingFormAndContinueButton(code: PaymentMethod): boolean {
    switch (code) {
    case PaymentMethod.Card:
    case PaymentMethod.GooglePay:
    case PaymentMethod.ApplePay:
    case PaymentMethod.iDeal:
      return false;

    default:
      return true;
    }
  }

  selectApmPaymentDetails(): void {
    let billingAddress;
    if (!this.sameAsShippingAddress$.value) {
      if (this.billingAddressForm.valid) {
        billingAddress = this.billingAddressForm.value;
      } else {
        makeFormErrorsVisible(this.billingAddressForm);
        return;
      }
    }

    this.submitting$.next(true);

    this.setPaymentDetails.emit({
      paymentDetails: {
        code: this.paymentDetails.code,
        name: this.paymentDetails.name,
      },
      billingAddress
    });
  }

  protected initializeGooglePay(): void {
    this.googlePay$ = this.worldpayApmService
      .getApmComponentById('googlePayComponent', PaymentMethod.GooglePay)
      .pipe(
        take(1),
        switchMap((apmData) => {
          this.worldpayGooglePayService.requestMerchantConfiguration();
          return this.worldpayGooglePayService.getMerchantConfigurationFromState().pipe(
            first(c => !!c), map(_ => apmData)
          );
        }),
      );
  }

  protected initializeApplePay(): void {
    if (this.worldpayApplePayService.applePayButtonAvailable()) {
      this.applePay$ = this.worldpayApmService
        .getApmComponentById('applePayComponent', PaymentMethod.ApplePay)
        .pipe(
          take(1),
          map((apmData) => apmData),
        );
    }
  }

  ngOnDestroy(): void {
    this.drop.next();
    this.drop.complete();
  }
}
