import { Injectable } from '@angular/core';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { CommandService, EventService, UserIdService } from '@spartacus/core';
import { ScheduledReplenishmentOrderConnector, ScheduledReplenishmentOrderService } from '@spartacus/order/core';
import { ScheduledReplenishmentOrderFacade } from '@spartacus/order/root';
import { WorldpayOrderFacade } from '../../facade';

/* eslint-disable @angular-eslint/prefer-inject */
@Injectable()
export class WorldpayReplenishmentOrderService extends ScheduledReplenishmentOrderService implements ScheduledReplenishmentOrderFacade {

  constructor(
    protected override activeCartFacade: ActiveCartFacade,
    protected override userIdService: UserIdService,
    protected override commandService: CommandService,
    protected override scheduledReplenishmentOrderConnector: ScheduledReplenishmentOrderConnector,
    protected override eventService: EventService,
    protected override orderFacade: WorldpayOrderFacade
  ) {
    super(activeCartFacade,
      userIdService,
      commandService,
      scheduledReplenishmentOrderConnector,
      eventService,
      orderFacade
    );
  }
}
