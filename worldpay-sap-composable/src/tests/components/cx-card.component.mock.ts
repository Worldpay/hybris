import { JsonPipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { CardComponent } from '@spartacus/storefront';

@Component({
  selector: 'cx-card',
  imports: [
    JsonPipe
  ],
  template: '{{content | json}}'
})
export class MockCxCardComponent extends CardComponent{

}
