import { TestBed } from '@angular/core/testing';
import { WorldpayACHAdapter } from './worldpay-ach.adapter';
import { WorldpayACHConnector } from './worldpay-ach.connector';
import { ACHPaymentForm } from '../../interfaces';
import { of, throwError } from 'rxjs';
import createSpy = jasmine.createSpy;

class MockWorldpayACHAdapter implements WorldpayACHAdapter {
  getACHBankAccountTypes = createSpy('WorldpayAdapter.getACHBankAccountTypes').and.callFake(() => of(null));

  placeACHOrder = createSpy('WorldpayAdapter.placeACHOrder').and.callFake((userId: string, cartId: string) => of(null));
}

describe('WorldpayApmConnector', () => {
  let service: WorldpayACHConnector;
  let adapter: WorldpayACHAdapter;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: WorldpayACHAdapter,
          useClass: MockWorldpayACHAdapter
        }
      ]
    });
    service = TestBed.inject(WorldpayACHConnector);
    adapter = TestBed.inject(WorldpayACHAdapter);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getACHBankAccountTypes', () => {
    it('should call placeACHOrder with correct parameters', () => {
      const achPaymentForm: ACHPaymentForm = {
        accountType: 'Checking',
        accountNumber: '1234567890',
        routingNumber: '987654321',
        companyName: 'John Doe',
        customIdentifier: 'Individual',
      };

      service.placeACHOrder('userId', 'cartId', achPaymentForm).subscribe();

      expect(adapter.placeACHOrder).toHaveBeenCalledWith('userId', 'cartId', achPaymentForm);
    });

    it('should handle error when getACHBankAccountTypes fails', () => {
      const errorResponse = new Error('Failed to fetch ACH Bank Account Types');
      adapter.getACHBankAccountTypes = createSpy('WorldpayAdapter.getACHBankAccountTypes').and.returnValue(throwError(errorResponse));

      service.getACHBankAccountTypes('userId', 'cartId').subscribe(
        () => {
        },
        (error) => {
          expect(error).toEqual(errorResponse);
        }
      );
    });
  });

  describe('placeACHOrder', () => {
    it('should call placeACHOrder with correct parameters', () => {
      const achPaymentForm: ACHPaymentForm = {
        accountType: 'Checking',
        accountNumber: '1234567890',
        routingNumber: '987654321',
        companyName: 'John Doe',
        customIdentifier: 'Individual',
      };

      service.placeACHOrder('userId', 'cartId', achPaymentForm).subscribe();

      expect(adapter.placeACHOrder).toHaveBeenCalledWith('userId', 'cartId', achPaymentForm);
    });

    it('should handle error when placeACHOrder fails', () => {
      const errorResponse = new Error('Failed to place ACH Order');
      const achPaymentForm: ACHPaymentForm = {
        accountType: 'Checking',
        accountNumber: '1234567890',
        routingNumber: '987654321',
        companyName: 'John Doe',
        customIdentifier: 'Individual',
      };
      adapter.placeACHOrder = createSpy('WorldpayAdapter.placeACHOrder').and.returnValue(throwError(errorResponse));

      service.placeACHOrder('userId', 'cartId', achPaymentForm).subscribe(
        () => {
        },
        (error) => {
          expect(error).toEqual(errorResponse);
        }
      );
    });
  });
});
