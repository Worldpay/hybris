import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Address, QueryState } from '@spartacus/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { ACHBankAccountType, ACHPaymentForm, ApmData, ApmPaymentDetails, PaymentMethod } from '../../../../core/interfaces';
import { makeFormErrorsVisible } from '../../../../core/utils';
import { WorldpayACHFacade } from '../../../../core/facade/worldpay-ach.facade';
import { filter, map, takeUntil, tap } from 'rxjs/operators';

@Component({
  selector: 'y-worldpay-apm-ach',
  templateUrl: './worldpay-apm-ach.component.html',
  styleUrls: ['./worldpay-apm-ach.component.scss']
})
export class WorldpayApmAchComponent implements OnInit, OnDestroy {
  @Input() apm: ApmData;
  @Input() billingAddressForm: UntypedFormGroup = new UntypedFormGroup({});
  @Output() setPaymentDetails = new EventEmitter<{ paymentDetails: ApmPaymentDetails; billingAddress: Address }>();
  @Output() back: EventEmitter<void> = new EventEmitter<void>();

  achBankAccountTypesState$: Observable<QueryState<ACHBankAccountType[]>>;

  public submitting$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public sameAsShippingAddress: boolean = true;
  private drop: Subject<void> = new Subject<void>();

  achForm: UntypedFormGroup = this.fb.group({
    accountType: this.fb.group({ code: null }, [Validators.required]),
    accountNumber: this.fb.control(null, [Validators.required, Validators.maxLength(17)]),
    routingNumber: this.fb.control(null, [Validators.required, Validators.minLength(8), Validators.maxLength(9)]),
    checkNumber: this.fb.control(null, [Validators.maxLength(15)]),
    companyName: this.fb.control(null, [Validators.maxLength(40)]),
    customIdentifier: this.fb.control(null, [Validators.maxLength(15)]),
  });

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
    const {
      invalid: billingInvalid,
      value: billingValue
    } = this.billingAddressForm;

    if (!this.sameAsShippingAddress && billingInvalid) {
      makeFormErrorsVisible(this.billingAddressForm);
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
        billingAddress = billingValue;
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
