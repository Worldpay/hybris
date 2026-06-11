import { DestroyRef } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { CheckoutPaymentDetailsCreatedEvent } from '@spartacus/checkout/base/root';
import { CurrencySetEvent, EventService, LoginEvent, LogoutEvent } from '@spartacus/core';
import { OrderPlacedEvent } from '@spartacus/order/root';
import { Subject } from 'rxjs';
import { MockWorldpayBillingAddressFormService } from 'worldpay-sap-composable-tests';
import { WorldpayBillingAddressFormService } from '../services';
import { WorldpayBillingAddressSameAsDeliveryAddressSetEvent, WorldpayClearBillingAddressFormEvent } from './billing-address-form.events';
import { WorldpayBillingAddressFormEventsListener } from './billing-address-form.events.listener';
import { WorldpayBillingAddressCreatedEvent, WorldpayBillingAddressUpdatedEvent } from './billing-address.events';
import { SetPaymentAddressEvent } from './checkout-payment.events';

describe('WorldpayBillingAddressFormEventsListener', () => {
  let listener: WorldpayBillingAddressFormEventsListener;
  let eventService: EventService;
  let billingAddressFormService: WorldpayBillingAddressFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        WorldpayBillingAddressFormEventsListener,
        EventService,
        {
          provide: WorldpayBillingAddressFormService,
          useClass: MockWorldpayBillingAddressFormService
        },
        DestroyRef
      ]
    });

    listener = TestBed.inject(WorldpayBillingAddressFormEventsListener);
    eventService = TestBed.inject(EventService);
    billingAddressFormService = TestBed.inject(WorldpayBillingAddressFormService);
  });

  it('should create', () => {
    expect(listener).toBeTruthy();
  });

  describe('onSameAsBillingAddressChange', () => {
    it('should disable edit toggle state when delivery address is undefined', () => {
      const sameAsBillingAddressEvent$ = new Subject<WorldpayBillingAddressSameAsDeliveryAddressSetEvent>();
      spyOn(eventService, 'get').and.returnValue(sameAsBillingAddressEvent$);
      spyOn(billingAddressFormService, 'setEditToggleState');

      listener['onSameAsBillingAddressChange']();

      sameAsBillingAddressEvent$.next({
        billingAddress: { firstName: 'John' },
        deliveryAddress: undefined
      } as any);

      expect(billingAddressFormService.setEditToggleState).toHaveBeenCalledWith(false);
    });

    it('should update delivery address when delivery address is defined', () => {
      const sameAsBillingAddressEvent$ = new Subject<WorldpayBillingAddressSameAsDeliveryAddressSetEvent>();
      spyOn(eventService, 'get').and.returnValue(sameAsBillingAddressEvent$);
      spyOn(billingAddressFormService, 'updateDeliveryAddress');

      listener['onSameAsBillingAddressChange']();

      const billingAddress = { firstName: 'John' };
      const deliveryAddress = { firstName: 'Jane' };
      sameAsBillingAddressEvent$.next({
        billingAddress,
        deliveryAddress
      } as any);

      expect(billingAddressFormService.updateDeliveryAddress).toHaveBeenCalledWith(billingAddress, deliveryAddress);
    });

    it('should not call updateDeliveryAddress when delivery address is undefined', () => {
      const sameAsBillingAddressEvent$ = new Subject<WorldpayBillingAddressSameAsDeliveryAddressSetEvent>();
      spyOn(eventService, 'get').and.returnValue(sameAsBillingAddressEvent$);
      spyOn(billingAddressFormService, 'updateDeliveryAddress');
      spyOn(billingAddressFormService, 'setEditToggleState');

      listener['onSameAsBillingAddressChange']();

      sameAsBillingAddressEvent$.next({
        billingAddress: { firstName: 'John' },
        deliveryAddress: undefined
      } as any);

      expect(billingAddressFormService.setEditToggleState).toHaveBeenCalledWith(false);
      expect(billingAddressFormService.updateDeliveryAddress).not.toHaveBeenCalled();
    });
  });

  describe('onWorldpayBillingAddressUpdatedEvent', () => {
    it('should set billing address when WorldpayBillingAddressUpdatedEvent is triggered', () => {
      const billingAddressUpdatedEvent$ = new Subject<WorldpayBillingAddressUpdatedEvent>();
      spyOn(eventService, 'get').and.returnValue(billingAddressUpdatedEvent$);
      spyOn(billingAddressFormService, 'setBillingAddress');

      listener['onWorldpayBillingAddressUpdatedEvent']();

      const billingAddress = { firstName: 'John' };
      const deliveryAddress = { firstName: 'Jane' };
      billingAddressUpdatedEvent$.next({
        billingAddress,
        deliveryAddress
      } as any);

      expect(billingAddressFormService.setBillingAddress).toHaveBeenCalledWith(billingAddress, deliveryAddress);
    });

    it('should handle event with empty billing address', () => {
      const billingAddressUpdatedEvent$ = new Subject<WorldpayBillingAddressUpdatedEvent>();
      spyOn(eventService, 'get').and.returnValue(billingAddressUpdatedEvent$);
      spyOn(billingAddressFormService, 'setBillingAddress');

      listener['onWorldpayBillingAddressUpdatedEvent']();

      billingAddressUpdatedEvent$.next({
        billingAddress: {},
        deliveryAddress: {}
      } as any);

      expect(billingAddressFormService.setBillingAddress).toHaveBeenCalledWith({}, {});
    });
  });

  describe('onWorldpayClearBillingAddressFormEvent', () => {
    it('should reset form and disable edit toggle on clear billing address form event', () => {
      const clearEvent$ = new Subject<WorldpayClearBillingAddressFormEvent>();
      // @ts-ignore
      spyOn(eventService, 'get').and.callFake((eventType: any) => {
        if (eventType === WorldpayClearBillingAddressFormEvent) {
          return clearEvent$;
        }
        return new Subject();
      });
      spyOn(billingAddressFormService, 'resetBillingAddressForm');
      spyOn(billingAddressFormService, 'setEditToggleState');

      listener['onWorldpayClearBillingAddressFormEvent']();

      clearEvent$.next({} as any);

      expect(billingAddressFormService.resetBillingAddressForm).toHaveBeenCalled();
      expect(billingAddressFormService.setEditToggleState).toHaveBeenCalledWith(false);
    });

    it('should reset form and disable edit toggle on login event', () => {
      const loginEvent$ = new Subject<LoginEvent>();
      // @ts-ignore
      spyOn(eventService, 'get').and.callFake((eventType: any) => {
        if (eventType === LoginEvent) {
          return loginEvent$;
        }
        return new Subject();
      });
      spyOn(billingAddressFormService, 'resetBillingAddressForm');
      spyOn(billingAddressFormService, 'setEditToggleState');

      listener['onWorldpayClearBillingAddressFormEvent']();

      loginEvent$.next({} as any);

      expect(billingAddressFormService.resetBillingAddressForm).toHaveBeenCalled();
      expect(billingAddressFormService.setEditToggleState).toHaveBeenCalledWith(false);
    });

    it('should reset form and disable edit toggle on logout event', () => {
      const logoutEvent$ = new Subject<LogoutEvent>();
      // @ts-ignore
      spyOn(eventService, 'get').and.callFake((eventType: any) => {
        if (eventType === LogoutEvent) {
          return logoutEvent$;
        }
        return new Subject();
      });
      spyOn(billingAddressFormService, 'resetBillingAddressForm');
      spyOn(billingAddressFormService, 'setEditToggleState');

      listener['onWorldpayClearBillingAddressFormEvent']();

      logoutEvent$.next({} as any);

      expect(billingAddressFormService.resetBillingAddressForm).toHaveBeenCalled();
      expect(billingAddressFormService.setEditToggleState).toHaveBeenCalledWith(false);
    });

    it('should reset form and disable edit toggle on order placed event', () => {
      const orderPlacedEvent$ = new Subject<OrderPlacedEvent>();
      // @ts-ignore
      spyOn(eventService, 'get').and.callFake((eventType: any) => {
        if (eventType === OrderPlacedEvent) {
          return orderPlacedEvent$;
        }
        return new Subject();
      });
      spyOn(billingAddressFormService, 'resetBillingAddressForm');
      spyOn(billingAddressFormService, 'setEditToggleState');

      listener['onWorldpayClearBillingAddressFormEvent']();

      orderPlacedEvent$.next({} as any);

      expect(billingAddressFormService.resetBillingAddressForm).toHaveBeenCalled();
      expect(billingAddressFormService.setEditToggleState).toHaveBeenCalledWith(false);
    });
  });

  describe('onCurrencyChangeEvent', () => {
    it('should disable edit toggle state when currency changes', () => {
      const currencySetEvent$ = new Subject<CurrencySetEvent>();
      spyOn(eventService, 'get').and.returnValue(currencySetEvent$);
      spyOn(billingAddressFormService, 'setEditToggleState');

      listener['onCurrencyChangeEvent']();

      currencySetEvent$.next({} as any);

      expect(billingAddressFormService.setEditToggleState).toHaveBeenCalledWith(false);
    });
  });

  describe('onWorldpayBillingChangeEvent', () => {
    it('should set billing address on CheckoutPaymentDetailsCreatedEvent', () => {
      const checkoutPaymentDetailsEvent$ = new Subject<CheckoutPaymentDetailsCreatedEvent>();
      const mockBillingAddress = { firstName: 'John' };
      // @ts-ignore
      spyOn(eventService, 'get').and.callFake((eventType: any) => {
        if (eventType === CheckoutPaymentDetailsCreatedEvent) {
          return checkoutPaymentDetailsEvent$;
        }
        return new Subject();
      });
      spyOn(billingAddressFormService, 'getBillingAddress').and.returnValue(mockBillingAddress);
      spyOn(billingAddressFormService, 'setBillingAddress');

      listener['onWorldpayBillingChangeEvent']();

      checkoutPaymentDetailsEvent$.next({} as any);

      expect(billingAddressFormService.setBillingAddress).toHaveBeenCalledWith(mockBillingAddress);
    });

    it('should set billing address on WorldpayBillingAddressCreatedEvent', () => {
      const billingAddressCreatedEvent$ = new Subject<WorldpayBillingAddressCreatedEvent>();
      const mockBillingAddress = { firstName: 'John' };
      // @ts-ignore
      spyOn(eventService, 'get').and.callFake((eventType: any) => {
        if (eventType === WorldpayBillingAddressCreatedEvent) {
          return billingAddressCreatedEvent$;
        }
        return new Subject();
      });
      spyOn(billingAddressFormService, 'getBillingAddress').and.returnValue(mockBillingAddress);
      spyOn(billingAddressFormService, 'setBillingAddress');

      listener['onWorldpayBillingChangeEvent']();

      billingAddressCreatedEvent$.next({} as any);

      expect(billingAddressFormService.setBillingAddress).toHaveBeenCalledWith(mockBillingAddress);
    });

    it('should set billing address on SetPaymentAddressEvent', () => {
      const setPaymentAddressEvent$ = new Subject<SetPaymentAddressEvent>();
      const mockBillingAddress = { firstName: 'John' };
      // @ts-ignore
      spyOn(eventService, 'get').and.callFake((eventType: any) => {
        if (eventType === SetPaymentAddressEvent) {
          return setPaymentAddressEvent$;
        }
        return new Subject();
      });
      spyOn(billingAddressFormService, 'getBillingAddress').and.returnValue(mockBillingAddress);
      spyOn(billingAddressFormService, 'setBillingAddress');

      listener['onWorldpayBillingChangeEvent']();

      setPaymentAddressEvent$.next({} as any);

      expect(billingAddressFormService.setBillingAddress).toHaveBeenCalledWith(mockBillingAddress);
    });

    it('should set billing address on WorldpayBillingAddressUpdatedEvent', () => {
      const billingAddressUpdatedEvent$ = new Subject<WorldpayBillingAddressUpdatedEvent>();
      const mockBillingAddress = { firstName: 'John' };
      // @ts-ignore
      spyOn(eventService, 'get').and.callFake((eventType: any) => {
        if (eventType === WorldpayBillingAddressUpdatedEvent) {
          return billingAddressUpdatedEvent$;
        }
        return new Subject();
      });
      spyOn(billingAddressFormService, 'getBillingAddress').and.returnValue(mockBillingAddress);
      spyOn(billingAddressFormService, 'setBillingAddress');

      listener['onWorldpayBillingChangeEvent']();

      billingAddressUpdatedEvent$.next({} as any);

      expect(billingAddressFormService.setBillingAddress).toHaveBeenCalledWith(mockBillingAddress);
    });

    it('should handle multiple billing change events', () => {
      const checkoutPaymentEvent$ = new Subject<CheckoutPaymentDetailsCreatedEvent>();
      const billingAddressCreatedEvent$ = new Subject<WorldpayBillingAddressCreatedEvent>();
      const mockBillingAddress = { firstName: 'John' };
      // @ts-ignore
      spyOn(eventService, 'get').and.callFake((eventType: any) => {
        if (eventType === CheckoutPaymentDetailsCreatedEvent) {
          return checkoutPaymentEvent$;
        }
        if (eventType === WorldpayBillingAddressCreatedEvent) {
          return billingAddressCreatedEvent$;
        }
        return new Subject();
      });
      spyOn(billingAddressFormService, 'getBillingAddress').and.returnValue(mockBillingAddress);
      spyOn(billingAddressFormService, 'setBillingAddress');

      listener['onWorldpayBillingChangeEvent']();

      checkoutPaymentEvent$.next({} as any);
      billingAddressCreatedEvent$.next({} as any);

      expect(billingAddressFormService.setBillingAddress).toHaveBeenCalledTimes(2);
    });

    it('should handle case when getBillingAddress returns undefined', () => {
      const checkoutPaymentEvent$ = new Subject<CheckoutPaymentDetailsCreatedEvent>();
      // @ts-ignore
      spyOn(eventService, 'get').and.callFake((eventType: any) => {
        if (eventType === CheckoutPaymentDetailsCreatedEvent) {
          return checkoutPaymentEvent$;
        }
        return new Subject();
      });
      spyOn(billingAddressFormService, 'getBillingAddress').and.returnValue(undefined);
      spyOn(billingAddressFormService, 'setBillingAddress');

      listener['onWorldpayBillingChangeEvent']();

      checkoutPaymentEvent$.next({} as any);

      expect(billingAddressFormService.setBillingAddress).toHaveBeenCalledWith(undefined);
    });
  });
});