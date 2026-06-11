import { inject, TestBed } from '@angular/core/testing';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import {
  EventService,
  OCC_USER_ID_CURRENT,
  UserIdService,
} from '@spartacus/core';
import { ScheduledReplenishmentOrderConnector } from '@spartacus/order/core';
import {
  ReplenishmentOrder,
  ReplenishmentOrderScheduledEvent,
  ScheduleReplenishmentForm,
} from '@spartacus/order/root';
import { EMPTY, of } from 'rxjs';
import { WorldpayOrderFacade } from '../../facade';
import { WorldpayReplenishmentOrderService } from './worldpay-replenishment-order.service';

import createSpy = jasmine.createSpy;

const mockUserId = OCC_USER_ID_CURRENT;
const mockCartId = 'cartID';
const mockReplenishmentOrder: ReplenishmentOrder = {
  replenishmentOrderCode: 'replenishmentOrderCode',
};
const mockScheduleReplenishmentForm: ScheduleReplenishmentForm = {
  numberOfDays: '1',
};
const termsChecked = true;

class MockActiveCartService implements Partial<ActiveCartFacade> {
  takeActiveCartId = createSpy().and.returnValue(of(mockCartId));
  isGuestCart = createSpy().and.returnValue(of(false));
}

class MockUserIdService implements Partial<UserIdService> {
  takeUserId = createSpy().and.returnValue(of(mockUserId));
}

class MockEventService implements Partial<EventService> {
  get = createSpy().and.returnValue(EMPTY);
  dispatch = createSpy();
}

class MockScheduledReplenishmentOrderConnector implements Partial<ScheduledReplenishmentOrderConnector>
{
  scheduleReplenishmentOrder = createSpy().and.returnValue(
    of(mockReplenishmentOrder)
  );
}

class MockWorldpayOrderFacade implements Partial<WorldpayOrderFacade> {
  setPlacedOrder = createSpy();
}

describe('WorldpayReplenishmentOrderService', () => {
  let service: WorldpayReplenishmentOrderService;
  let connector: ScheduledReplenishmentOrderConnector;
  let orderFacade: WorldpayOrderFacade;
  let eventService: EventService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        WorldpayReplenishmentOrderService,
        { provide: ActiveCartFacade, useClass: MockActiveCartService },
        { provide: UserIdService, useClass: MockUserIdService },
        { provide: EventService, useClass: MockEventService },
        {
          provide: ScheduledReplenishmentOrderConnector,
          useClass: MockScheduledReplenishmentOrderConnector,
        },
        {
          provide: WorldpayOrderFacade,
          useClass: MockWorldpayOrderFacade,
        },
      ],
    });

    service = TestBed.inject(WorldpayReplenishmentOrderService);
    connector = TestBed.inject(ScheduledReplenishmentOrderConnector);
    orderFacade = TestBed.inject(WorldpayOrderFacade);
    eventService = TestBed.inject(EventService);
  });

  it('should inject WorldpayReplenishmentOrderService', inject(
    [WorldpayReplenishmentOrderService],
    (
      WorldpayReplenishmentOrderService: WorldpayReplenishmentOrderService
    ) => {
      expect(WorldpayReplenishmentOrderService).toBeTruthy();
    }
  ));

  describe('scheduleReplenishmentOrder', () => {
    it('should call checkoutDeliveryConnector.createAddress', () => {
      service.scheduleReplenishmentOrder(
        mockScheduleReplenishmentForm,
        termsChecked
      ).subscribe();

      expect(connector.scheduleReplenishmentOrder).toHaveBeenCalledWith(
        mockCartId,
        mockScheduleReplenishmentForm,
        termsChecked,
        mockUserId
      );
    });

    it('should call orderFacade', () => {
      service.scheduleReplenishmentOrder(
        mockScheduleReplenishmentForm,
        termsChecked
      ).subscribe();

      expect(orderFacade.setPlacedOrder).toHaveBeenCalledWith(
        mockReplenishmentOrder
      );
    });

    // TODO:#deprecation-checkout Replace with event testing once we remove ngrx store.
    it('should dispatch ReplenishmentOrderScheduledEvent', () => {
      service.scheduleReplenishmentOrder(
        mockScheduleReplenishmentForm,
        termsChecked
      ).subscribe();

      expect(eventService.dispatch).toHaveBeenCalledWith(
        {
          userId: mockUserId,
          cartId: mockCartId,
          cartCode: mockCartId,
          replenishmentOrder: mockReplenishmentOrder,
        },
        ReplenishmentOrderScheduledEvent
      );
    });
  });
});
