import { Provider } from '@angular/core';
import { CheckoutDeliveryAddressConnector } from '@spartacus/checkout/base/core';
import { OrderConnector } from '@spartacus/order/core';
import { WorldpayACHConnector } from './worldpay-ach/worldpay-ach.connector';
import { WorldpayApmConnector } from './worldpay-apm/worldpay-apm.connector';
import { WorldpayApplepayConnector } from './worldpay-applepay/worldpay-applepay.connector';
import { WorldpayUserPaymentConnector } from './worldpay-checkout-user-payment';
import { WorldpayGooglePayConnector } from './worldpay-googlepay/worldpay-googlepay.connector';
import { WorldpayCheckoutPaymentConnector } from './worldpay-payment-connector/worldpay-checkout-payment.connector';
import { WorldpayConnector } from './worldpay.connector';

export const worldpayAchConnectorProvider: Provider[] = [WorldpayACHConnector];

export const worldpayApmConnectorProvider: Provider[] = [WorldpayApmConnector];

export const worldpayApplepayConnectorProvider: Provider[] = [WorldpayApplepayConnector];

export const worldpayCheckoutPaymentConnectorProvider: Provider[] = [WorldpayCheckoutPaymentConnector];

export const worldpayConnectorProvider: Provider[] = [WorldpayConnector];

export const worldpayDeliveryAddressConnectorProvider: Provider[] = [CheckoutDeliveryAddressConnector];

export const worldpayGooglepayConnectorProvider: Provider[] = [WorldpayGooglePayConnector];

export const worldpayOrderConnectorProvider: Provider[] = [OrderConnector];

export const worldpayUserPaymentConnectorProvider: Provider[] = [WorldpayUserPaymentConnector];

/**
 * Aggregates all Worldpay connector providers into a single array.
 *
 * @since 2211.43.0
 */
export const provideWorldpayConnectors: () => Provider[] = (): Provider[] => [
  ...worldpayAchConnectorProvider,
  ...worldpayApmConnectorProvider,
  ...worldpayApplepayConnectorProvider,
  ...worldpayConnectorProvider,
  ...worldpayCheckoutPaymentConnectorProvider,
  ...worldpayDeliveryAddressConnectorProvider,
  ...worldpayGooglepayConnectorProvider,
  ...worldpayOrderConnectorProvider,
  ...worldpayUserPaymentConnectorProvider,
];
