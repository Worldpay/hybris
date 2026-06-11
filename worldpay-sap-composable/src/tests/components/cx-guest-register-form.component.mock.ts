import { Component, Input } from '@angular/core';

@Component({
  selector: 'cx-guest-register-form',
  template: ''
})
export class MockCxGuestRegisterFormComponent {
  @Input() guid: string;
  @Input() email: string;
}
