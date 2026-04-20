import { Provider } from '@angular/core';
import {
  WorldpayACHAdapter,
  WorldpayAdapter,
  WorldpayApmAdapter,
  WorldpayApplepayAdapter,
  WorldpayCheckoutPaymentAdapter,
  WorldpayGooglepayAdapter,
  WorldpayUserPaymentAdapter,
} from '../connectors';
import { OccWorldpayACHAdapter } from './adapters/worldpay-ach/occ-worldpay-ach.adapter';
import { OccWorldpayApmAdapter } from './adapters/worldpay-apm/occ-worldpay-apm.adapter';
import { OccWorldpayApplepayAdapter } from './adapters/worldpay-applepay/occ-worldpay-applepay.adapter';
import { OccWorldpayCheckoutPaymentAdapter } from './adapters/worldpay-checkout-payment-connector/occ-worldpay-checkout-payment.adapter';
import { OccWorldpayGooglepayAdapter } from './adapters/worldpay-googlepay/occ-worldpay-googlepay.adapter';
import { OccWorldpayUserPaymentAdapter } from './adapters/worldpay-user-payment/occ-worldpay-user-payment.adapter';
import { OccWorldpayAdapter } from './occ-worldpay.adapter';

export const worldpayAchAdapterProvider: Provider[] = [
  {
    provide: WorldpayACHAdapter,
    useClass: OccWorldpayACHAdapter,
  },
];

export const worldpayAdapterProvider: Provider[] = [
  {
    provide: WorldpayAdapter,
    useClass: OccWorldpayAdapter,
  },
];

export const worldpayApmAdapterProvider: Provider[] = [
  {
    provide: WorldpayApmAdapter,
    useClass: OccWorldpayApmAdapter,
  },
];

export const worldpayApplepayAdapterProvider: Provider[] = [
  {
    provide: WorldpayApplepayAdapter,
    useClass: OccWorldpayApplepayAdapter,
  },
];

export const worldpayCheckoutPaymentAdapterProvider: Provider[] = [
  {
    provide: WorldpayCheckoutPaymentAdapter,
    useClass: OccWorldpayCheckoutPaymentAdapter,
  },
];

export const worldpayGooglepayAdapterProvider: Provider[] = [
  {
    provide: WorldpayGooglepayAdapter,
    useClass: OccWorldpayGooglepayAdapter,
  },
];

export const worldpayUserPaymentAdapterProvider: Provider[] = [
  {
    provide: WorldpayUserPaymentAdapter,
    useClass: OccWorldpayUserPaymentAdapter,
  },
];

/**
 * Aggregates all Worldpay adapter providers into a single array.
 *
 * @since 2211.43.0
 */
export const provideWorldpayAdapters: () => Provider[] = (): Provider[] => [
  ...worldpayAchAdapterProvider,
  ...worldpayAdapterProvider,
  ...worldpayApmAdapterProvider,
  ...worldpayApplepayAdapterProvider,
  ...worldpayCheckoutPaymentAdapterProvider,
  ...worldpayGooglepayAdapterProvider,
  ...worldpayUserPaymentAdapterProvider,
];
