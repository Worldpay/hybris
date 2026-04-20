import { Provider } from '@angular/core';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { OrderService } from '@spartacus/order/core';
import { OrderFacade } from '@spartacus/order/root';
import { WorldpayACHAdapter } from '../connectors';
import { WorldpayACHFacade, WorldpayApmFacade, WorldpayCheckoutPaymentFacade, WorldpayFraudsightFacade, WorldpayGuaranteedPaymentsFacade } from '../facade';
import { OccWorldpayACHAdapter } from '../occ';
import { WorldpayACHService } from './worldpay-ach/worldpay-ach.service';
import { WorldpayApmService } from './worldpay-apm/worldpay-apm.service';
import { WorldpayBillingAddressFormService } from './worldpay-billing-address-form/worldpay-billing-address-form.service';
import { WorldpayCheckoutPaymentService } from './worldpay-checkout/worldpay-checkout-payment.service';
import { WorldpayFraudsightService } from './worldpay-fraudsight/worldpay-fraudsight.service';
import { WorldpayGuaranteedPaymentsService } from './worldpay-guaranteed-payments/worldpay-guaranteed-payments.service';
import { WorldpayOrderService } from './worldpay-order/worldpay-order.service';

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
  },
  WorldpayBillingAddressFormService,
  {
    provide: CheckoutBillingAddressFormService,
    useExisting: WorldpayBillingAddressFormService
  }
];
