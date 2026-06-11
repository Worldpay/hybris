import { Injectable } from '@angular/core';
import { facadeFactory } from '@spartacus/core';
import { ScheduledReplenishmentOrderFacade } from '@spartacus/order/root';
import { WORLDPAY_REPLENISHMENT_ORDER_FEATURE } from '../../../core/facade/worldpay-feature-name';

/**
 * Facade for Worldpay Replenishment Order integration.
 *
 * This abstract class defines the contract for managing Replenishment Order configuration,
 * requesting merchant configuration, authorising orders, and handling checkout preconditions
 * for the Worldpay Replenishment Order feature.
 *
 * @since 2211.43.0
 */
@Injectable({
  providedIn: 'root',
  useFactory: (): WorldpayReplenishmentOrderFacade =>
    facadeFactory({
      facade: WorldpayReplenishmentOrderFacade,
      feature: WORLDPAY_REPLENISHMENT_ORDER_FEATURE,
      methods: ['scheduleReplenishmentOrder'],
    }),
})
export abstract class WorldpayReplenishmentOrderFacade extends ScheduledReplenishmentOrderFacade {

}
