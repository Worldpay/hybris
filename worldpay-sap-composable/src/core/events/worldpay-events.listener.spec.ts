import { TestBed } from '@angular/core/testing';
import { EventService } from '@spartacus/core';
import { Subject } from 'rxjs';
import { WorldpayClearBillingAddressFormEvent } from './billing-address-form.events';
import { ClearGooglepayEvent } from './googlepay.events';
import { WorldpayEventsListener } from './worldpay-events.listener';
import { ClearWorldpayPaymentDetailsEvent, SetWorldpaySaveAsDefaultCreditCardEvent, SetWorldpaySavedCreditCardEvent } from './worldpay.events';

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
    eventServiceSpy = spyOn(eventService, 'dispatch').and.callThrough();

    listener['onOrderPlacedEvent']();

    clearWorldpayPaymentStateEvent$.next();

    expect(eventServiceSpy).toHaveBeenCalledWith({}, ClearGooglepayEvent);
    expect(eventServiceSpy).toHaveBeenCalledWith({ saved: false }, SetWorldpaySavedCreditCardEvent);
    expect(eventServiceSpy).toHaveBeenCalledWith({ saved: false }, SetWorldpaySaveAsDefaultCreditCardEvent);
    expect(eventServiceSpy).toHaveBeenCalledWith({}, ClearWorldpayPaymentDetailsEvent);
    expect(eventServiceSpy).toHaveBeenCalledWith({}, WorldpayClearBillingAddressFormEvent);
  });
});