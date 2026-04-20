import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MockWorldpayApmBaseComponent } from './worldpay-apm-base-component.mock';

@Component({
  selector: 'y-worldpay-apm-sepa',
  template: '<ng-container [ngTemplateOutlet]="cardTemplate"></ng-container>',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false
})
export class MockWorldpayApmSepaComponent extends MockWorldpayApmBaseComponent {
}

@Component({
  selector: 'y-worldpay-b2b-apm-sepa',
  template: '<ng-container [ngTemplateOutlet]="cardTemplate"></ng-container>',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false
})
export class MockWorldpayB2BApmSepaComponent extends MockWorldpayApmBaseComponent {
}