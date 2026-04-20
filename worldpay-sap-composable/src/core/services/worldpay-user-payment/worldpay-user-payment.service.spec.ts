import { inject, TestBed } from '@angular/core/testing';
import { Store, StoreModule } from '@ngrx/store';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import {
  CheckoutDeliveryAddressCreatedEvent,
  CheckoutDeliveryAddressSetEvent,
  CheckoutPaymentDetailsCreatedEvent,
  CheckoutPaymentDetailsSetEvent
} from '@spartacus/checkout/base/root';
import { CurrencySetEvent, LoggerService, OCC_USER_ID_CURRENT, QueryState, StateWithUser, tryNormalizeHttpError, UserActions, UserIdService } from '@spartacus/core';
import { of, throwError } from 'rxjs';
import { MockActiveCartFacade } from 'worldpay-sap-composable-tests';
import { WorldpayUserPaymentConnector } from '../../connectors';
import {
  CreateWorldpayPaymentDetailsEvent,
  WorldpayBillingAddressCreatedEvent,
  WorldpayBillingAddressSameAsDeliveryAddressSetEvent,
  WorldpayBillingAddressUpdatedEvent
} from '../../events';
import { ApmPaymentDetails, PaymentMethod } from '../../interfaces';
import { WorldpayUserPaymentService } from './worldpay-user-payment.service';

const apmPaymentDetail: ApmPaymentDetails = { code: PaymentMethod.GooglePay };

class MockUserIdService implements Partial<UserIdService> {
  takeUserId() {
    return of(OCC_USER_ID_CURRENT);
  }
}

class MockWorldpayUserPaymentConnector implements Partial<WorldpayUserPaymentConnector> {
  loadAllForCart() {
    return of([apmPaymentDetail]);
  }
}

describe('WorldpayUserPaymentService', () => {
  let service: WorldpayUserPaymentService;
  let store: Store<StateWithUser>;
  let userPaymentMethodConnector: WorldpayUserPaymentConnector;
  let logger: LoggerService;
  const error = new Error('Test error');

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
      ],
      providers: [
        WorldpayUserPaymentService,
        {
          provide: UserIdService,
          useClass: MockUserIdService
        },
        {
          provide: ActiveCartFacade,
          useClass: MockActiveCartFacade
        },
        {
          provide: WorldpayUserPaymentConnector,
          useClass: MockWorldpayUserPaymentConnector
        },
        LoggerService,
      ],
    });

    store = TestBed.inject(Store);
    spyOn(store, 'dispatch').and.callThrough();
    service = TestBed.inject(WorldpayUserPaymentService);
    userPaymentMethodConnector = TestBed.inject(WorldpayUserPaymentConnector);
    logger = TestBed.inject(LoggerService);
    spyOn(console, 'error').and.callFake(() => {
    });
    spyOn(console, 'log').and.callFake(() => {
    });
    spyOn(console, 'warn').and.callFake(() => {
    });
  });

  it('should UserPaymentService is injected', inject(
    [WorldpayUserPaymentService],
    (userPaymentService: WorldpayUserPaymentService) => {
      expect(userPaymentService).toBeTruthy();
    }
  ));

  it('should be able to load user payment methods', () => {
    service.loadPaymentMethods();
    expect(store.dispatch).toHaveBeenCalledWith(
      new UserActions.LoadUserPaymentMethodsSuccess([apmPaymentDetail])
    );
  });

  it('should be able to show error message when load user payment methods fails', () => {
    const error = new Error('Test error');
    spyOn(logger, 'error');
    spyOn(userPaymentMethodConnector, 'loadAllForCart').and.returnValue(throwError(() => error));

    service.loadPaymentMethods();

    expect(store.dispatch).toHaveBeenCalledWith(new UserActions.LoadUserPaymentMethodsFail(tryNormalizeHttpError(error, service['logger'])));
  });

  it('should be able to get user payment methods loading flag', () => {
    spyOn(service, 'triggerLoadAllPaymentMethods').and.returnValue(of({
      loading: true,
      error: false,
      data: undefined,
    }));

    let flag: boolean;
    service.getPaymentMethodsLoading().subscribe((data) => {
      flag = data;
    }).unsubscribe();
    expect(flag).toEqual(true);
  });

  describe('reloadSavedPaymentMethodsEvents', () => {
    it('should return the correct list of reload events', () => {
      const events = service['reloadSavedPaymentMethodsEvents']();
      expect(events).toEqual([
        CurrencySetEvent,
        CheckoutDeliveryAddressSetEvent,
        CheckoutPaymentDetailsSetEvent,
        CheckoutDeliveryAddressCreatedEvent,
        CheckoutPaymentDetailsCreatedEvent,
        CreateWorldpayPaymentDetailsEvent,
        WorldpayBillingAddressCreatedEvent,
        WorldpayBillingAddressUpdatedEvent,
        WorldpayBillingAddressSameAsDeliveryAddressSetEvent
      ]);
    });

    it('should return an empty array if no events are defined', () => {
      spyOn<any>(service, 'reloadSavedPaymentMethodsEvents').and.returnValue([]);
      const events = service['reloadSavedPaymentMethodsEvents']();
      expect(events).toEqual([]);
    });
  });

  describe('triggerLoadAllPaymentMethods', () => {
    it('should return the current state of loadAllPaymentMethodsQuery$', () => {
      const mockState = {
        loading: false,
        error: null,
        data: [apmPaymentDetail]
      };
      spyOn(service['loadAllPaymentMethodsQuery$'], 'getState').and.returnValue(of(mockState));

      service.triggerLoadAllPaymentMethods().subscribe((state) => {
        expect(state).toEqual(mockState);
      });
    });

    it('should handle an error state from loadAllPaymentMethodsQuery$', () => {
      const mockErrorState = {
        loading: false,
        error,
        data: null
      };
      spyOn(service['loadAllPaymentMethodsQuery$'], 'getState').and.returnValue(of(mockErrorState));

      service.triggerLoadAllPaymentMethods().subscribe((state) => {
        expect(state).toEqual(mockErrorState);
      });
    });
  });

  describe('loadPaymentMethods', () => {
    it('should dispatch success action when payment methods are loaded successfully', () => {
      const mockQueryState = {
        loading: false,
        error: false,
        data: [apmPaymentDetail]
      } as QueryState<ApmPaymentDetails[]>;
      spyOn(service, 'triggerLoadAllPaymentMethods').and.returnValue(of(mockQueryState));
      spyOn(service, 'dispatchError');

      service.loadPaymentMethods();

      expect(store.dispatch).toHaveBeenCalledWith(
        new UserActions.LoadUserPaymentMethodsSuccess(mockQueryState.data)
      );
      expect(service.dispatchError).not.toHaveBeenCalled();
    });

    it('should dispatch error action when loading payment methods fails', () => {
      const mockError = new Error('Test error');
      spyOn(service, 'triggerLoadAllPaymentMethods').and.returnValue(throwError(() => mockError));
      spyOn(service, 'dispatchError');

      service.loadPaymentMethods();

      expect(service.dispatchError).toHaveBeenCalledWith(mockError);
    });

    it('should dispatch error action when query state contains an error', () => {
      const mockQueryState = {
        loading: false,
        error,
        data: null
      };
      spyOn(service, 'triggerLoadAllPaymentMethods').and.returnValue(of(mockQueryState));
      spyOn(service, 'dispatchError');

      service.loadPaymentMethods();

      expect(service.dispatchError).toHaveBeenCalledWith(mockQueryState.error);
    });

    describe('getPaymentMethodsLoading', () => {
      it('returns true when payment methods are loading', () => {
        spyOn(service, 'triggerLoadAllPaymentMethods').and.returnValue(of({
          loading: true,
          error: null,
          data: null
        }));

        let isLoading: boolean;
        service.getPaymentMethodsLoading().subscribe((loading) => {
          isLoading = loading;
        }).unsubscribe();

        expect(isLoading).toBeTrue();
      });

      it('returns false when payment methods are not loading', () => {
        spyOn(service, 'triggerLoadAllPaymentMethods').and.returnValue(of({
          loading: false,
          error: null,
          data: null
        }));

        let isLoading: boolean;
        service.getPaymentMethodsLoading().subscribe((loading) => {
          isLoading = loading;
        }).unsubscribe();

        expect(isLoading).toBeFalse();
      });

      it('returns false when there is an error in the query state', () => {
        spyOn(service, 'triggerLoadAllPaymentMethods').and.returnValue(of({
          loading: false,
          error,
          data: null
        }));

        let isLoading: boolean;
        service.getPaymentMethodsLoading().subscribe((loading) => {
          isLoading = loading;
        }).unsubscribe();

        expect(isLoading).toBeFalse();
      });
    });
  });

  describe('checkoutPreconditions', () => {
    it('throws an error when userId is missing', () => {
      spyOn(service['userIdService'], 'takeUserId').and.returnValue(of(null));
      spyOn(service['activeCartFacade'], 'takeActiveCartId').and.returnValue(of('cartId'));

      service.checkoutPreconditions().subscribe({
        error: (error) => {
          expect(error.message).toBe('Checkout conditions not met');
        }
      });
    });

    it('throws an error when cartId is missing', () => {
      spyOn(service['userIdService'], 'takeUserId').and.returnValue(of('userId'));
      spyOn(service['activeCartFacade'], 'takeActiveCartId').and.returnValue(of(null));

      service.checkoutPreconditions().subscribe({
        error: (error) => {
          expect(error.message).toBe('Checkout conditions not met');
        }
      });
    });

    it('emits userId and cartId when both are available', () => {
      spyOn(service['userIdService'], 'takeUserId').and.returnValue(of('userId'));
      spyOn(service['activeCartFacade'], 'takeActiveCartId').and.returnValue(of('cartId'));

      let result: [string, string];
      service.checkoutPreconditions().subscribe((data) => {
        result = data;
      }).unsubscribe();

      expect(result).toEqual(['userId', 'cartId']);
    });
  });

  describe('dispatchError', () => {
    it('dispatches LoadUserPaymentMethodsFail action with normalized error', () => {
      service.dispatchError(error);

      expect(store.dispatch).toHaveBeenCalledWith(
        new UserActions.LoadUserPaymentMethodsFail(tryNormalizeHttpError(error, logger))
      );
    });

    it('dispatches LoadUserPaymentMethodsFail action with null error', () => {
      service.dispatchError(null);

      expect(store.dispatch).toHaveBeenCalledWith(
        new UserActions.LoadUserPaymentMethodsFail(tryNormalizeHttpError(null, logger))
      );
    });
  });
});
