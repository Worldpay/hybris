import { Component, ViewEncapsulation } from '@angular/core';
import { OrderGuestRegisterFormComponent } from '@spartacus/order/components';

@Component({
  selector: 'y-worldpay-order-guest-register-form',
  templateUrl: './worldpay-order-guest-register-form.component.html',
  encapsulation: ViewEncapsulation.None,
  standalone: false
})
export class WorldpayOrderGuestRegisterFormComponent extends OrderGuestRegisterFormComponent {

}
