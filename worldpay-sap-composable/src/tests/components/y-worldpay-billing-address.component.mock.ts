import { Component, Output } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'y-worldpay-billing-address',
  template: ''
})
export class MockWorldpayBillingAddressComponent {
  @Output() sameAsShippingAddressChange: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
}