import { Component, Input } from '@angular/core';
import { CardComponent } from '@spartacus/storefront';

@Component({
  selector: 'cx-card',
  template: '{{content | json}}',
  standalone: false,
})
export class MockCxCardComponent extends CardComponent{

}
