import { WorldpayCheckoutService } from './worldpay-checkout.service';
import { TestBed } from '@angular/core/testing';
import { Store, StoreModule } from '@ngrx/store';
import { of } from 'rxjs';
import { ActiveCartService, PaymentDetails, UserIdService } from '@spartacus/core';
import * as WorldpayActions from '../../store/worldpay.action';
import { StateWithWorldpay } from '../../store/worldpay.state';
import { WorldpayCheckoutPaymentService } from './worldpay-checkout-payment.service';
import { WorldpayApmService } from '../worldpay-apm/worldpay-apm.service';
import { ApmData, InitialPaymentRequestPayload, PaymentMethod } from '../../interfaces';
import { CheckoutState } from '@spartacus/checkout/core';
import createSpy = jasmine.createSpy;

describe('WorldpayCheckoutService', () => {
  let service: WorldpayCheckoutService;
  let activeCartService: ActiveCartService;
  let userIdService: UserIdService;
  let checkoutStore: Store<CheckoutState>;
  let worldpayStore: Store<StateWithWorldpay>;

  const paymentDetails: PaymentDetails = {
    cardNumber: '4444333322221111',
    saved: true
  };
  const userId = 'testUserId';
  const cartId = 'testCartId';
  const apm: ApmData = { code: PaymentMethod.Card };

  class ActiveCartServiceStub {
    cartId = cartId;

    public getActiveCartId() {
      return of(this.cartId);
    }
  }

  class UserIdServiceStub implements Partial<UserIdService> {
    getUserId = createSpy('getUserId').and.returnValue(of(userId));
  }

  class MockCheckoutPaymentService {
    getPaymentDetails() {
      return of(paymentDetails);
    }
  }

  class MockWorldpayApmService {
    getSelectedAPMFromState() {
      return of(apm);
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({})],
      providers: [
        {
          provide: ActiveCartService,
          useClass: ActiveCartServiceStub
        },
        {
          provide: UserIdService,
          useClass: UserIdServiceStub
        },
        {
          provide: WorldpayApmService,
          useClass: MockWorldpayApmService
        },
        {
          provide: WorldpayCheckoutPaymentService,
          useClass: MockCheckoutPaymentService
        },
      ]
    });

    service = TestBed.inject(WorldpayCheckoutService);

    activeCartService = TestBed.inject(ActiveCartService);
    checkoutStore = TestBed.inject(Store);
    worldpayStore = TestBed.inject(Store);
    userIdService = TestBed.inject(UserIdService);

    spyOn(checkoutStore, 'dispatch').and.callThrough();
  });

  it('should be able to call initial payment request', () => {
    const dfReferenceId = '123-123213185-1231231';
    const cseToken = '123';
    const acceptedTermsAndConditions = true;

    service.initialPaymentRequest(
      paymentDetails,
      dfReferenceId,
      cseToken,
      acceptedTermsAndConditions,
      null
    );

    const paymentDetailsWithoutCardNumber = { ...paymentDetails };
    delete paymentDetailsWithoutCardNumber.cardNumber;

    const expectedPayload: InitialPaymentRequestPayload = {
      userId,
      cartId,
      paymentDetails: paymentDetailsWithoutCardNumber,
      dfReferenceId,
      challengeWindowSize: '600x400',
      cseToken,
      acceptedTermsAndConditions,
      deviceSession: null,
    };

    expect(checkoutStore.dispatch).toHaveBeenCalledWith(
      new WorldpayActions.InitialPaymentRequest(expectedPayload)
    );
  });
});
