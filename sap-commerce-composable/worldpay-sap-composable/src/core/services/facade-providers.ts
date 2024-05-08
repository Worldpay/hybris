import { Provider } from '@angular/core';
import { WorldpayFraudsightFacade } from '../facade/worldpay-fraudsight.facade';
import { WorldpayFraudsightService } from './worldpay-fraudsight/worldpay-fraudsight.service';
import { WorldpayGuaranteedPaymentsService } from './worldpay-guaranteed-payments/worldpay-guaranteed-payments.service';
import { WorldpayGuaranteedPaymentsFacade } from '../facade/worldpay-guaranteed-payments.facade';
import { WorldpayApmService } from './worldpay-apm/worldpay-apm.service';
import { WorldpayApmFacade } from '../facade/worldpay-apm-facade';
import { WorldpayCheckoutPaymentFacade } from '../facade/worldpay-checkout-payment.facade';
import { WorldpayCheckoutPaymentService } from './worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayOrderService } from './worldpay-order/worldpay-order.service';
import { OrderFacade } from '@spartacus/order/root';
import { OrderService } from '@spartacus/order/core';
import { WorldpayACHService } from './worldpay-ach/worldpay-ach.service';
import { WorldpayACHFacade } from '../facade/worldpay-ach.facade';
import { WorldpayACHAdapter } from '../connectors/worldpay-ach/worldpay-ach.adapter';
import { OccWorldpayACHAdapter } from '../occ/adapters/worldpay-ach/occ-worldpay-ach.adapter';

export const worldpayFacadeProviders: Provider[] = [
  WorldpayOrderService,
  {
    provide: OrderFacade,
    useExisting: WorldpayOrderService
  },
  {
    provide: OrderService,
    useExisting: WorldpayOrderService
  },
  WorldpayGuaranteedPaymentsService,
  {
    provide: WorldpayGuaranteedPaymentsFacade,
    useExisting: WorldpayGuaranteedPaymentsService
  },
  WorldpayFraudsightService,
  {
    provide: WorldpayFraudsightFacade,
    useExisting: WorldpayFraudsightService
  },
  WorldpayApmService,
  {
    provide: WorldpayApmFacade,
    useExisting: WorldpayApmService
  },
  WorldpayCheckoutPaymentService,
  {
    provide: WorldpayCheckoutPaymentFacade,
    useExisting: WorldpayCheckoutPaymentService
  },
  WorldpayACHService,
  {
    provide: WorldpayACHFacade,
    useExisting: WorldpayACHService
  },
  {
    provide: WorldpayACHAdapter,
    useClass: OccWorldpayACHAdapter
  }
];
