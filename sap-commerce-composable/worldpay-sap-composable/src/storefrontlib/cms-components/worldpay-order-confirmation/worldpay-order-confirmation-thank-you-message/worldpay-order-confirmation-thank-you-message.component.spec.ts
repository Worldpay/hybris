import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, Input } from '@angular/core';
import { By } from '@angular/platform-browser';
import { GlobalMessageService, GlobalMessageType, I18nTestingModule, TranslationService, } from '@spartacus/core';
import { of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { OrderFacade } from '@spartacus/order/root';
import { MockFeatureLevelDirective } from '../../worldpay-cart-shared/worldpay-cart-item/worldpay-cart-item.component.spec';
import { WorldpayOrderConfirmationThankYouMessageComponent } from './worldpay-order-confirmation-thank-you-message.component';
import createSpy = jasmine.createSpy;

const replenishmentOrderCode = 'test-repl-code';
const mockOrder = {
  code: 'test-code-412',
  guid: 'guid',
  guestCustomer: true,
  paymentInfo: { billingAddress: { email: 'test@test.com' } },
};

@Component({
  selector: 'cx-add-to-home-screen-banner',
  template: ''
})
class MockAddtoHomeScreenBannerComponent {
}

@Component({
  selector: 'y-worldpay-order-guest-register-form',
  template: ''
})
class MockGuestRegisterFormComponent {
  @Input() guid: string;
  @Input() email: string;
}

class MockOrderFacade implements Partial<OrderFacade> {
  getOrderDetails = createSpy().and.returnValue(of(mockOrder));
  clearPlacedOrder = createSpy();
}

class MockGlobalMessageService {
  add(): void {
  }
}

class MockTranslationService {
  translate = createSpy().and.returnValue(of('testMessage'));
}

const mockActivatedRoute = {
  snapshot: {
    queryParams: {},
  },
};

describe('WorldpayOrderConfirmationThankYouMessageComponent', () => {
  let component: WorldpayOrderConfirmationThankYouMessageComponent;
  let fixture: ComponentFixture<WorldpayOrderConfirmationThankYouMessageComponent>;

  let orderFacade: OrderFacade;
  let globalMessageService: GlobalMessageService;
  let activatedRoute: ActivatedRoute;
  let addSpy: jasmine.Spy;

  beforeEach(async () =>
    await TestBed.configureTestingModule({
      imports: [I18nTestingModule],
      declarations: [
        WorldpayOrderConfirmationThankYouMessageComponent,
        MockAddtoHomeScreenBannerComponent,
        MockGuestRegisterFormComponent,
        MockFeatureLevelDirective,
      ],
      providers: [
        {
          provide: GlobalMessageService,
          useClass: MockGlobalMessageService
        },
        {
          provide: OrderFacade,
          useClass: MockOrderFacade
        },
        {
          provide: TranslationService,
          useClass: MockTranslationService
        },
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute
        }
      ],
    }).compileComponents()
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayOrderConfirmationThankYouMessageComponent);
    component = fixture.componentInstance;
    orderFacade = TestBed.inject(OrderFacade);
    globalMessageService = TestBed.inject(GlobalMessageService);
    activatedRoute = TestBed.inject(ActivatedRoute);
    activatedRoute.snapshot.queryParams = {};
    addSpy = spyOn(globalMessageService, 'add');
  });

  it('should create', () => {
    component.ngOnInit();
    expect(component).toBeTruthy();
  });

  it('should display order code', () => {
    fixture.detectChanges();
    expect(
      fixture.debugElement.query(By.css('.cx-page-title')).nativeElement.innerHTML).toContain(mockOrder.code);
  });

  it('should display replenishment order code', () => {
    orderFacade.getOrderDetails = createSpy().and.returnValue(
      of({
        ...mockOrder,
        replenishmentOrderCode
      })
    );

    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('.cx-page-title')).nativeElement.innerHTML).toContain(replenishmentOrderCode);
  });

  it('should display guest register form for guest user', () => {
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css('y-worldpay-order-guest-register-form'))).not.toBeNull();
  });

  it('should not display guest register form for login user', () => {
    orderFacade.getOrderDetails = createSpy().and.returnValue(
      of({
        guid: 'guid',
        guestCustomer: false
      })
    );

    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css('cx-guest-register-form'))).toBeNull();
  });

  it('should add assistive message after view init', () => {

    const expectedMessage = `testMessage ${mockOrder.code}. testMessage testMessage`;

    component.ngOnInit();
    component.ngAfterViewInit();

    expect(addSpy).toHaveBeenCalledWith(
      expectedMessage,
      GlobalMessageType.MSG_TYPE_ASSISTIVE
    );
  });

  describe('When pending queryParam exists', () => {
    it('should show redirectPaymentPending message', () => {
      activatedRoute.snapshot.queryParams = { pending: 'true' };
      component.ngOnInit();
      component.ngAfterViewInit();
      fixture.detectChanges();

      expect(fixture.debugElement.query(By.css('.cx-order-confirmation-message h2'))
        .nativeElement.innerHTML).toContain('checkoutOrderConfirmation.pending.thankYouForOrder');

      expect(fixture.debugElement.query(By.css('.cx-order-confirmation-message p'))
        .nativeElement.innerHTML).toContain('checkoutOrderConfirmation.pending.invoiceHasBeenSentByEmail email:test@test.com');
    });
  });
});
