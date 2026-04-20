import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormGroup, Validators } from '@angular/forms';
import { QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';
import {
  AccountTypes,
  ACHBankAccountType,
  ACHPaymentForm,
  ApmPaymentDetails,
  FORM_VALIDATION_LIMITS,
  makeFormErrorsVisible,
  PaymentMethod,
  WorldpayACHFacade
} from '../../../../core';
import { WorldpayApmBaseComponent } from '../worldpay-apm-base/worldpay-apm-base.component';

@Component({
  selector: 'y-worldpay-apm-ach',
  templateUrl: './worldpay-apm-ach.component.html',
  styleUrls: ['./worldpay-apm-ach.component.scss'],
  standalone: false
})
export class WorldpayApmAchComponent extends WorldpayApmBaseComponent {
  achBankAccountTypesState$: Observable<QueryState<ACHBankAccountType[]>>;
  /**
   * Form group for ACH payment details.
   * @since 6.4.2
   * @property {UntypedFormGroup} accountType - Nested form group for account type with a required validator.
   * @property {UntypedFormGroup} accountNumber - Form control for account number with required and max length validators.
   * @property {UntypedFormGroup} routingNumber - Form control for routing number with required, min length, and max length validators.
   * @property {UntypedFormGroup} checkNumber - Form control for check number with a max length validator.
   * @property {UntypedFormGroup} companyName - Form control for company name with a max length validator.
   * @property {UntypedFormGroup} customIdentifier - Form control for custom identifier with a max length validator.
   */
  public achForm: UntypedFormGroup = this.fb.group({
    accountType: this.fb.group({ code: null }, { validators: Validators.required }),
    accountNumber: this.fb.control(null, [Validators.required, Validators.maxLength(FORM_VALIDATION_LIMITS.ACCOUNT_NUMBER_MAX)]),
    routingNumber: this.fb.control(null, [
      Validators.required,
      Validators.minLength(FORM_VALIDATION_LIMITS.ROUTING_NUMBER_MIN),
      Validators.maxLength(FORM_VALIDATION_LIMITS.ROUTING_NUMBER_MAX)
    ]),
    checkNumber: this.fb.control(null, [Validators.maxLength(FORM_VALIDATION_LIMITS.CHECK_NUMBER_MAX)]),
    companyName: this.fb.control(null, [Validators.maxLength(FORM_VALIDATION_LIMITS.COMPANY_NAME_MAX)]),
    customIdentifier: this.fb.control(null, [Validators.maxLength(FORM_VALIDATION_LIMITS.CUSTOM_IDENTIFIER_MAX)]),
  });
  protected worldpayACHFacade: WorldpayACHFacade = inject(WorldpayACHFacade);

  /**
   * Initialize component
   * @since 6.4.2
   */
  override ngOnInit(): void {
    super.ngOnInit();
    this.achBankAccountTypesState$ = this.worldpayACHFacade.getACHBankAccountTypesState().pipe(
      filter((state: QueryState<AccountTypes>): boolean => !!state.data),
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
      filter((value: ACHPaymentForm): boolean => !!value),
      tap((value: ACHPaymentForm): void => {
        this.achForm.setValue({
          ...value,
          accountType: { code: value.accountType }
        });
      }),
      takeUntilDestroyed(this.destroyRef)
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
    if (this.achForm.valid) {
      const paymentDetails: ApmPaymentDetails = {
        code: PaymentMethod.ACH,
        achPaymentForm: {
          ...this.achForm.value,
          accountType: this.achForm.get('accountType')?.value?.code
        }
      };
      this.worldpayACHFacade.setACHPaymentFormValue(this.achForm.value);
      this.createPaymentDetails(paymentDetails);
    } else {
      makeFormErrorsVisible(this.achForm);
    }
  }
}
