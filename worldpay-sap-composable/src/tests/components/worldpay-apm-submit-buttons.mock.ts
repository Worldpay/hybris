import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
  selector: 'y-worldpay-apm-submit-buttons',
  template: '',
  standalone: false
})
export class MockWorldpayApmSubmitButtonsComponent {
  @Input() disableContinue$: Observable<boolean>;
  @Input() dataTestId: string;
  @Output() back: EventEmitter<void> = new EventEmitter<void>();
  @Output() continue: EventEmitter<void> = new EventEmitter<void>();
}
