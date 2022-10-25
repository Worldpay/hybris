import { TestBed } from '@angular/core/testing';

import { WorldpayGooglepayService } from './worldpay-googlepay.service';
import { ActiveCartService, UserIdService } from '@spartacus/core';
import { Store, StoreModule } from '@ngrx/store';
import { StateWithWorldpay } from '../../store/worldpay.state';
import { Observable, of } from 'rxjs';
import { AuthoriseGooglePayPayment, GetGooglePayMerchantConfiguration } from '../../store/worldpay.action';
import { GooglePayPaymentRequest } from '../../interfaces';

describe('WorldpayGooglepayService', () => {
  let service: WorldpayGooglepayService;
  let activeCartService: ActiveCartService;
  let userIdService: UserIdServiceStub;
  let worldpayStore: Store<StateWithWorldpay>;

  const userId = 'testUserId';
  const cartId = 'testCartId';

  class ActiveCartServiceStub {
    cartId = cartId;

    public getActiveCartId() {
      return of(this.cartId);
    }
  }

  class UserIdServiceStub {
    getUserId(): Observable<string> {
      return of(userId);
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({})],
      providers: [
        WorldpayGooglepayService,
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
    service = TestBed.inject(WorldpayGooglepayService);
    activeCartService = TestBed.inject(ActiveCartService);
    worldpayStore = TestBed.inject(Store);
    userIdService = TestBed.inject(UserIdService);

    spyOn(worldpayStore, 'dispatch').and.callThrough();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should request merchant configuration', () => {
    service.requestMerchantConfiguration();

    expect(worldpayStore.dispatch).toHaveBeenCalledWith(
      new GetGooglePayMerchantConfiguration({
        userId,
        cartId
      })
    );
  });

  it('should request authorise order', () => {
    const paymentRequest: GooglePayPaymentRequest = {
      apiVersion: 2,
      apiVersionMinor: 0,
      paymentMethodData: {
        info: { billingAddress: { name: 'first last' } },
        tokenizationData: {
          token: '{"json": true}'
        }
      }
    };

    service.authoriseOrder(paymentRequest, true);

    expect(worldpayStore.dispatch).toHaveBeenCalledWith(
      new AuthoriseGooglePayPayment({
        userId,
        cartId,
        token: { json: true },
        billingAddress: { name: 'first last' },
        savePaymentMethod: true
      })
    );
  });
});
