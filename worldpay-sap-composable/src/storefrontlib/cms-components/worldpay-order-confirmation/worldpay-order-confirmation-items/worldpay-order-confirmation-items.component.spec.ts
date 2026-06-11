import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FeaturesConfig, I18nTestingModule } from '@spartacus/core';
import { OrderFacade } from '@spartacus/order/root';
import { PromotionsModule } from '@spartacus/storefront';
import { of } from 'rxjs';
import { WorldpayOrderConfirmationItemsComponent } from './worldpay-order-confirmation-items.component';
import createSpy = jasmine.createSpy;

class MockOrderFacade implements Partial<OrderFacade> {
  getOrderDetails = createSpy().and.returnValue(
    of({
      code: 'order-123',
      entries: [
        {
          entryNumber: 1,
          quantity: 1,
        },
      ],
    })
  );

  clearPlacedOrder() {
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
        WorldpayOrderConfirmationItemsComponent,
      ],
      providers: [
        {
          provide: OrderFacade,
          useClass: MockOrderFacade
        },
        {
          // eslint-disable-next-line deprecation/deprecation,@typescript-eslint/no-deprecated
          provide: FeaturesConfig,
          useValue: {
            features: { level: '1.3' },
          },
        },
      ],
    }).compileComponents();
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
