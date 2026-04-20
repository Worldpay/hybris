import { Directive, EventEmitter, Input, Output, TemplateRef } from '@angular/core';
import { ApmData, PaymentFormData } from '../../core';

@Directive()
export abstract class MockWorldpayApmBaseComponent {
  @Input() apm: ApmData;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  @Input() cardTemplate: TemplateRef<any>;
  @Output() setPaymentDetails: EventEmitter<PaymentFormData> = new EventEmitter<PaymentFormData>();
  @Output() back: EventEmitter<void> = new EventEmitter<void>();
}
