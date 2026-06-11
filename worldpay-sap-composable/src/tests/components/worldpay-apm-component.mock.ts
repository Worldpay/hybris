import { NgTemplateOutlet } from '@angular/common';
import { Component, EventEmitter, Input, Output, TemplateRef } from '@angular/core';
import { Observable } from 'rxjs';
import { ApmData } from '../../core';

@Component({
  selector: 'y-worldpay-apm-component, y-worldpay-b2b-apm-component',
  template: '<ng-container [ngTemplateOutlet]="cardTemplate"></ng-container>',
  imports: [
    NgTemplateOutlet
  ]
})
export class MockWorldpayApmComponent {
  @Input() apms: Observable<ApmData[]>;
  @Input() cardTemplate!: TemplateRef<any>;
  @Output() setPaymentDetails = new EventEmitter<any>();
  @Output() back = new EventEmitter<any>();
  @Input() processing: boolean;
}
