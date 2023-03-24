import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldpayOrderConfirmationItemsComponent } from './worldpay-order-confirmation-items.component';
import { FeaturesConfig, FeaturesConfigModule, I18nTestingModule, Order, OrderEntry, PromotionLocation } from '@spartacus/core';
import { CheckoutFacade } from '@spartacus/checkout/root';
import { Observable, of } from 'rxjs';
import { Component, Input } from '@angular/core';
import { PromotionsModule } from '@spartacus/storefront';
import { By } from '@angular/platform-browser';
import createSpy = jasmine.createSpy;

@Component({
  selector: 'y-worldpay-cart-item-list',
  template: ''
})
class MockReviewSubmitComponent {
  @Input() items: OrderEntry[];
  @Input() readonly: boolean;
  @Input() promotionLocation: PromotionLocation = PromotionLocation.Checkout;
}

class MockCheckoutService {
  clearCheckoutData = createSpy();

  getOrderDetails(): Observable<Order> {
    return of({
      entries: [
        {
          entryNumber: 1,
          quantity: 1,
        },
      ],
    });
  }
}

describe('WorldpayOrderConfirmationItemsComponent', () => {
  let component: WorldpayOrderConfirmationItemsComponent;
  let fixture: ComponentFixture<WorldpayOrderConfirmationItemsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        I18nTestingModule,
        PromotionsModule,
        FeaturesConfigModule
      ],
      declarations: [
        WorldpayOrderConfirmationItemsComponent,
        MockReviewSubmitComponent,
      ],
      providers: [
        {
          provide: CheckoutFacade,
          useClass: MockCheckoutService
        },
        {
          provide: FeaturesConfig,
          useValue: {
            features: { level: '1.3' },
          },
        },
      ],
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayOrderConfirmationItemsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display items', () => {
    const items = () => fixture.debugElement.query(By.css('y-worldpay-cart-item-list'));
    component.ngOnInit();
    fixture.detectChanges();
    expect(items()).toBeTruthy();
  });
});
