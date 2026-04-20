import { CheckoutDeliveryAddressFacade } from '@spartacus/checkout/base/root';
import { Address, QueryState } from '@spartacus/core';
import { EMPTY, Observable, of } from 'rxjs';
import { generateOneAddress } from '../fake-data/address.mock';
import createSpy = jasmine.createSpy;

const mockDeliveryAddress: Address = generateOneAddress();

export class MockCheckoutDeliveryAddressFacade implements Partial<CheckoutDeliveryAddressFacade> {
  getAddressVerificationResults = createSpy().and.returnValue(EMPTY);
  verifyAddress = createSpy();
  clearAddressVerificationResults = createSpy();

  getDeliveryAddressState(): Observable<QueryState<Address | undefined>> {
    return of({
      loading: false,
      data: mockDeliveryAddress,
      error: false
    });
  }
}

export class MockCheckoutDeliveryAddressService extends MockCheckoutDeliveryAddressFacade {
  constructor() {
    super();
  }
}