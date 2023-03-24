import { CmsComponentData } from '@spartacus/storefront';
import { WorldpayApmModel } from './worldpay-apm.model';
import { WorldpayCheckoutPaymentService } from '../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';

export abstract class AbstractWorldpayApmComponent {
  code: string;

  constructor(
    protected checkoutPaymentService: WorldpayCheckoutPaymentService
  ) {}

  selectApm() {
    this.checkoutPaymentService.selectAPM(this.code);
  }
}
