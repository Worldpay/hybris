import { Component, Input } from '@angular/core';
import { ApmData } from '../../core';

@Component({
  selector: 'y-worldpay-apm-tile',
  template: '',
})
export class MockWorldpayApmTileComponent {
  @Input() apm: ApmData;
}