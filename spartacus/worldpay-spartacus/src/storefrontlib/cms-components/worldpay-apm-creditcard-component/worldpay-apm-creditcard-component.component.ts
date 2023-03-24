import { Component, HostListener, OnInit } from '@angular/core';
import { AbstractWorldpayApmComponent } from '../worldpay-apm-component/abstract-worldpay-apm.component';
import { WorldpayCheckoutPaymentService } from '../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';
import { CmsComponentData } from '@spartacus/storefront';
import { WorldpayCCModel } from '../worldpay-apm-component/worldpay-apm.model';

@Component({
  selector: 'y-worldpay-apm-creditcard-component',
  templateUrl: './worldpay-apm-creditcard-component.component.html',
  styleUrls: ['./worldpay-apm-creditcard-component.component.scss']
})
export class WorldpayApmCreditcardComponent extends AbstractWorldpayApmComponent
  implements OnInit {
  code: string = 'credit card';
  isSelected: boolean = false;

  constructor(
    public componentData: CmsComponentData<WorldpayCCModel>,
    protected checkoutPaymentService: WorldpayCheckoutPaymentService
  ) {
    super(checkoutPaymentService);
  }

  ngOnInit(): void {
    this.checkoutPaymentService
      .getSelectedAPMFromState()
      .subscribe(selectedAPM => (this.isSelected = this.code === selectedAPM));
  }

  @HostListener('click') onClick(): void {
    super.selectApm();
  }
}
