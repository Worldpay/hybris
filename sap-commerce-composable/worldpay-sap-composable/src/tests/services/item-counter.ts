import { Component, Input } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';

@Component({
  template: '',
  selector: 'cx-item-counter',
  standalone: false
})
export class MockItemCounterComponent {
  @Input() min: number;
  @Input() max: number;
  @Input() step: number;
  @Input() control: UntypedFormControl;
}
