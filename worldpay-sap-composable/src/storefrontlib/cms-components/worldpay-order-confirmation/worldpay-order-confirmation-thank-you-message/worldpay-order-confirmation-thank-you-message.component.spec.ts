import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import {
  CxDatePipe,
  FeatureLevelDirective,
  GlobalMessageService,
  GlobalMessageType,
  I18nTestingModule,
  MockDatePipe,
  MockTranslatePipe,
  TranslatePipe,
  TranslationService,
} from '@spartacus/core';
import { OrderGuestRegisterFormComponent } from '@spartacus/order/components';
import { OrderFacade } from '@spartacus/order/root';
import { AddToHomeScreenBannerComponent } from '@spartacus/storefront';
import { of } from 'rxjs';
import { MockCxFeatureLevelDirective } from 'worldpay-sap-composable-tests';
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
  template: '',
  imports: [I18nTestingModule]
})
class MockAddtoHomeScreenBannerComponent {
}

@Component({
  selector: 'cx-guest-register-form',
  template: '',
  imports: [I18nTestingModule]
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
      imports: [
        I18nTestingModule,
        WorldpayOrderConfirmationThankYouMessageComponent
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
    }).overrideComponent(WorldpayOrderConfirmationThankYouMessageComponent, {
      remove: {
        imports: [
          TranslatePipe,
          CxDatePipe,
          AddToHomeScreenBannerComponent,
          OrderGuestRegisterFormComponent,
          FeatureLevelDirective,
        ],
      },
      add: {
        imports: [
          MockTranslatePipe,
          MockDatePipe,
          MockAddtoHomeScreenBannerComponent,
          MockGuestRegisterFormComponent,
          MockCxFeatureLevelDirective,
        ],
      },
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

    expect(fixture.debugElement.query(By.directive(MockGuestRegisterFormComponent))).not.toBeNull();
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

    // eslint-disable-next-line max-len
    const expectedMessage = `checkoutOrderConfirmation.confirmationOfOrder ${mockOrder.code}. checkoutOrderConfirmation.thankYou checkoutOrderConfirmation.invoiceHasBeenSentByEmail`;

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
