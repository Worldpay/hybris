import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { AbstractOrderContextDirective } from '@spartacus/cart/base/components';
import { DeliveryMode } from '@spartacus/cart/base/root';
import { Address, Country, CxDatePipe, I18nTestingModule, MockDatePipe, MockTranslatePipe, MockTranslationService, TranslatePipe, TranslationService } from '@spartacus/core';
import { OrderFacade } from '@spartacus/order/root';
import { CardComponent, OutletContextData, OutletModule, PromotionsModule } from '@spartacus/storefront';
import { of } from 'rxjs';
import { MockCxCardComponent } from 'worldpay-sap-composable-tests';
import { MockAbstractOrderContextDirective } from '../../../../tests/directives/cx-abstract-oreder-context.directive.mock';
import { WorldpayOrderConfirmationShippingComponent } from './worldpay-order-confirmation-shipping.component';
import createSpy = jasmine.createSpy;

const mockCountry: Country = {
  isocode: 'JP',
  name: 'Japan',
};
const mockAddress: Address = {
  firstName: 'John',
  lastName: 'Doe',
  titleCode: 'mr',
  line1: 'Toyosaki 2 create on cart',
  line2: 'line2',
  town: 'town',
  region: { isocode: 'JP-27' },
  postalCode: 'zip',
  country: mockCountry,
};

class MockOrderFacade implements Partial<OrderFacade> {
  getOrderDetails = createSpy().and.returnValue(
    of({
      entries: [
        {
          entryNumber: 1,
          quantity: 1,
        },
      ],
      deliveryAddress: { id: 'testAddress' },
      deliveryMode: { code: 'testCode' },
    })
  );
}

describe('WorldpayOrderConfirmationShippingComponent', () => {
  let component: WorldpayOrderConfirmationShippingComponent;
  let fixture: ComponentFixture<WorldpayOrderConfirmationShippingComponent>;

  function configureTestingModule(): TestBed {
    return TestBed.configureTestingModule({
      providers: [
        {
          provide: OrderFacade,
          useClass: MockOrderFacade
        },
        {
          provide: TranslationService,
          useClass: MockTranslationService,
        }
      ],
    }).overrideComponent(WorldpayOrderConfirmationShippingComponent, {
      remove: {
        imports: [
          TranslatePipe,
          CxDatePipe,
          CardComponent,
          AbstractOrderContextDirective
        ],
      },
      add: {
        imports: [
          MockTranslatePipe,
          MockDatePipe,
          MockCxCardComponent,
          MockAbstractOrderContextDirective
        ],
      },
    });
  }

  function stubSeviceAndCreateComponent() {
    fixture = TestBed.createComponent(WorldpayOrderConfirmationShippingComponent);
    component = fixture.componentInstance;
  }

  describe('Not use outlet', () => {
    beforeEach(() => {
      configureTestingModule();
      stubSeviceAndCreateComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should get order entries, delivery address and delivery mode', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(component.entries?.length).toEqual(1);
    }));

    it('should call getDeliveryAddressCard(deliveryAddress, countryName) to get address card data', () => {
      component
        .getDeliveryAddressCard(mockAddress, 'Canada')
        .subscribe((card) => {
          expect(card.title).toEqual('addressCard.shipTo');
          expect(card.textBold).toEqual('John Doe');
          expect(card.text).toEqual([
            'Toyosaki 2 create on cart',
            'line2',
            'town, JP-27, Canada',
            'zip',
            undefined,
          ]);
        });
    });

    it('should call getDeliveryModeCard(deliveryMode) to get delivery mode card data', () => {
      const selectedMode: DeliveryMode = {
        code: 'standard-gross',
        name: 'Standard gross',
        description: 'Standard Delivery description',
        deliveryCost: {
          formattedValue: '$9.99',
        },
      };
      component.getDeliveryModeCard(selectedMode).subscribe((card) => {
        expect(card.title).toEqual('checkoutMode.deliveryMethod');
        expect(card.textBold).toEqual('Standard gross');
        expect(card.text).toEqual(['Standard Delivery description', '$9.99']);
      });
    });
  });

  describe('use Order with different deliveryPointOfService value', () => {
    class MockOrderFacade implements Partial<OrderFacade> {
      getOrderDetails = createSpy().and.returnValue(
        of({
          entries: [
            {
              entryNumber: 1,
              quantity: 1,
              deliveryPointOfService: null,
            },
          ],
          deliveryAddress: { id: 'testAddress' },
          deliveryMode: { code: 'testCode' },
        })
      );
    }

    function configureTestingModule(): TestBed {
      return TestBed.configureTestingModule({
        imports: [
          PromotionsModule,
          OutletModule,
          WorldpayOrderConfirmationShippingComponent,
          I18nTestingModule
        ],
        providers: [{
          provide: OrderFacade,
          useClass: MockOrderFacade
        }],
      }).overrideComponent(WorldpayOrderConfirmationShippingComponent, {
        remove: {
          imports: [
            TranslatePipe,
            CxDatePipe,
            CardComponent,
            AbstractOrderContextDirective
          ],
        },
        add: {
          imports: [
            MockTranslatePipe,
            MockDatePipe,
            MockCxCardComponent,
            MockAbstractOrderContextDirective
          ],
        },
      });
    }

    beforeEach(() => {
      configureTestingModule();
      stubSeviceAndCreateComponent();
    });

    it('should get entries when deliveryPointOfService is null', fakeAsync(() => {
      fixture.detectChanges();
      tick();
      expect(component.entries?.length).toEqual(1);
    }));
  });

  describe('Use outlet with outlet context data', () => {
    const context$ = of({
      showItemList: false,
      order: { code: 'test' },
    });

    beforeEach(() => {
      configureTestingModule()
        .overrideComponent(WorldpayOrderConfirmationShippingComponent, {
          remove: {
            imports: [TranslatePipe, CxDatePipe, CardComponent],
          },
          add: {
            imports: [MockTranslatePipe, MockDatePipe, MockCxCardComponent],
          },
        })
        .overrideProvider(OutletContextData, {
          useValue: { context$ },
        });
      TestBed.compileComponents();
      stubSeviceAndCreateComponent();
    });

    it('should be able to get data from outlet context', () => {
      component.ngOnInit();

      expect(component.showItemList).toEqual(false);

      component.order$.subscribe((value) =>
        expect(value).toEqual({ code: 'test' })
      );
    });
  });
});
