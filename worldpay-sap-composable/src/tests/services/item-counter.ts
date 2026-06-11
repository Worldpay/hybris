import { Component, Input } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';

@Component({
  template: '',
  selector: 'cx-item-counter',
})
export class MockItemCounterComponent {
  @Input() min: number;
  @Input() max: number;
  @Input() step: number;
  @Input() control: UntypedFormControl;
}
