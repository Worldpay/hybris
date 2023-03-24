import { ChangeDetectionStrategy, Component, ViewEncapsulation, } from '@angular/core';
import { OrderConfirmationOverviewComponent } from '@spartacus/checkout/components';

@Component({
  selector: 'y-worldpay-order-confirmation-overview',
  templateUrl: './worldpay-order-confirmation-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
})
export class WorldpayOrderConfirmationOverviewComponent extends OrderConfirmationOverviewComponent {

}
