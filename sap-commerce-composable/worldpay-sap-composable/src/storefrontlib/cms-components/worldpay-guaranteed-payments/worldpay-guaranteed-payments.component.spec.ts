import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldpayGuaranteedPaymentsComponent } from './worldpay-guaranteed-payments.component';
import { WorldpayGuaranteedPaymentsService } from '../../../core/services/worldpay-guaranteed-payments/worldpay-guaranteed-payments.service';
import { GlobalMessageService, GlobalMessageType, QueryState, UserIdService } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { Store, StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { UserAccountService } from '@spartacus/user/account/core';
import { User } from '@spartacus/user/account/root';
import { provideMockStore } from '@ngrx/store/testing';
import { ActiveCartService } from '@spartacus/cart/base/core';
import { Cart } from '@spartacus/cart/base/root';

const registeredUser: User = {
  customerId: 'user-id'
};

const cart: Cart = {
  guid: 'cart-100'
};

class MockActiveCartService {
  getActive() {
    return of(cart);
  }
}

class MockUserIdService {
  getUserId() {
    return of(null);
  };
}

class MockUserAccountService {
  get(): Observable<User> {
    return of(null);
  }
}

describe('WorldpayGuaranteedPaymentsComponent', () => {
  let component: WorldpayGuaranteedPaymentsComponent;
  let fixture: ComponentFixture<WorldpayGuaranteedPaymentsComponent>;
  let service: WorldpayGuaranteedPaymentsService;
  let activeCartService: ActiveCartService;
  let userIdService: UserIdService;
  let accountService: UserAccountService;
  let globalMessageService: GlobalMessageService;
  let spyUserId;
  let spyAccount;

  class MockWorldpayGuaranteedPaymentsService implements Partial<WorldpayGuaranteedPaymentsService> {
    getSessionId(): Observable<string> {
      return of('user-id_cart-100');
    }

    isGuaranteedPaymentsEnabled(): Observable<boolean> {
      return of(true);
    }

    isGuaranteedPaymentsEnabledState(): Observable<QueryState<boolean>> {
      return of({
        data: false,
        error: false,
        loading: false,
      });
    }

    generateScript(sessionId) {
      return sessionId;
    }

    setSessionId(sessionId) {
      component.sessionId = sessionId;
    }

    setGuaranteedPaymentsEnabledEvent(enabled: boolean): void {
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
        declarations: [
          WorldpayGuaranteedPaymentsComponent,
        ],
        imports: [
          StoreModule.forRoot({}),
          EffectsModule.forRoot([])
        ],
        providers: [
          Store,
          GlobalMessageService,
          {
            provide: WorldpayGuaranteedPaymentsService,
            useClass: MockWorldpayGuaranteedPaymentsService
          },
          {
            provide: UserAccountService,
            useClass: MockUserAccountService
          },
          {
            provide: ActiveCartService,
            useClass: MockActiveCartService,
          },
          {
            provide: UserIdService,
            useClass: MockUserIdService,
          },
          provideMockStore({
            initialState: {
              cart: {
                carts: {
                  entities: {
                    current: {
                      test: {
                        value: {
                          cart
                        }
                      }
                    }
                  }
                }
              }
            }
          })
        ]
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayGuaranteedPaymentsComponent);
    component = fixture.componentInstance;
    service = TestBed.inject(WorldpayGuaranteedPaymentsService);
    activeCartService = TestBed.inject(ActiveCartService);
    accountService = TestBed.inject(UserAccountService);
    userIdService = TestBed.inject(UserIdService);
    globalMessageService = TestBed.inject(GlobalMessageService);

    spyUserId = spyOn(userIdService, 'getUserId');
    spyAccount = spyOn(accountService, 'get');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be disabled', () => {
    spyOn(service, 'isGuaranteedPaymentsEnabled').and.returnValue(of(false));
    let enabled;
    service.isGuaranteedPaymentsEnabled()
      .subscribe(isEnabled => {
        enabled = isEnabled;
      });

    expect(enabled).toBeFalse();
    expect(component.sessionId).toBe('');
  });

  describe('should be enabled', () => {
    beforeEach(() => {
      spyOn(service, 'isGuaranteedPaymentsEnabled').and.returnValue(of(true));
      spyAccount.and.returnValue(of(''));
      spyUserId.and.returnValue(of('anonymous'));
      spyOn(activeCartService, 'getActive').and.returnValue(of(cart));
    });

    it('should get Active Cart', () => {
      fixture.detectChanges();
      let activeCart;
      activeCartService.getActive().subscribe(active => activeCart = active.guid);

      expect(activeCart).toEqual('cart-100');
    });

    it('should be anonymous User', () => {
      fixture.detectChanges();
      let userUid;
      accountService.get().subscribe(user => {
        userUid = user;
      });
      expect(userUid).toEqual('');

      let userId;
      userIdService.getUserId().subscribe(active => {
        userId = active;
      });
      expect(userId).toEqual('anonymous');

      expect(component.sessionId).toEqual('anonymous_cart-100');
    });

    describe('should be registered User', () => {
      beforeEach(() => {
        component.sessionId = '';
        service.setSessionId('user-id');
        spyAccount.and.returnValue(of(registeredUser));
        spyUserId.and.returnValue(of('current'));
        component.firstLoad = true;
        fixture.detectChanges();
        component.ngOnInit();
      });

      it('should get Registered User', () => {
        let userUid;
        accountService.get().subscribe(user => {
          userUid = user.customerId;
        });
        expect(userUid).toEqual('user-id');
      });

      it('should get Current User', () => {
        let userId;
        userIdService.getUserId().subscribe(active => {
          userId = active;
        });

        expect(userId).toEqual('current');
        expect(component.sessionId).toEqual('user-id_cart-100');
      });

      it('should get session id with getSessionId method', () => {
        let id = '';
        service.getSessionId().subscribe(sessionId => id = sessionId).unsubscribe();
        expect(id).toBe('user-id_cart-100');
      });

      it('should show error message  with showErrorMessage method', () => {
        spyOn(globalMessageService, 'add').and.callThrough();
        component.showErrorMessage({
          details: [
            { message: 'error' }
          ]
        });

        expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'error' }, GlobalMessageType.MSG_TYPE_ERROR);
      });
    });
  });
});
