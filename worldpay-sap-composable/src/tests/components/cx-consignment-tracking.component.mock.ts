import { Component, Input } from '@angular/core';
import { Consignment } from '@spartacus/order/root';

@Component({
  selector: 'cx-consignment-tracking',
  template: ''
})
export class MockCxConsignmentTrackingComponent {
  @Input()
    consignment: Consignment;
  @Input()
    orderCode: string;
}
