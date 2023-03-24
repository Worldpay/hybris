import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldpayApmComponent } from './worldpay-apm-component.component';
import { CmsComponentData, Media } from '@spartacus/storefront';
import { WorldpayCheckoutPaymentService } from '../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';
import { of } from 'rxjs';
import { WorldpayApmModel } from './worldpay-apm.model';

const MockCmsComponentData: CmsComponentData<WorldpayApmModel> = {
  data$: of({
    name: 'Name should not be used',
    media: {
      src: 'http://placekitten.com/201/201',
      alt: 'paypal kitteh'
    } as Media,
    apmConfiguration: {
      code: 'paypal',
      description: 'Secure Paypal payments',
      name: 'PayPal'
    }
  } as WorldpayApmModel),

  uid: 'paypalComponent'
};
describe('WorldpayApmComponent', () => {
  let component: WorldpayApmComponent;
  let fixture: ComponentFixture<WorldpayApmComponent>;
  let mockWorldpayCheckoutPaymentService;
  let apm = 'paypal';

  class MockWorldpayCheckoutPaymentService
    implements Partial<WorldpayCheckoutPaymentService> {
    getSelectedAPMFromState() {
      return of(apm);
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WorldpayApmComponent],
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
    fixture = TestBed.createComponent(WorldpayApmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
