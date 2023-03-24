import { TestBed } from '@angular/core/testing';

import { WorldpayApmService } from './worldpay-apm.service';
import { ActiveCartService, CmsService, ConverterService, UserIdService } from '@spartacus/core';
import { Store, StoreModule } from '@ngrx/store';
import { reducerWorldpay } from '../../store/worldpay.reducer';
import { WORLDPAY_FEATURE, WorldpayState } from '../../store/worldpay.state';
import { of } from 'rxjs';
import { GetAvailableApms, SetSelectedAPM } from '../../store/worldpay.action';
import { PaymentMethod } from '../../interfaces';
import { CheckoutService } from '@spartacus/checkout/core';
import createSpy = jasmine.createSpy;

const apm = {
  code: PaymentMethod.Card,
  name: 'credit'
};

const userId = 'current';
const cartId = '0000000';

class MockCmsService {
  getComponentData = createSpy('getComponentData').and.returnValue(of(apm));
}

class MockUserIdService {
  getUserId() {
    return of(userId);
  }
}

class MockActiveCartService {
  getActiveCartId() {
    return of(cartId);
  }
}

class CmsServiceStub {
  getComponentData(uid, code) {
    return of({
      name,
      code
    });
  }

}

class MockCheckoutService {
  getOrderDetails() {
    return of(null);
  }
}

describe('WorldpayApmService', () => {
  let service: WorldpayApmService;
  let worldpayStore: Store<WorldpayState>;
  let converterService: ConverterService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({}), StoreModule.forFeature(WORLDPAY_FEATURE, reducerWorldpay)],
      providers: [
        {
          useClass: MockCmsService,
          provide: CmsService
        },
        {
          useClass: MockUserIdService,
          provide: UserIdService
        },
        {
          useClass: MockActiveCartService,
          provide: ActiveCartService
        },
        {
          provide: CmsService,
          useClass: CmsServiceStub
        },
        {
          provide: CheckoutService,
          useClass: MockCheckoutService
        }

      ]
    });
    service = TestBed.inject(WorldpayApmService);

    worldpayStore = TestBed.inject(Store);
    converterService = TestBed.inject(ConverterService);

    spyOn(converterService, 'pipeable').and.callThrough();
    spyOn(worldpayStore, 'dispatch').and.callThrough();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get apm by id', (done) => {
    const uid: string = 'cc-component';

    service.getApmComponentById(uid, PaymentMethod.Card)
      .subscribe((result) => {
        expect(result.code).toEqual(PaymentMethod.Card);
        done();
      });
  });

  it('should dispatch SetSelectedApm event', () => {
    service.selectAPM(apm);
    expect(worldpayStore.dispatch).toHaveBeenCalledWith(new SetSelectedAPM(apm));
  });

  it('should get requestAvailableApms event', () => {
    service.requestAvailableApms();

    expect(worldpayStore.dispatch).toHaveBeenCalledWith(
      new GetAvailableApms({
        userId,
        cartId
      })
    );
  });

});
