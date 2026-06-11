import { Component, Input } from '@angular/core';
import { ApmData } from '../../core';

@Component({
  selector: 'y-worldpay-apm-ach',
  template: ''
})
export class MockWorldpayAPMACHComponent {
  @Input() apm: ApmData;
}