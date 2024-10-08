import { Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Address, QueryState } from '@spartacus/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { ACHBankAccountType, ACHPaymentForm, ApmData, ApmPaymentDetails, PaymentMethod } from '../../../../core/interfaces';
import { makeFormErrorsVisible } from '../../../../core/utils';
import { WorldpayACHFacade } from '../../../../core/facade/worldpay-ach.facade';
import { filter, map, takeUntil, tap } from 'rxjs/operators';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';

@Component({
  selector: 'y-worldpay-apm-ach',
  templateUrl: './worldpay-apm-ach.component.html',
  styleUrls: ['./worldpay-apm-ach.component.scss']
})
export class WorldpayApmAchComponent implements OnInit, OnDestroy {
  @Input() apm: ApmData;
  @Output() setPaymentDetails = new EventEmitter<{ paymentDetails: ApmPaymentDetails; billingAddress: Address }>();
  @Output() back: EventEmitter<void> = new EventEmitter<void>();

  achBankAccountTypesState$: Observable<QueryState<ACHBankAccountType[]>>;

  public submitting$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public sameAsShippingAddress: boolean = true;
  public billingAddressForm: UntypedFormGroup = new UntypedFormGroup({});
  private drop: Subject<void> = new Subject<void>();

  /**
   * Form group for ACH payment details.
   * @since 6.4.2
   * @property {FormGroup} accountType - Nested form group for account type with a required validator.
   * @property {FormControl} accountNumber - Form control for account number with required and max length validators.
   * @property {FormControl} routingNumber - Form control for routing number with required, min length, and max length validators.
   * @property {FormControl} checkNumber - Form control for check number with a max length validator.
   * @property {FormControl} companyName - Form control for company name with a max length validator.
   * @property {FormControl} customIdentifier - Form control for custom identifier with a max length validator.
   */
  achForm: UntypedFormGroup = this.fb.group({
    accountType: this.fb.group({ code: null }, [Validators.required]),
    accountNumber: this.fb.control(null, [Validators.required, Validators.maxLength(17)]),
    routingNumber: this.fb.control(null, [Validators.required, Validators.minLength(8), Validators.maxLength(9)]),
    checkNumber: this.fb.control(null, [Validators.maxLength(15)]),
    companyName: this.fb.control(null, [Validators.maxLength(40)]),
    customIdentifier: this.fb.control(null, [Validators.maxLength(15)]),
  });

  /**
   * Injects the CheckoutBillingAddressFormService into the component.
   * This service is used to manage the billing address form in the checkout process.
   * @protected
   * @since 2211.27.0
   */
  protected billingAddressFormService = inject(
    CheckoutBillingAddressFormService
  );

  /**
   * Constructor for WorldpayApmAchComponent.
   *
   * @param {UntypedFormBuilder} fb - Form builder for creating reactive forms.
   * @param {WorldpayACHFacade} worldpayACHFacade - Facade for interacting with Worldpay ACH services.
   */
  constructor(
    protected fb: UntypedFormBuilder,
    protected worldpayACHFacade: WorldpayACHFacade,
  ) {
  }

  /**
   * Initialize component
   * @since 6.4.2
   */
  ngOnInit(): void {
    this.billingAddressForm = this.billingAddressFormService.getBillingAddressForm();
    this.sameAsShippingAddress = this.billingAddressFormService.isBillingAddressSameAsDeliveryAddress();

    this.achBankAccountTypesState$ = this.worldpayACHFacade.getACHBankAccountTypesState().pipe(
      filter((state: QueryState<ACHBankAccountType[]>) => !!state.data),
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      map((response: QueryState<any>): QueryState<ACHBankAccountType[]> => {
        const data: { code: string; name: string }[] = Object.keys(response.data).map((key: string): { code: string; name: string } => ({
          code: key,
          name: response.data[key]
        }));

        return {
          ...response,
          data
        };
      })
    );

    this.worldpayACHFacade.getACHPaymentFormValue().pipe(
      filter((value: ACHPaymentForm) => !!value),
      tap((value: ACHPaymentForm): void => {
        this.achForm.setValue({
          ...value,
          accountType: { code: value.accountType }
        });
      }),
      takeUntil(this.drop)
    ).subscribe();
  }

  /**
   * Allow only numbers in input fields
   * @since 6.4.2
   * @param $event - Event
   * @param field - Field name
   */
  allowNumbersOnly($event: Event, field: string): void {
    const event: InputEvent = $event as InputEvent;
    const input: HTMLInputElement = event.target as HTMLInputElement;
    const value: string = input.value;
    input.value = value.replace(/[^0-9]/g, '');
    this.achForm.get(field).setValue(input.value);
  }

  /**
   * Submit ACH payment
   * @since 6.4.2
   */
  next(): void {
    const {
      valid,
      value
    } = this.achForm;

    this.sameAsShippingAddress = this.billingAddressFormService.isBillingAddressSameAsDeliveryAddress();

    if (!this.sameAsShippingAddress && !this.billingAddressFormService.isBillingAddressFormValid()) {
      makeFormErrorsVisible(this.billingAddressFormService.getBillingAddressForm());
      return;
    }

    if (valid) {
      const paymentDetails: ApmPaymentDetails = {
        code: PaymentMethod.ACH,
        achPaymentForm: {
          ...this.achForm.value,
          accountType: this.achForm.get('accountType').value.code
        }
      };
      this.worldpayACHFacade.setACHPaymentFormValue(value);

      this.submitting$.next(true);

      let billingAddress = null;
      if (!this.sameAsShippingAddress) {
        billingAddress = this.billingAddressFormService.getBillingAddress();
      }

      this.setPaymentDetails.emit({
        paymentDetails,
        billingAddress
      });
    } else {
      makeFormErrorsVisible(this.achForm);
    }
  }

  /**
   * Return to previous step
   * @since 6.4.0
   */
  return(): void {
    this.back.emit();
  }

  /**
   * On Destroy component
   * @since 6.4.2
   */
  ngOnDestroy(): void {
    this.submitting$.next(false);
    this.drop.next();
    this.drop.complete();
  }

}
