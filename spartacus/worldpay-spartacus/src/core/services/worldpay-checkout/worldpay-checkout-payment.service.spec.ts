import { WorldpayCheckoutPaymentService } from './worldpay-checkout-payment.service';
import { inject, TestBed } from '@angular/core/testing';
import { Store, StoreModule } from '@ngrx/store';
import { ActiveCartService, Address, PaymentDetails, UserIdService } from '@spartacus/core';
import * as WorldpayActions from '../../store/worldpay.action';
import { of, Subject } from 'rxjs';
import { StateWithWorldpay } from '../../store/worldpay.state';
import { DomSanitizer } from '@angular/platform-browser';
import { CheckoutActions, CheckoutState } from '@spartacus/checkout/core';
import { takeUntil } from 'rxjs/operators';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import createSpy = jasmine.createSpy;

describe('WorldpayCheckoutPaymentService', () => {
  let service: WorldpayCheckoutPaymentService;
  let userIdService: UserIdService;
  let activeCartService: ActiveCartService;
  let checkoutStore: Store<CheckoutState>;
  let worldpayStore: Store<StateWithWorldpay>;
  let sanitizer: DomSanitizer;
  let router: Router;

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

  const paymentDetails: PaymentDetails = {
    id: 'mockPaymentDetails'
  };

  const address: Address = {
    line1: '123 Test St',
    postalCode: 'AA1 2BB'
  };

  class DomSanitizerStub {
    public bypassSecurityTrustResourceUrl(url) {
      return url;
    }
  }

  const getSerializedUrl = (): string => {
    const parameters = `${router.serializeUrl(router.createUrlTree(['']))}`;
    return parameters.length > 1 ? parameters : '/';
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
        RouterTestingModule,
      ],
      providers: [
        WorldpayCheckoutPaymentService,
        {
          provide: ActiveCartService,
          useClass: ActiveCartServiceStub
        },
        {
          provide: UserIdService,
          useClass: UserIdServiceStub
        },
        {
          provide: DomSanitizer,
          useClass: DomSanitizerStub
        },
      ]
    });

    service = TestBed.inject(WorldpayCheckoutPaymentService);
    activeCartService = TestBed.inject(ActiveCartService);
    checkoutStore = TestBed.inject(Store);
    worldpayStore = TestBed.inject(Store);
    userIdService = TestBed.inject(UserIdService);
    sanitizer = TestBed.inject(DomSanitizer);
    router = TestBed.inject(Router);

    spyOn(checkoutStore, 'dispatch').and.callThrough();

    window['Worldpay'] = {
      encrypt: () => 'dummyCseToken',
      setPublicKey: () => {
      }
    };
  });

  it('should inject WorldpayCheckoutPaymentService', inject(
    [WorldpayCheckoutPaymentService],
    (worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService) => {
      expect(worldpayCheckoutPaymentService).toBeTruthy();
    }
  ));

  it('should be able to create payment details', () => {
    spyOn(worldpayStore, 'pipe').and.returnValue(of('pk'));

    service.createPaymentDetails(paymentDetails);

    expect(checkoutStore.dispatch).toHaveBeenCalledWith(
      new WorldpayActions.CreateWorldpayPaymentDetails({
        userId,
        cartId,
        paymentDetails,
        cseToken: 'dummyCseToken'
      })
    );
  });

  it('should be able to use existing payment details', () => {
    spyOn(worldpayStore, 'pipe').and.returnValue(of('pk'));

    service.useExistingPaymentDetails(paymentDetails);

    expect(checkoutStore.dispatch).toHaveBeenCalledWith(
      new WorldpayActions.UseExistingWorldpayPaymentDetails({
        userId,
        cartId,
        paymentDetails
      })
    );
  });

  it('should set billing address', () => {
    spyOn(worldpayStore, 'pipe').and.returnValue(of('pk'));
    const drop = new Subject();
    service.setPaymentAddress(address).pipe(takeUntil(drop)).subscribe(response => response);

    expect(checkoutStore.dispatch).toHaveBeenCalledWith(
      new WorldpayActions.SetPaymentAddress({
        userId,
        cartId,
        address
      })
    );
  });

  it('should get the 3ds DDC iframe url', () => {
    const ddcUrl = '/ddc-iframe/action';
    const cardNumber = '4444333322221111';
    const jwt = 'some jwt data';
    const context = getSerializedUrl();

    service.setThreeDsDDCIframeUrl(ddcUrl, cardNumber, jwt);

    expect(worldpayStore.dispatch).toHaveBeenCalledWith(
      new WorldpayActions.SetWorldpayDDCIframeUrl(
        `${context}worldpay-3ds-device-detection/${encodeURIComponent(
          ddcUrl
        )}/${encodeURIComponent(cardNumber)}/${encodeURIComponent(jwt)}`
      )
    );
  });

  it('should get the 3ds challenge iframe url', () => {
    const challengeUrl = '/challenge-iframe/action';
    const merchantData = '111020020219';
    const jwt = 'some jwt data';
    const context = getSerializedUrl();

    service.setThreeDsChallengeIframeUrl(challengeUrl, jwt, merchantData);

    expect(worldpayStore.dispatch).toHaveBeenCalledWith(
      new WorldpayActions.SetWorldpayChallengeIframeUrl(
        `${context}worldpay-3ds-challenge/${encodeURIComponent(
          challengeUrl
        )}/${encodeURIComponent(merchantData)}/${encodeURIComponent(jwt)}`
      )
    );
  });

  it('should clear payment details step', () => {
    service.resetSetPaymentDetailsProcess();

    expect(checkoutStore.dispatch).toHaveBeenCalledWith(
      new CheckoutActions.ResetSetPaymentDetailsProcess()
    );
  });
});
