import { AsyncPipe } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { Observable } from 'rxjs';

@Component({
  selector: 'y-worldpay-apm-submit-buttons',
  templateUrl: './worldpay-apm-submit-buttons.component.html',
  imports: [
    I18nModule,
    AsyncPipe
  ]
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
