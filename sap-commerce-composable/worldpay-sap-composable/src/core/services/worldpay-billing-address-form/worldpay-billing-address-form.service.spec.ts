import { TestBed } from '@angular/core/testing';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { Address, CommandService, EventService, LoggerService, OCC_USER_ID_ANONYMOUS, QueryService, UserIdService } from '@spartacus/core';
import { of } from 'rxjs';
import { WorldpayConnector } from 'worldpay-sap-composable-connectors';
import { WorldpayBillingAddressSameAsDeliveryAddressSetEvent } from 'worldpay-sap-composable-events';
import { MockActiveCartFacade, MockUserIdService, MockWorldpayConnector } from 'worldpay-sap-composable-tests';
import { WorldpayBillingAddressFormService } from './worldpay-billing-address-form.service';

const mockAddress: Address = {
  firstName: 'John',
  lastName: 'Doe',
  line1: '123 Main St',
  line2: '',
  town: 'Anytown',
  region: { isocodeShort: 'CA' },
  country: { isocode: 'US' },
  postalCode: '12345',
};

describe('WorldpayBillingAddressFormService', () => {
  let service: WorldpayBillingAddressFormService;
  let eventService: EventService;
  let activeCartFacade: ActiveCartFacade;
  let userIdService: UserIdService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: WorldpayConnector,
          useClass: MockWorldpayConnector
        },
        {
          provide: UserIdService,
          useClass: MockUserIdService
        },
        {
          provide: ActiveCartFacade,
          useClass: MockActiveCartFacade
        },
        QueryService,
        CommandService,
        WorldpayBillingAddressFormService,
        EventService,
        LoggerService
      ]
    });
    service = TestBed.inject(WorldpayBillingAddressFormService);
    eventService = TestBed.inject(EventService);
    userIdService = TestBed.inject(UserIdService);
    activeCartFacade = TestBed.inject(ActiveCartFacade);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getBillingAddressForm', () => {
    it('should create and return a form group with default values', () => {
      const form = service.getBillingAddressForm();
      expect(form).toBeTruthy();
      expect(form.get('firstName')).toBeTruthy();
      expect(form.get('lastName')).toBeTruthy();
      expect(form.get('line1')).toBeTruthy();
      expect(form.get('line2')).toBeTruthy();
      expect(form.get('town')).toBeTruthy();
      expect(form.get('region.isocodeShort')).toBeTruthy();
      expect(form.get('country.isocode')).toBeTruthy();
      expect(form.get('postalCode')).toBeTruthy();
    });

    it('should return the same form group instance if called multiple times', () => {
      const form1 = service.getBillingAddressForm();
      const form2 = service.getBillingAddressForm();
      expect(form1).toBe(form2);
    });
  });

  describe('isBillingAddressSameAsDeliveryAddress', () => {
    it('returns true when _sameAsDeliveryAddress is true', () => {
      service['_sameAsDeliveryAddress'].next(true);
      expect(service.isBillingAddressSameAsDeliveryAddress()).toBeTrue();
    });

    it('returns false when _sameAsDeliveryAddress is false', () => {
      service['_sameAsDeliveryAddress'].next(false);
      expect(service.isBillingAddressSameAsDeliveryAddress()).toBeFalse();
    });
  });

  describe('isBillingAddressFormValid', () => {
    it('should return false if form is invalid', () => {
      const form = service.getBillingAddressForm();
      form.patchValue({
        firstName: '',
        lastName: ''
      });
      expect(service.isBillingAddressFormValid()).toEqual(false);
    });

    it('should return true if form is valid', () => {
      const form = service.getBillingAddressForm();
      form.patchValue(mockAddress);
      expect(service.isBillingAddressFormValid()).toEqual(true);
    });
  });

  describe('markAllAsTouched', () => {
    it('should mark all form controls as touched', () => {
      const form = service.getBillingAddressForm();
      spyOn(form, 'markAllAsTouched');
      service.markAllAsTouched();
      expect(form.markAllAsTouched).toHaveBeenCalled();
    });
  });

  describe('getBillingAddress', () => {
    it('returns the billing address when it is defined', () => {
      service['billingAddress'] = mockAddress;
      expect(service.getBillingAddress()).toEqual(mockAddress);
    });

    it('returns the value from billingAddress$ when billing address is undefined', () => {
      service['billingAddress'] = undefined;
      service.billingAddress$.next(mockAddress);
      expect(service.getBillingAddress()).toEqual(mockAddress);
    });

    it('returns undefined when both billing address and billingAddress$ are undefined', () => {
      service['billingAddress'] = undefined;
      service.billingAddress$.next(undefined);
      expect(service.getBillingAddress()).toBeUndefined();
    });
  });

  describe('compareAddresses', () => {
    it('returns false when either billingAddress or deliveryAddress is undefined', () => {
      expect(service.compareAddresses(undefined, mockAddress)).toBeFalse();
      expect(service.compareAddresses(mockAddress, undefined)).toBeFalse();
      expect(service.compareAddresses(undefined, undefined)).toBeFalse();
    });

    it('returns true when billingAddress and deliveryAddress have the same id', () => {
      const address1 = {
        ...mockAddress,
        id: '123'
      };
      const address2 = {
        ...mockAddress,
        id: '123'
      };
      expect(service.compareAddresses(address1, address2)).toBeTrue();
    });

    it('returns true when all properties of billingAddress match deliveryAddress', () => {
      const address1 = { ...mockAddress };
      const address2 = { ...mockAddress };
      expect(service.compareAddresses(address1, address2)).toBeTrue();
    });

    it('returns false when a property of billingAddress does not match deliveryAddress', () => {
      const address1 = {
        ...mockAddress,
        id: '100',
        line1: '456 Another St'
      };
      const address2 = { ...mockAddress };
      expect(service.compareAddresses(address1, address2)).toBeFalse();
    });

    it('returns true when region properties match by isocode or isocodeShort', () => {
      const address1 = {
        ...mockAddress,
        id: '100',
        region: {
          isocode: 'CA',
          isocodeShort: 'CA'
        }
      };
      const address2 = {
        ...mockAddress,
        id: '100',
        region: {
          isocode: 'CA',
          isocodeShort: 'CA'
        }
      };
      expect(service.compareAddresses(address1, address2)).toBeTrue();
    });

    it('returns false when region properties do not match by isocode or isocodeShort', () => {
      const address1 = {
        ...mockAddress,
        id: '100',
        region: {
          isocode: 'CA',
          isocodeShort: 'CA'
        }
      };
      const address2 = {
        ...mockAddress,
        id: '200',
        region: {
          isocode: 'NY',
          isocodeShort: 'NY'
        }
      };
      expect(service.compareAddresses(address1, address2)).toBeFalse();
    });

    it('returns false when region is missing in either billingAddress or deliveryAddress', () => {
      const address1 = {
        ...mockAddress,
        id: '100',
        region: undefined
      };
      const address2 = {
        ...mockAddress,
        id: '200',
        region: {
          isocode: 'CA',
          isocodeShort: 'CA'
        }
      };
      expect(service.compareAddresses(address1, address2)).toBeFalse();
    });
  });

  describe('getSameAsDeliveryAddress()', () => {
    it('should return true when billing address is the same as delivery address', (done) => {
      service['_sameAsDeliveryAddress'].next(true);
      let sameAsDeliveryAddress = false;
      service.getSameAsDeliveryAddress().subscribe({
        next: (res) => {
          sameAsDeliveryAddress = res;
          done();
        }
      });
      expect(sameAsDeliveryAddress).toBeTrue();
    });

    it('should return false when billing address is not the same as delivery address', (done) => {
      service['_sameAsDeliveryAddress'].next(false);
      let sameAsDeliveryAddress = true;
      service.getSameAsDeliveryAddress().subscribe((res) => {
        sameAsDeliveryAddress = res;
        done();
      });
      expect(sameAsDeliveryAddress).toBeFalse();
    });
  });

  describe('setSameAsDeliveryAddress', () => {
    it('updates the observable with the provided value', () => {
      spyOn(service['_sameAsDeliveryAddress'], 'next');
      service.setSameAsDeliveryAddress(true);
      expect(service['_sameAsDeliveryAddress'].next).toHaveBeenCalledWith(true);
    });

    it('dispatches an event when value is true and deliveryAddress is provided', () => {
      spyOn(eventService, 'dispatch');
      service.setSameAsDeliveryAddress(true, mockAddress);
      expect(eventService.dispatch).toHaveBeenCalledWith(
        {
          billingAddress: mockAddress,
          deliveryAddress: undefined
        },
        WorldpayBillingAddressSameAsDeliveryAddressSetEvent
      );
    });

    it('does not dispatch an event when value is false', () => {
      spyOn(eventService, 'dispatch');
      service.setSameAsDeliveryAddress(false, mockAddress);
      expect(eventService.dispatch).not.toHaveBeenCalled();
    });

    it('does not dispatch an event when value is true but deliveryAddress is undefined', () => {
      spyOn(eventService, 'dispatch');
      service.setSameAsDeliveryAddress(true, undefined);
      expect(eventService.dispatch).toHaveBeenCalledWith(
        {
          billingAddress: undefined,
          deliveryAddress: undefined
        },
        WorldpayBillingAddressSameAsDeliveryAddressSetEvent
      );
    });
  });

  describe('isEditModeEnabled', () => {
    it('returns true when edit mode toggle is enabled', (done) => {
      service.editModeStatus$.next(true);
      service.isEditModeEnabled().subscribe((isEnabled) => {
        expect(isEnabled).toBeTrue();
        done();
      });
    });

    it('returns false when edit mode is disabled', (done) => {
      service.editModeStatus$.next(false);
      service.isEditModeEnabled().subscribe((isEnabled) => {
        expect(isEnabled).toBeFalse();
        done();
      });
    });
  });

  describe('isEditModeEnabledValue', () => {
    it('returns true when edit mode value is enabled', () => {
      service.editModeStatus$.next(true);
      expect(service.isEditModeEnabledValue()).toBeTrue();
    });

    it('returns false when edit mode is false and disabled', () => {
      service.editModeStatus$.next(false);
      expect(service.isEditModeEnabledValue()).toBeFalse();
    });
  });

  describe('toggleEditMode', () => {
    it('switches edit mode from false to true', () => {
      service.editModeStatus$.next(false);
      service.toggleEditMode();
      expect(service.editModeStatus$.value).toBeTrue();
    });

    it('switches edit mode from true to false', () => {
      service.editModeStatus$.next(true);
      service.toggleEditMode();
      expect(service.editModeStatus$.value).toBeFalse();
    });
  });

  describe('setEditToggleState', () => {
    it('updates the edit mode status to true', () => {
      service.setEditToggleState(true);
      expect(service.editModeStatus$.value).toBeTrue();
    });

    it('updates the edit mode status to false', () => {
      service.setEditToggleState(false);
      expect(service.editModeStatus$.value).toBeFalse();
    });
  });

  describe('setBillingAddress', () => {
    it('updates the form with the provided billing address', () => {
      const mockForm = service.getBillingAddressForm();
      spyOn(mockForm, 'patchValue');
      service.setSameAsDeliveryAddress(false);
      service.setBillingAddress(mockAddress);
      expect(mockForm.patchValue).toHaveBeenCalledWith(mockAddress);
      expect(service['billingAddress']).toEqual(mockAddress);
    });

    it('resets the form when billing address is undefined', () => {
      const mockForm = service.getBillingAddressForm();
      spyOn(mockForm, 'reset');
      service.setBillingAddress(undefined);
      expect(mockForm.reset).toHaveBeenCalled();
      expect(service['billingAddress']).toBeUndefined();
    });

    it('sets the delivery address as billing address when delivery address is provided', (done) => {
      const billingAddress = {
        firstName: null,
        lastName: null,
        line1: null,
        line2: null,
        town: null,
        region: { isocodeShort: null },
        country: { isocode: null },
        postalCode: null
      };

      spyOn(service, 'setDeliveryAddressAsBillingAddress').and.callThrough();
      service.setBillingAddress(mockAddress, mockAddress);
      expect(service.setDeliveryAddressAsBillingAddress).toHaveBeenCalledWith(mockAddress);
      expect(service.getBillingAddressForm().value).toEqual(billingAddress);
      expect(service['billingAddress']).toEqual(undefined);
      service.getSameAsDeliveryAddress().subscribe(res => {
        expect(res).toEqual(true);
        done();
      });
    });

    it('sets the same as delivery address flag based on comparison result and clear billing address form', () => {
      spyOn(service, 'setSameAsDeliveryAddress');
      spyOn(service, 'isBillingAddressSameAsDeliveryAddress').and.returnValue(true);
      service.setBillingAddress(mockAddress, mockAddress);
      expect(service.setSameAsDeliveryAddress).toHaveBeenCalledWith(true, mockAddress);
      expect(service['billingAddress']).toEqual(undefined);
    });
  });

  describe('setDeliveryAddressAsBillingAddress', () => {
    it('updates the billingAddress$ observable with the provided address', () => {
      service.setSameAsDeliveryAddress(false);
      const address = {
        ...mockAddress,
        id: '123'
      };
      service.setDeliveryAddressAsBillingAddress(address);
      expect(service.billingAddress$.value).toEqual(address);
    });

    it('patches the form with the provided address when it is defined', () => {
      service.setSameAsDeliveryAddress(false);
      const address = {
        ...mockAddress,
        id: '123'
      };
      const mockForm = service.getBillingAddressForm();
      spyOn(mockForm, 'patchValue');
      service.setDeliveryAddressAsBillingAddress(address);
      expect(mockForm.patchValue).toHaveBeenCalledWith(address);
    });

    it('resets the form when the provided address is undefined', () => {
      const mockForm = service.getBillingAddressForm();
      spyOn(mockForm, 'reset');
      service.setDeliveryAddressAsBillingAddress(undefined);
      expect(mockForm.reset).toHaveBeenCalled();
      expect(service.billingAddress$.value).toBeUndefined();
    });
  });

  describe('checkoutPreconditions', () => {
    it('emits userId and cartId when conditions are met', (done) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of('user123'));
      spyOn(activeCartFacade, 'takeActiveCartId').and.returnValue(of('cart123'));
      spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(false));

      service.checkoutPreconditions().subscribe(([userId, cartId]) => {
        expect(userId).toBe('user123');
        expect(cartId).toBe('cart123');
        done();
      });
    });

    it('throws an error when userId is undefined', (done) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of(undefined));
      spyOn(activeCartFacade, 'takeActiveCartId').and.returnValue(of('cart123'));
      spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(false));

      service.checkoutPreconditions().subscribe({
        error: (err) => {
          expect(err.message).toBe('Checkout conditions not met');
          done();
        },
      });
    });

    it('throws an error when cartId is undefined', (done) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of('user123'));
      spyOn(activeCartFacade, 'takeActiveCartId').and.returnValue(of(undefined));
      spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(false));

      service.checkoutPreconditions().subscribe({
        error: (err) => {
          expect(err.message).toBe('Checkout conditions not met');
          done();
        },
      });
    });

    it('throws an error when userId is anonymous and isGuestCart is false', (done) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of(OCC_USER_ID_ANONYMOUS));
      spyOn(activeCartFacade, 'takeActiveCartId').and.returnValue(of('cart123'));
      spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(false));

      service.checkoutPreconditions().subscribe({
        error: (err) => {
          expect(err.message).toBe('Checkout conditions not met');
          done();
        },
      });
    });

    it('emits userId and cartId when userId is anonymous and isGuestCart is true', (done) => {
      spyOn(userIdService, 'takeUserId').and.returnValue(of(OCC_USER_ID_ANONYMOUS));
      spyOn(activeCartFacade, 'takeActiveCartId').and.returnValue(of('cart123'));
      spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(true));

      service.checkoutPreconditions().subscribe(([userId, cartId]) => {
        expect(userId).toBe(OCC_USER_ID_ANONYMOUS);
        expect(cartId).toBe('cart123');
        done();
      });
    });
  });

  describe('resetBillingAddressForm', () => {
    it('resets the billing address form when it exists', () => {
      const mockForm = service.getBillingAddressForm();
      spyOn(mockForm, 'reset');
      service.resetBillingAddressForm();
      expect(mockForm.reset).toHaveBeenCalled();
    });

    it('sets the billing address to undefined', () => {
      service.billingAddress$.next(mockAddress);
      service.resetBillingAddressForm();
      expect(service.billingAddress$.value).toBeUndefined();
    });

    it('does not throw an error when the form is undefined', () => {
      spyOn(service, 'getBillingAddressForm').and.returnValue(undefined);
      expect(() => service.resetBillingAddressForm()).not.toThrow();
    });
  });
})
;
