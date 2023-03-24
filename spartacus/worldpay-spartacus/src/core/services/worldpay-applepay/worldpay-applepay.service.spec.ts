import { TestBed } from '@angular/core/testing';

import { WorldpayApplepayService } from './worldpay-applepay.service';
import { Store, StoreModule } from '@ngrx/store';
import { ActiveCartService, UserIdService } from '@spartacus/core';
import { of } from 'rxjs';
import { StateWithWorldpay } from '../../store/worldpay.state';
import { RequestApplePayPaymentRequest } from '../../store/worldpay.action';
import createSpy = jasmine.createSpy;

describe('WorldpayApplepayService', () => {
  let service: WorldpayApplepayService;
  let activeCartService: ActiveCartService;
  let userIdService: UserIdService;
  let worldpayStore: Store<StateWithWorldpay>;

  const userId = 'testUserId';
  const cartId = 'testCartId';

  class ActiveCartServiceStub {
    cartId = cartId;

    public getActiveCartId() {
      return of(this.cartId);
    }
  }

  class UserIdServiceStub implements Partial<UserIdService> {
    getUserId = createSpy('getUserId').and.returnValue(of(userId));
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({})],
      providers: [
        WorldpayApplepayService,
        {
          provide: ActiveCartService,
          useClass: ActiveCartServiceStub
        },
        {
          provide: UserIdService,
          useClass: UserIdServiceStub
        }
      ]
    });
    service = TestBed.inject(WorldpayApplepayService);
    activeCartService = TestBed.inject(ActiveCartService);
    worldpayStore = TestBed.inject(Store);
    userIdService = TestBed.inject(UserIdService);

    spyOn(worldpayStore, 'dispatch').and.callThrough();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should dispatch events for requestApplePayPaymentRequest', () => {
    service.requestApplePayPaymentRequest();

    expect(worldpayStore.dispatch).toHaveBeenCalledWith(
      new RequestApplePayPaymentRequest({
        userId,
        cartId
      })
    );
  });
});
