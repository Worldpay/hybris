import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
  selector: 'y-worldpay-apm-submit-buttons',
  templateUrl: './worldpay-apm-submit-buttons.component.html',
  standalone: false
})
export class WorldpayApmSubmitButtonsComponent {
  @Input() disableContinue$: Observable<boolean>;
  @Output() back: EventEmitter<void> = new EventEmitter<void>();
  @Output() continue: EventEmitter<void> = new EventEmitter<void>();
  @Input() dataTestId: string;

  onBack(): void {
    this.back.emit();
  }

  onContinue(): void {
    this.continue.emit();
  }
}
