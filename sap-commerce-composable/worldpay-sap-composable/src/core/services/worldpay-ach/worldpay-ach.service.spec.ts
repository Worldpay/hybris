import { TestBed } from '@angular/core/testing';
import { ActiveCartFacade } from '@spartacus/cart/base/root';
import { EventService, QueryService, UserIdService } from '@spartacus/core';
import { of, throwError } from 'rxjs';
import { WorldpayACHService } from './worldpay-ach.service';
import { ACHBankAccountType, ACHPaymentFormRaw } from '../../interfaces';
import { WorldpayACHConnector } from '../../connectors/worldpay-ach/worldpay-ach.connector';
import createSpy = jasmine.createSpy;

const cartId = 'cartId';
const userId = 'userId';

class MockUserIdService implements Partial<UserIdService> {
  takeUserId = createSpy().and.returnValue(of(userId));
}

class MockActiveCartFacade {
  takeActiveCartId() {
    return of(cartId);
  }

  isGuestCart() {
    return of(false);
  }
}

class MockQueryService implements Partial<QueryService> {
  create = createSpy().and.callThrough();
}

describe('WorldpayAchService', () => {
  let service: WorldpayACHService;
  let userIdService: UserIdService;
  let activeCartFacade: ActiveCartFacade;
  const worldpayACHConnector = jasmine.createSpyObj('WorldpayACHConnector', ['getACHBankAccountTypes']);

  beforeEach(() => {

    TestBed.configureTestingModule({
      providers: [
        {
          provide: UserIdService,
          useClass: MockUserIdService
        },
        {
          provide: ActiveCartFacade,
          useClass: MockActiveCartFacade,
        },
        QueryService,
        EventService,
        {
          provide: WorldpayACHConnector,
          useValue: worldpayACHConnector
        }
      ]
    });
    service = TestBed.inject(WorldpayACHService);
    userIdService = TestBed.inject(UserIdService);
    activeCartFacade = TestBed.inject(ActiveCartFacade);

  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should throw error when checkout conditions are not met', () => {
    spyOn(activeCartFacade, 'takeActiveCartId').and.returnValue(of(null));
    spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(false));
    service.checkoutPreconditions().subscribe({
      error: (err) => expect(err).toEqual(new Error('Checkout conditions not met'))
    });
  });

  it('should return userId and cartId when checkout conditions are met', (done) => {
    spyOn(activeCartFacade, 'takeActiveCartId').and.returnValue(of('cartId'));
    spyOn(activeCartFacade, 'isGuestCart').and.returnValue(of(false));
    service.checkoutPreconditions().subscribe(([userId, cartId]) => {
      expect(userId).toEqual('userId');
      expect(cartId).toEqual('cartId');
      done();
    });
  });

  it('should set ACH payment form value', () => {
    const formValue: ACHPaymentFormRaw = {
      accountType: { code: 'type1' },
      accountNumber: '123456789',
      routingNumber: '987654321',
      checkNumber: '11111',
      companyName: 'Test Company',
      customIdentifier: '12345'
    };
    service.setACHPaymentFormValue(formValue);
    service.getACHPaymentFormValue().subscribe(value => {
      expect(value).toEqual({
        ...formValue,
        accountType: formValue.accountType.code
      });
    });
  });

  it('should return ACH bank account types state', () => {
    const achBankAccountTypes: ACHBankAccountType[] = [
      {
        code: 'type1',
        name: 'Type 1'
      },
      {
        code: 'type2',
        name: 'Type 2'
      },
    ];
    worldpayACHConnector.getACHBankAccountTypes.and.returnValue(of(achBankAccountTypes));

    service.getACHBankAccountTypesState().subscribe(state => {
      expect(state).toEqual({
        loading: false,
        error: false,
        data: achBankAccountTypes
      });
    });
  });

  it('should handle error when getting ACH bank account types state', () => {
    const error = new Error('Failed to fetch');
    worldpayACHConnector.getACHBankAccountTypes.and.returnValue(throwError(error));
    service.getACHBankAccountTypesState().subscribe({
      next: (err) => expect(err).toEqual({
        loading: false,
        error,
        data: undefined
      }),
      error: (err) => {
        expect(err).toEqual(error);
      }
    });
  });
});
