import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldpayApmCreditcardComponent } from './worldpay-apm-creditcard-component.component';
import { WorldpayCheckoutPaymentService } from '../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';
import { CmsComponentData, Media } from '@spartacus/storefront';
import { of } from 'rxjs';
import {
  WorldpayCCModel,
  WorldpayGooglePayModel
} from '../worldpay-apm-component/worldpay-apm.model';

const MockCmsComponentData: CmsComponentData<WorldpayCCModel> = {
  data$: of({
    name: 'Credit card',
    media: {
      src: 'http://placekitten.com/202/202',
      alt: 'credit kitteh'
    } as Media
  } as WorldpayCCModel),

  uid: 'creditComponent'
};

describe('WorldpayApmCreditcardComponentComponent', () => {
  let component: WorldpayApmCreditcardComponent;
  let fixture: ComponentFixture<WorldpayApmCreditcardComponent>;

  class MockWorldpayCheckoutPaymentService
    implements Partial<WorldpayCheckoutPaymentService> {
    getSelectedAPMFromState() {
      return of('credit card');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WorldpayApmCreditcardComponent],
      providers: [
        { provide: CmsComponentData, useValue: MockCmsComponentData },
        {
          provide: WorldpayCheckoutPaymentService,
          useClass: MockWorldpayCheckoutPaymentService
        }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayApmCreditcardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be selected if apm equals to credit card', () => {
    expect(component.code).toEqual('credit card');
    expect(component.isSelected).toBeTruthy();
  });
});
