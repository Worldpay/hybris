import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'y-worldpay-apm-submit-buttons',
  template: '',
  standalone: false
})
export class MockWorldpayApmSubmitButtonsComponent {
  @Input() disableContinue: boolean = false;
  @Output() back: EventEmitter<void> = new EventEmitter<void>();
  @Output() continue: EventEmitter<void> = new EventEmitter<void>();
}
