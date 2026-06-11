import { Provider } from '@angular/core';
import { ScheduledReplenishmentOrderConnector } from '@spartacus/order/core';

export const scheduleReplenishmentConnectorProvider: Provider[] = [ScheduledReplenishmentOrderConnector];
/**
 * Aggregates all Worldpay connector providers into a single array.
 *
 * @since 2211.43.0
 */
export const worldpayB2bConnectorsProviders: () => Provider[] = (): Provider[] => [
  ...scheduleReplenishmentConnectorProvider,
];
