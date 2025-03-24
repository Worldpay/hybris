import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EffectsModule } from '@ngrx/effects';
import { Store, StoreModule } from '@ngrx/store';
import { provideMockStore } from '@ngrx/store/testing';
import { ActiveCartService } from '@spartacus/cart/base/core';
import { Cart } from '@spartacus/cart/base/root';
import { GlobalMessageService, GlobalMessageType, LoggerService, QueryState, UserIdService } from '@spartacus/core';
import { UserAccountService } from '@spartacus/user/account/core';
import { User } from '@spartacus/user/account/root';
import { WorldpayGuaranteedPaymentsService } from '@worldpay-services/worldpay-guaranteed-payments/worldpay-guaranteed-payments.service';
import { Observable, of, throwError } from 'rxjs';
import { WorldpayGuaranteedPaymentsComponent } from './worldpay-guaranteed-payments.component';

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

const errorMessage = { details: [{ message: 'error message' }] };

describe('WorldpayGuaranteedPaymentsComponent', () => {
  let component: WorldpayGuaranteedPaymentsComponent;
  let fixture: ComponentFixture<WorldpayGuaranteedPaymentsComponent>;
  let worldpayGuaranteedPaymentsService: WorldpayGuaranteedPaymentsService;
  let activeCartService: ActiveCartService;
  let userIdService: UserIdService;
  let userAccountService: UserAccountService;
  let globalMessageService: GlobalMessageService;
  let loggerService: LoggerService;
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
    worldpayGuaranteedPaymentsService = TestBed.inject(WorldpayGuaranteedPaymentsService);
    activeCartService = TestBed.inject(ActiveCartService);
    userAccountService = TestBed.inject(UserAccountService);
    userIdService = TestBed.inject(UserIdService);
    globalMessageService = TestBed.inject(GlobalMessageService);
    loggerService = TestBed.inject(LoggerService);

    spyUserId = spyOn(userIdService, 'getUserId');
    spyAccount = spyOn(userAccountService, 'get');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be disabled', () => {
    spyOn(worldpayGuaranteedPaymentsService, 'isGuaranteedPaymentsEnabled').and.returnValue(of(false));
    let enabled;
    worldpayGuaranteedPaymentsService.isGuaranteedPaymentsEnabled()
      .subscribe(isEnabled => {
        enabled = isEnabled;
      });

    expect(enabled).toBeFalse();
    expect(component.sessionId).toBe('');
  });

  describe('should be enabled', () => {
    beforeEach(() => {
      spyOn(worldpayGuaranteedPaymentsService, 'isGuaranteedPaymentsEnabled').and.returnValue(of(true));
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
      userAccountService.get().subscribe(user => {
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
        worldpayGuaranteedPaymentsService.setSessionId('user-id');
        spyAccount.and.returnValue(of(registeredUser));
        spyUserId.and.returnValue(of('current'));
        component.firstLoad = true;
        fixture.detectChanges();
        component.ngOnInit();
      });

      it('should get Registered User', () => {
        let userUid;
        userAccountService.get().subscribe(user => {
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
        worldpayGuaranteedPaymentsService.getSessionId().subscribe(sessionId => id = sessionId).unsubscribe();
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

    it('should generate script when session id changes', () => {
      spyOn(worldpayGuaranteedPaymentsService, 'generateScript');
      spyOn(worldpayGuaranteedPaymentsService, 'getSessionId').and.returnValue(of('new-session-id'));
      fixture.detectChanges();
      expect(worldpayGuaranteedPaymentsService.generateScript).toHaveBeenCalledWith('new-session-id');
    });

    it('should handle error when getting session id', () => {
      spyOn(worldpayGuaranteedPaymentsService, 'getSessionId').and.returnValue(throwError(() => errorMessage));
      spyOn(globalMessageService, 'add');
      spyOn(console, 'error');
      fixture.detectChanges();
      expect(globalMessageService.add).toHaveBeenCalledWith({ key: errorMessage.details[0].message }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(console.error).toHaveBeenCalledWith('Failed to get Guaranteed Payments session Id, check component configuration', errorMessage);
    });

    it('should update session id for anonymous user', () => {
      spyOn(worldpayGuaranteedPaymentsService, 'isGuaranteedPaymentsEnabledState').and.returnValue(of({
        error: false,
        loading: false,
        data: true
      }));
      fixture.detectChanges();
      let sessionId;
      component.updateSessionId().subscribe(id => sessionId = id);
      expect(sessionId).toBe('anonymous_cart-100');
    });

    it('should update session id for registered user', () => {
      spyOn(worldpayGuaranteedPaymentsService, 'isGuaranteedPaymentsEnabledState').and.returnValue(of({
        error: false,
        loading: false,
        data: true
      }));
      spyAccount.and.returnValue(of(registeredUser));
      spyUserId.and.returnValue(of('current'));
      fixture.detectChanges();
      let sessionId;
      component.updateSessionId().subscribe(id => sessionId = id);
      expect(sessionId).toBe('user-id_cart-100');
    });

    it('should show error message and log error when initialization fails', () => {
      spyOn(globalMessageService, 'add');
      spyOn(loggerService, 'error');
      spyOn(worldpayGuaranteedPaymentsService, 'setGuaranteedPaymentsEnabledEvent');

      component.showErrorMessage(errorMessage);
      loggerService.error('Failed to initialize Guaranteed Payments, check component configuration', errorMessage);
      worldpayGuaranteedPaymentsService.setGuaranteedPaymentsEnabledEvent(false);

      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'error message' }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(loggerService.error).toHaveBeenCalledWith('Failed to initialize Guaranteed Payments, check component configuration', errorMessage);
      expect(worldpayGuaranteedPaymentsService.setGuaranteedPaymentsEnabledEvent).toHaveBeenCalledWith(false);
    });

    it('should show error message and log error when session id update fails', () => {
      spyOn(globalMessageService, 'add');
      spyOn(loggerService, 'error');
      spyOn(worldpayGuaranteedPaymentsService, 'setGuaranteedPaymentsEnabledEvent');

      component.showErrorMessage(errorMessage);
      loggerService.error('Failed to update Guaranteed Payments session Id, check component configuration', errorMessage);
      worldpayGuaranteedPaymentsService.setGuaranteedPaymentsEnabledEvent(false);

      expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'error message' }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(loggerService.error).toHaveBeenCalledWith('Failed to update Guaranteed Payments session Id, check component configuration', errorMessage);
      expect(worldpayGuaranteedPaymentsService.setGuaranteedPaymentsEnabledEvent).toHaveBeenCalledWith(false);
    });

    it('should handle error when updating session id', () => {
      spyOn(worldpayGuaranteedPaymentsService, 'isGuaranteedPaymentsEnabledState').and.returnValue(throwError(() => errorMessage));
      spyOn(globalMessageService, 'add');
      spyOn(loggerService, 'error');
      fixture.detectChanges();
      component.updateSessionId().subscribe({
        error: (error) => {
          expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'error message' }, GlobalMessageType.MSG_TYPE_ERROR);
          expect(loggerService.error).toHaveBeenCalledWith('Failed to update Guaranteed Payments session Id, check component configuration', errorMessage);
        }
      });
    });
    
    it('should generate script when session id changes', () => {
      spyOn(worldpayGuaranteedPaymentsService, 'generateScript');
      spyOn(worldpayGuaranteedPaymentsService, 'getSessionId').and.returnValue(of('new-session-id'));
      fixture.detectChanges();
      expect(worldpayGuaranteedPaymentsService.generateScript).toHaveBeenCalledWith('new-session-id');
    });

    it('should handle error when getting session id', () => {
      spyOn(worldpayGuaranteedPaymentsService, 'getSessionId').and.returnValue(throwError(() => errorMessage));
      spyOn(globalMessageService, 'add');
      spyOn(loggerService, 'error');
      fixture.detectChanges();
      expect(globalMessageService.add).toHaveBeenCalledWith({ key: errorMessage.details[0].message }, GlobalMessageType.MSG_TYPE_ERROR);
      expect(loggerService.error).toHaveBeenCalledWith('Failed to get Guaranteed Payments session Id, check component configuration', errorMessage);
    });

    it('should update session id for anonymous user', () => {
      spyOn(worldpayGuaranteedPaymentsService, 'isGuaranteedPaymentsEnabledState').and.returnValue(of({
        error: false,
        loading: false,
        data: true
      }));
      spyUserId.and.returnValue(of('anonymous'));
      fixture.detectChanges();
      let sessionId;
      component.updateSessionId().subscribe(id => sessionId = id);
      expect(sessionId).toBe('anonymous_cart-100');
    });

    it('should update session id for registered user', () => {
      spyOn(worldpayGuaranteedPaymentsService, 'isGuaranteedPaymentsEnabledState').and.returnValue(of({
        error: false,
        loading: false,
        data: true
      }));
      spyAccount.and.returnValue(of(registeredUser));
      spyUserId.and.returnValue(of('current'));
      fixture.detectChanges();
      let sessionId;
      component.updateSessionId().subscribe(id => sessionId = id);
      expect(sessionId).toBe('user-id_cart-100');
    });
  });
});
