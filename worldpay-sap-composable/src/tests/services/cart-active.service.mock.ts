import { ActiveCartService } from '@spartacus/cart/base/core';
import { ActiveCartFacade, Cart, OrderEntry } from '@spartacus/cart/base/root';
import { EMPTY, Observable, of } from 'rxjs';

export class MockActiveCartFacade implements Partial<ActiveCartFacade> {
  getActiveCartId(): Observable<string> {
    return of('cartId');
  }
  takeActiveCartId(): Observable<string> {
    return of('cartId');
  }

  isGuestCart(): Observable<boolean> {
    return of(false);
  }

  addEntry(
    _productCode: string,
    _quantity: number,
    _pickupStore?: string
  ): void {
  }

  getEntry(_productCode: string): Observable<OrderEntry> {
    return EMPTY;
  }

  isStable(): Observable<boolean> {
    return EMPTY;
  }

  getActive(): Observable<Cart> {
    return of({
      code: 'cartId',
      guid: 'guid',
      totalItems: 0,
      totalPrice: {
        currencyIso: 'USD',
        value: 0,
      },
    });
  }

  getEntries(): Observable<OrderEntry[]> {
    return of([]);
  }

  updateEntry(_entryNumber: number, _quantity: number): void {
  }

  getLastEntry(_productCode: string): Observable<OrderEntry | undefined> {
    return of({});
  }
}

export class MockActiveCartService extends MockActiveCartFacade implements Partial<ActiveCartService> {
  constructor() {
    super();
  }
}





