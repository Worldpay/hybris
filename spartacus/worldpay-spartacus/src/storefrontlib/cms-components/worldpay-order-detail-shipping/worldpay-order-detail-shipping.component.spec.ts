import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldpayOrderDetailShippingComponent } from './worldpay-order-detail-shipping.component';
import { I18nTestingModule, Order, ReplenishmentOrder } from '@spartacus/core';
import { OrderDetailsService } from '@spartacus/storefront';
import { Observable, of } from 'rxjs';
import { Component, Input } from '@angular/core';

const mockOrder: Order = {
  code: 'test-code-412',
  statusDisplay: 'test-status-display',
  created: new Date('2019-02-11T13:02:58+0000'),
  purchaseOrderNumber: 'test-po',
  costCenter: {
    name: 'Rustic Global',
    unit: {
      name: 'Rustic',
    },
  },
  worldpayAPMPaymentInfo: {
    name: 'Paypal'
  }
};

const mockReplenishmentOrder: ReplenishmentOrder = {
  active: true,
  purchaseOrderNumber: 'test-po',
  replenishmentOrderCode: 'test-repl-order',
  entries: [{
    entryNumber: 0,
    product: { name: 'test-product' }
  }],
  firstDate: '1994-01-11T00:00Z',
  trigger: {
    activationTime: '1994-01-11T00:00Z',
    displayTimeTable: 'every-test-date',
  },
  paymentType: {
    code: 'test-type',
    displayName: 'test-type-name',
  },
  costCenter: {
    name: 'Rustic Global',
    unit: {
      name: 'Rustic',
    },
  },
};

class MockOrderDetailsService {
  getOrderDetails(): Observable<Order> {
    return of(mockOrder);
  }
}

@Component({
  selector: 'y-worldpay-order-overview',
  template: '',
})
class MockWorldpayOrderOverviewComponent {
  @Input() order: Order;
}

describe('WorldpayOrderDetailShippingComponent', () => {
  let component: WorldpayOrderDetailShippingComponent;
  let fixture: ComponentFixture<WorldpayOrderDetailShippingComponent>;
  let orderDetailsService: OrderDetailsService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [I18nTestingModule],
      providers: [
        {
          provide: OrderDetailsService,
          useClass: MockOrderDetailsService
        },
      ],
      declarations: [
        WorldpayOrderDetailShippingComponent,
        MockWorldpayOrderOverviewComponent
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayOrderDetailShippingComponent);
    orderDetailsService = TestBed.inject(OrderDetailsService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be able to get order details', () => {
    let result: any;

    orderDetailsService
      .getOrderDetails()
      .subscribe((data) => (result = data))
      .unsubscribe();

    expect(result).toEqual(mockOrder);

    spyOn(orderDetailsService, 'getOrderDetails').and.returnValue(
      of(mockReplenishmentOrder)
    );

    orderDetailsService
      .getOrderDetails()
      .subscribe((data) => (result = data))
      .unsubscribe();

    expect(result).toEqual(mockReplenishmentOrder);
  });
});
