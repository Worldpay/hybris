import { Component, Input } from '@angular/core';
import { ICON_TYPE } from '@spartacus/storefront';

@Component({
  selector: 'cx-icon',
  template: ''
})
export class MockCxIconComponent {
  @Input() type: ICON_TYPE;
  @Input() tooltip: any;
}
