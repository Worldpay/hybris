import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { ApmData } from '../../core';

@Component({
  selector: 'y-worldpay-b2b-apm-component',
  template: '',
})
export class MockWorldpayB2BApmComponent {
  @Input() apms: Observable<ApmData[]>;
  @Output() setPaymentDetails = new EventEmitter<any>();
  @Output() back = new EventEmitter<any>();
  @Input() processing: boolean;
}