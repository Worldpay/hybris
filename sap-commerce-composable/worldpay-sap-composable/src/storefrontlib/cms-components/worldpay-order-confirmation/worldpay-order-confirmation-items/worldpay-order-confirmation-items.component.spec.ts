import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldpayOrderConfirmationItemsComponent } from './worldpay-order-confirmation-items.component';
import { FeaturesConfig, FeaturesConfigModule, I18nTestingModule } from '@spartacus/core';
import { of } from 'rxjs';
import { Component, Input } from '@angular/core';
import { AtMessageModule, OutletModule, PromotionsModule } from '@spartacus/storefront';
import { OrderEntry, PromotionLocation } from '@spartacus/cart/base/root';
import { OrderFacade } from '@spartacus/order/root';
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

class MockOrderFacade implements Partial<OrderFacade> {
  clearPlacedOrder = createSpy();
  getOrderDetails = createSpy().and.returnValue(
    of({
      entries: [
        {
          entryNumber: 1,
          quantity: 1,
        },
      ],
    })
  );
}

describe('WorldpayOrderConfirmationItemsComponent', () => {
  let component: WorldpayOrderConfirmationItemsComponent;
  let fixture: ComponentFixture<WorldpayOrderConfirmationItemsComponent>;

  beforeEach(async () => {
    let orderFacade: OrderFacade;
    await TestBed.configureTestingModule({
        imports: [
          I18nTestingModule,
          PromotionsModule,
          FeaturesConfigModule,
          AtMessageModule,
          OutletModule,
        ],
        declarations: [
          WorldpayOrderConfirmationItemsComponent,
          MockReviewSubmitComponent,
        ],
        providers: [
          {
            provide: OrderFacade,
            useClass: MockOrderFacade
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
});
