import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { WorldpayUserPaymentAdapter } from './worldpay-user-payment.adapter';
import { WorldpayUserPaymentConnector } from './worldpay-user-payment.connector';
import createSpy = jasmine.createSpy;

class MockUserPaymentAdapter implements WorldpayUserPaymentAdapter {
  delete = createSpy('load').and.returnValue(of({}));
  loadAll = createSpy('loadAll').and.callFake((userId) =>
    of(`loadList-${userId}`)
  );
  setDefault = createSpy('setDefault').and.returnValue(of({}));
  loadAllForCart = createSpy('loadAllForCart').and.callFake((userId, cartId) =>
    of(`loadList-${userId}-${cartId}`)
  );
}

describe('UserPaymentConnector', () => {
  let service: WorldpayUserPaymentConnector;
  let adapter: WorldpayUserPaymentAdapter;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: WorldpayUserPaymentAdapter, useClass: MockUserPaymentAdapter },
      ],
    });

    service = TestBed.inject(WorldpayUserPaymentConnector);
    adapter = TestBed.inject(WorldpayUserPaymentAdapter);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('delete should call adapter', () => {
    let result;
    service.delete('user-id', 'payment-id').subscribe((res) => (result = res));
    expect(result).toEqual({});
    expect(adapter.delete).toHaveBeenCalledWith('user-id', 'payment-id');
  });

  it('getAll should call adapter', () => {
    let result;
    service.getAll('user-id').subscribe((res) => (result = res));
    expect(result).toEqual('loadList-user-id');
    expect(adapter.loadAll).toHaveBeenCalledWith('user-id');
  });

  it('setDefault should call adapter', () => {
    let result;
    service
      .setDefault('user-id', 'payment-id')
      .subscribe((res) => (result = res));
    expect(result).toEqual({});
    expect(adapter.setDefault).toHaveBeenCalledWith('user-id', 'payment-id');
  });

  it('loadAllForCart should call adapter', () => {
    let result;
    service.loadAllForCart('user-id', 'cart-id').subscribe((res) => (result = res));
    expect(result).toEqual('loadList-user-id-cart-id');
    expect(adapter.loadAllForCart).toHaveBeenCalledWith('user-id', 'cart-id');
  });
});
