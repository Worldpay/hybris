import { TestBed } from '@angular/core/testing';
import { EventService } from '@spartacus/core';
import { WorldpayEventsListener } from '@worldpay-events/worldpay-events.listener';
import { ClearWorldpayPaymentDetailsEvent, SetWorldpaySaveAsDefaultCreditCardEvent, SetWorldpaySavedCreditCardEvent } from '@worldpay-events/worldpay.events';
import { Subject } from 'rxjs';
import { ClearGooglepayEvent } from './googlepay.events';

describe('WorldpayEventsListener', () => {
  let listener: WorldpayEventsListener;
  let eventService: EventService;
  let eventServiceSpy: jasmine.Spy;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        WorldpayEventsListener,
        EventService
      ]
    });

    listener = TestBed.inject(WorldpayEventsListener);
    eventService = TestBed.inject(EventService);
  });

  it('should dispatch events when ClearWorldpayPaymentStateEvent is triggered', () => {
    const clearWorldpayPaymentStateEvent$ = new Subject<void>();
    spyOn(eventService, 'get').and.returnValue(clearWorldpayPaymentStateEvent$);
    eventServiceSpy = spyOn(eventService, 'dispatch');
    
    listener['onOrderPlacedEvent']();

    clearWorldpayPaymentStateEvent$.next();

    expect(eventServiceSpy).toHaveBeenCalledWith(ClearGooglepayEvent);
    expect(eventServiceSpy).toHaveBeenCalledWith({ saved: false }, SetWorldpaySavedCreditCardEvent);
    expect(eventServiceSpy).toHaveBeenCalledWith({ saved: false }, SetWorldpaySaveAsDefaultCreditCardEvent);
    expect(eventServiceSpy).toHaveBeenCalledWith(ClearWorldpayPaymentDetailsEvent);
  });

  it('should unsubscribe from events on destroy', () => {
    spyOn(listener['subscriptions'], 'unsubscribe');

    listener.ngOnDestroy();

    expect(listener['subscriptions'].unsubscribe).toHaveBeenCalled();
  });
});