import { Provider } from '@angular/core';
import { CheckoutBillingAddressFormService } from '@spartacus/checkout/base/components';
import { CheckoutPaymentFacade } from '@spartacus/checkout/base/root';
import { UserPaymentService } from '@spartacus/core';
import { OrderService } from '@spartacus/order/core';
import { OrderFacade } from '@spartacus/order/root';
import {
  WorldpayACHService,
  WorldpayApmService,
  WorldpayApplepayService,
  WorldpayBillingAddressFormService,
  WorldpayCheckoutPaymentService,
  WorldpayGooglepayService,
  WorldpayOrderService,
  WorldpayUserPaymentService,
} from '../services';
import { WorldpayACHFacade } from './worldpay-ach.facade';
import { WorldpayApmFacade } from './worldpay-apm-facade';
import { WorldpayApplepayFacade } from './worldpay-applepay.facade';
import { WorldpayCheckoutPaymentFacade } from './worldpay-checkout-payment.facade';
import { WorldpayGooglepayFacade } from './worldpay-googlepay.facade';
import { WorldpayOrderFacade } from './worldpay-order.facade';

export const worldpayACHFacadeProviders: Provider[] = [
  WorldpayACHService,
  {
    provide: WorldpayACHFacade,
    useExisting: WorldpayACHService,
  },
];

export const worldpayApmFacadeProviders: Provider[] = [
  WorldpayApmService,
  {
    provide: WorldpayApmFacade,
    useExisting: WorldpayApmService,
  },
];

export const worldpayApplepayFacadeProviders: Provider[] = [
  WorldpayApplepayService,
  {
    provide: WorldpayApplepayFacade,
    useExisting: WorldpayApplepayService,
  },
];

export const worldpayBillingAddressFormProviders: Provider[] = [
  WorldpayBillingAddressFormService,
  {
    provide: CheckoutBillingAddressFormService,
    useExisting: WorldpayBillingAddressFormService,
  },
];

export const worldpayCheckoutPaymentFacadeProviders: Provider[] = [
  WorldpayCheckoutPaymentService,
  {
    provide: CheckoutPaymentFacade,
    useExisting: WorldpayCheckoutPaymentService,
  },
  {
    provide: WorldpayCheckoutPaymentFacade,
    useExisting: WorldpayCheckoutPaymentService,
  },
];

export const worldpayGooglepayFacadeProviders: Provider[] = [
  WorldpayGooglepayService,
  {
    provide: WorldpayGooglepayFacade,
    useExisting: WorldpayGooglepayService,
  },
];

export const worldpayOrderFacadeProviders: Provider[] = [
  WorldpayOrderService,
  {
    provide: OrderFacade,
    useExisting: WorldpayOrderService,
  },
  {
    provide: WorldpayOrderFacade,
    useExisting: WorldpayOrderService,
  },
  {
    provide: OrderService,
    useExisting: WorldpayOrderService,
  },
];

export const worldpayUserPaymentFacadeProviders: Provider[] = [
  WorldpayUserPaymentService,
  {
    provide: UserPaymentService,
    useExisting: WorldpayUserPaymentService,
  },
];

/**
 * Aggregates all Worldpay facade providers into a single array.
 *
 * @since 2211.43.0
 */
export const worldpayFacadesProviders: Provider[] = [
  ...worldpayUserPaymentFacadeProviders,
  ...worldpayACHFacadeProviders,
  ...worldpayApmFacadeProviders,
  ...worldpayApplepayFacadeProviders,
  ...worldpayBillingAddressFormProviders,
  ...worldpayCheckoutPaymentFacadeProviders,
  ...worldpayGooglepayFacadeProviders,
  ...worldpayOrderFacadeProviders,
];
