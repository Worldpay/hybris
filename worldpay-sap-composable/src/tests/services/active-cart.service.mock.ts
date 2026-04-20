import { ActiveCartService } from '@spartacus/cart/base/core';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { of } from 'rxjs';
import createSpy = jasmine.createSpy;
import { generateOneCart } from '../fake-data/cart.mock';

const cartId = 'cartId';

export class MockActiveCartFacade implements Partial<ActiveCartFacade> {
  takeActiveCartId = createSpy('takeActiveCartId').and.returnValue(of(cartId));
  isGuestCart = createSpy('isGuestCart').and.returnValue(of(false));
  getActiveCartId = createSpy('getActiveCartId').and.returnValue(of(cartId));
  getActive = createSpy('getActive').and.returnValue(of(generateOneCart()));
}

export class MockActiveCartService implements Partial<ActiveCartService> {
  takeActiveCartId = createSpy('takeActiveCartId').and.returnValue(of(cartId));
  isGuestCart = createSpy('isGuestCart').and.returnValue(of(false));
  getActiveCartId = createSpy('getActiveCartId').and.returnValue(of(cartId));
  getActive = createSpy('getActive').and.returnValue(of(generateOneCart()));
}