import { Component, CUSTOM_ELEMENTS_SCHEMA, Directive, Input, NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OrderEntry, PromotionLocation } from '@spartacus/cart/base/root';
import { FeaturesConfig, FeaturesConfigModule, I18nTestingModule } from '@spartacus/core';
import { OrderFacade } from '@spartacus/order/root';
import { AtMessageModule, OutletDirective, OutletModule, PromotionsModule } from '@spartacus/storefront';
import { of } from 'rxjs';
import { WorldpayOrderConfirmationItemsComponent } from './worldpay-order-confirmation-items.component';
import createSpy = jasmine.createSpy;

@Component({
  selector: 'y-worldpay-cart-item-list',
  template: '',
  standalone: false
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

@Directive({
  selector: '[cxOutlet]',
  standalone: false,
})
class MockOutletDirective implements Partial<OutletDirective> {
  @Input() cxOutlet: string;
}

describe('WorldpayOrderConfirmationItemsComponent', () => {
  let component: WorldpayOrderConfirmationItemsComponent;
  let fixture: ComponentFixture<WorldpayOrderConfirmationItemsComponent>;

  beforeEach(async () => {
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
        MockOutletDirective
      ],
      providers: [
        {
          provide: OrderFacade,
          useClass: MockOrderFacade
        },
        {
          // eslint-disable-next-line deprecation/deprecation
          provide: FeaturesConfig,
          useValue: {
            features: { level: '1.3' },
          },
        },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
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
