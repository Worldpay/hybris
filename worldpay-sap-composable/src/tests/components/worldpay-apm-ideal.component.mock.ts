import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Address } from '@spartacus/core';
import { ApmData, ApmPaymentDetails } from '../../core';

@Component({
  selector: 'y-worldpay-apm-ideal',
  template: ''
})
export class MockWorldpayApmIdealComponent {
  @Input() apm: ApmData;
  @Output() setPaymentDetails = new EventEmitter<{ paymentDetails: ApmPaymentDetails; billingAddress: Address }>();
}
