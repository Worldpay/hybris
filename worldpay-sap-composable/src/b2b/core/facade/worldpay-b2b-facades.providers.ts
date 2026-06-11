import { Provider } from '@angular/core';
import { ScheduledReplenishmentOrderFacade } from '@spartacus/order/root';
import { WorldpayReplenishmentOrderService } from '../../../core/services/worldpay-replenishment-order/worldpay-replenishment-order.service';
import { WorldpayReplenishmentOrderFacade } from './worldpay-replenishment-order.facade';

export const worldpayB2bOrderFacadeProviders: Provider[] = [
  WorldpayReplenishmentOrderService,
  {
    provide: ScheduledReplenishmentOrderFacade,
    useExisting: WorldpayReplenishmentOrderService
  },
  {
    provide: WorldpayReplenishmentOrderFacade,
    useExisting: WorldpayReplenishmentOrderService
  }
];

/**
 * Aggregates all Worldpay B2b facade providers into a single array.
 *
 * @since 221121.11.0
 */
export const worldpayB2bFacadesProviders: Provider[] = [
  ...worldpayB2bOrderFacadeProviders,
];
