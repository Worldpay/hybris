import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { DeliveryMode } from '@spartacus/cart/base/root';
import { Address, CmsOrderDetailOverviewComponent, I18nTestingModule, PaymentDetails, TranslationService, } from '@spartacus/core';
import { OrderDetailsService, OrderOverviewComponentService } from '@spartacus/order/components';
import { Order, ReplenishmentOrder } from '@spartacus/order/root';
import { CmsComponentData } from '@spartacus/storefront';
import { MockCxCardComponent } from '@worldpay-tests/components';
import { EMPTY, Observable, of } from 'rxjs';
import { WorldpayOrderOverviewComponent } from './worldpay-order-overview.component';

const mockDeliveryAddress: Address = {
  firstName: 'John',
  lastName: 'Smith',
  line1: 'Buckingham Street 5',
  line2: '1A',
  phone: '(+11) 111 111 111',
  postalCode: 'MA8902',
  town: 'London',
  country: {
    name: 'test-country-name',
    isocode: 'UK',
  },
  formattedAddress: 'test-formattedAddress',
};

const mockDeliveryMode: DeliveryMode = {
  name: 'Standard order-detail-shipping',
  description: '3-5 days',
  deliveryCost: {
    formattedValue: 'test-formatted-cosg',
  },
};

const mockBillingAddress: Address = {
  firstName: 'John',
  lastName: 'Smith',
  line1: 'Buckingham Street 5',
  line2: '1A',
  phone: '(+11) 111 111 111',
  postalCode: 'MA8902',
  town: 'London',
  country: {
    name: 'test-country-name',
    isocode: 'UK',
  },
  formattedAddress: 'test-formattedAddress',
};

const mockPayment: PaymentDetails = {
  accountHolderName: 'John Smith',
  cardNumber: '************6206',
  expiryMonth: '12',
  expiryYear: '2026',
  cardType: {
    name: 'Visa',
  },
  billingAddress: mockBillingAddress,
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
  paymentInfo: mockPayment,
};

const mockOrder: Order = {
  code: 'test-code-412',
  deliveryAddress: mockDeliveryAddress,
  deliveryMode: mockDeliveryMode,
  paymentInfo: mockPayment,
  statusDisplay: 'test-status-display',
  created: new Date('2019-02-11T13:02:58+0000'),
  purchaseOrderNumber: 'test-po',
  costCenter: {
    name: 'Rustic Global',
    unit: {
      name: 'Rustic',
    },
  },
};

const mockUnformattedAddress = 'test1, , test3, test4';
const mockFormattedAddress = 'test1, test2, test3, test4';

class MockTranslationService {
  translate(): Observable<string> {
    return EMPTY;
  }
}

class MockOrderDetailsService {
  isOrderDetailsLoading(): Observable<boolean> {
    return of(false);
  }

  getOrderDetails() {
    return of(mockOrder);
  }
}

class MockOrderOverviewComponentService {
  shouldShowDeliveryMode(_mode: DeliveryMode): boolean {
    return true;
  }
}

const mockData: CmsOrderDetailOverviewComponent = {
  simple: false,
};

const MockCmsComponentData = <CmsComponentData<any>>{
  data$: of(mockData),
};

describe('WorldpayOrderOverviewComponent', () => {
  let component: WorldpayOrderOverviewComponent;
  let fixture: ComponentFixture<WorldpayOrderOverviewComponent>;
  let translationService: TranslationService;
  let orderDetailsService: OrderDetailsService;
  let componentService: OrderOverviewComponentService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [I18nTestingModule],
        declarations: [
          WorldpayOrderOverviewComponent,
          MockCxCardComponent
        ],
        providers: [
          {
            provide: TranslationService,
            useClass: MockTranslationService
          },
          {
            provide: OrderOverviewComponentService,
            useClass: MockOrderOverviewComponentService,
          },
          {
            provide: OrderDetailsService,
            useClass: MockOrderDetailsService
          },
          {
            provide: CmsComponentData,
            useValue: MockCmsComponentData
          },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayOrderOverviewComponent);
    component = fixture.componentInstance;
    translationService = TestBed.inject(TranslationService);
    orderDetailsService = TestBed.inject(OrderDetailsService);
    componentService = TestBed.inject(OrderOverviewComponentService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('when replenishment order code is defined', () => {
    beforeEach(() => {
      spyOn(orderDetailsService, 'getOrderDetails').and.returnValue(
        of(mockReplenishmentOrder)
      );
      spyOn(translationService, 'translate').and.returnValue(of('test'));
    });

    it('should call getReplenishmentCodeCardContent(orderCode: string)', () => {
      spyOn(component, 'getReplenishmentCodeCardContent').and.callThrough();

      component
        .getReplenishmentCodeCardContent(
          mockReplenishmentOrder.replenishmentOrderCode
        )
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.text).toEqual([
            mockReplenishmentOrder.replenishmentOrderCode,
          ]);
        })
        .unsubscribe();

      expect(component.getReplenishmentCodeCardContent).toHaveBeenCalledWith(
        mockReplenishmentOrder.replenishmentOrderCode
      );
    });

    it('should call getReplenishmentActiveCardContent(active: boolean)', () => {
      spyOn(component, 'getReplenishmentActiveCardContent').and.callThrough();

      component
        .getReplenishmentActiveCardContent(mockReplenishmentOrder.active)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.text).toEqual(['test']);
        })
        .unsubscribe();

      expect(component.getReplenishmentActiveCardContent).toHaveBeenCalledWith(
        mockReplenishmentOrder.active
      );
    });

    it('should call getReplenishmentStartOnCardContent(isoDate: string)', () => {
      spyOn(component, 'getReplenishmentStartOnCardContent').and.callThrough();

      const date = mockReplenishmentOrder.firstDate;

      component
        .getReplenishmentStartOnCardContent(date)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.text).toEqual([date]);
        })
        .unsubscribe();

      expect(component.getReplenishmentStartOnCardContent).toHaveBeenCalledWith(
        mockReplenishmentOrder.firstDate
      );
    });

    it('should call getReplenishmentFrequencyCardContent(frequency: string)', () => {
      spyOn(
        component,
        'getReplenishmentFrequencyCardContent'
      ).and.callThrough();

      component
        .getReplenishmentFrequencyCardContent(
          mockReplenishmentOrder.trigger.displayTimeTable
        )
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.text).toEqual([
            mockReplenishmentOrder.trigger.displayTimeTable,
          ]);
        })
        .unsubscribe();

      expect(
        component.getReplenishmentFrequencyCardContent
      ).toHaveBeenCalledWith(mockReplenishmentOrder.trigger.displayTimeTable);
    });

    it('should call getReplenishmentNextDateCardContent(isoDate: string)', () => {
      spyOn(component, 'getReplenishmentNextDateCardContent').and.callThrough();

      const date = mockReplenishmentOrder.trigger.activationTime;

      component
        .getReplenishmentNextDateCardContent(date)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.text).toEqual([date]);
        })
        .unsubscribe();

      expect(
        component.getReplenishmentNextDateCardContent
      ).toHaveBeenCalledWith(date);
    });
  });

  describe('when replenishment is NOT defined', () => {
    beforeEach(() => {
      spyOn(translationService, 'translate').and.returnValue(of('test'));
    });

    it('should call getOrderCodeCardContent(orderCode: string)', () => {
      spyOn(component, 'getOrderCodeCardContent').and.callThrough();

      component
        .getOrderCodeCardContent(mockOrder.code)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.text).toEqual([mockOrder.code]);
        })
        .unsubscribe();

      expect(component.getOrderCodeCardContent).toHaveBeenCalledWith(
        mockOrder.code
      );
    });

    it('should call getOrderCurrentDateCardContent(isoDate: string)', () => {
      spyOn(component, 'getOrderCurrentDateCardContent').and.callThrough();

      const date = mockOrder.created.toDateString();

      component
        .getOrderCurrentDateCardContent(date)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.text).toEqual([date]);
        })
        .unsubscribe();

      expect(component.getOrderCurrentDateCardContent).toHaveBeenCalled();
    });

    it('should call getOrderStatusCardContent(status: string)', () => {
      spyOn(component, 'getOrderStatusCardContent').and.callThrough();

      component
        .getOrderStatusCardContent(mockOrder.statusDisplay)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.text).toEqual(['test']);
        })
        .unsubscribe();

      expect(component.getOrderStatusCardContent).toHaveBeenCalledWith(
        mockOrder.statusDisplay
      );
    });
  });

  describe('when purchase order number is defined', () => {
    beforeEach(() => {
      spyOn(translationService, 'translate').and.returnValue(of('test'));
    });

    it('should call getPurchaseOrderNumber(poNumber: string)', () => {
      spyOn(component, 'getPurchaseOrderNumber').and.callThrough();

      component
        .getPurchaseOrderNumber(mockOrder.purchaseOrderNumber)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.text).toEqual([mockOrder.purchaseOrderNumber]);
        })
        .unsubscribe();

      expect(component.getPurchaseOrderNumber).toHaveBeenCalledWith(
        mockOrder.purchaseOrderNumber
      );
    });

    it('should call getMethodOfPaymentCardContent(hasPaymentInfo: PaymentDetails)', () => {
      spyOn(component, 'getMethodOfPaymentCardContent').and.callThrough();

      component
        .getMethodOfPaymentCardContent(mockOrder.paymentInfo)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.text).toEqual(['test']);
        })
        .unsubscribe();

      expect(component.getMethodOfPaymentCardContent).toHaveBeenCalledWith(
        mockOrder.paymentInfo
      );
    });

    it('should call getCostCenterCardContent(costCenter: CostCenter)', () => {
      spyOn(component, 'getCostCenterCardContent').and.callThrough();

      component
        .getCostCenterCardContent(mockOrder.costCenter)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.textBold).toEqual(mockOrder.costCenter.name);
          expect(data.text).toEqual([`(${mockOrder.costCenter.unit.name})`]);
        })
        .unsubscribe();

      expect(component.getCostCenterCardContent).toHaveBeenCalledWith(
        mockOrder.costCenter
      );
    });
  });

  describe('when paymentInfo is defined', () => {

    it('should call getPaymentInfoCardContent(payment: PaymentDetails)', (doneFn) => {
      const mockOrderWithPayment = { ...mockOrder };
      spyOn(translationService, 'translate').and.returnValue(of('paymentForm.payment'));
      spyOn(component, 'getPaymentDetailsLineTranslation').and.returnValue(of('12/2026'));
      spyOn(component, 'getPaymentInfoCardContent').and.callThrough();

      component
        .getPaymentInfoCardContent(mockOrderWithPayment)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('paymentForm.payment');
          expect(data.textBold).toEqual(mockOrderWithPayment.paymentInfo.accountHolderName);
          expect(data.text).toEqual([mockOrderWithPayment.paymentInfo.cardNumber, '12/2026']);
          doneFn();
        })
        .unsubscribe();

      expect(component.getPaymentInfoCardContent).toHaveBeenCalledWith(
        mockOrder
      );
    });

    it('should return card content with APM details when order has APM payment info', (doneFn) => {
      const mockOrderWithAPM = {
        ...mockOrder,
        paymentInfo: {
          expiryYear: null,
          accountHolderName: null,
          cardNumber: null
        },
        worldpayAPMPaymentInfo: { name: 'iDeal' }
      };
      spyOn(component, 'getPaymentDetailsLineTranslation').and.returnValue(of('12/2026'));
      spyOn(translationService, 'translate').and.returnValue(of('paymentForm.payment'));

      component
        .getPaymentInfoCardContent(mockOrderWithAPM)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('paymentForm.payment');
          expect(data.textBold).toEqual(mockOrderWithAPM.paymentInfo.accountHolderName);
          expect(data.text).toEqual([mockOrderWithAPM.paymentInfo.cardNumber, '12/2026']);
          doneFn();
        })
        .unsubscribe();
    });

    it('should isPaymentInfoCardFull be falsy when paymentInfo is partial', () => {
      expect(
        component.isPaymentInfoCardFull({
          ...mockOrder.paymentInfo,
          expiryMonth: undefined,
        })
      ).toBeFalsy();
    });

    it('should call getBillingAddressCardContent(billingAddress: Address)', () => {
      spyOn(component, 'getBillingAddressCardContent').and.callThrough();
      spyOn(translationService, 'translate').and.returnValue(of('paymentForm.billingAddress'));

      const billingAddress = mockOrder.paymentInfo.billingAddress;

      component
        .getBillingAddressCardContent(billingAddress)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('paymentForm.billingAddress');
          expect(data.textBold).toEqual(
            `${billingAddress.firstName} ${billingAddress.lastName}`
          );
          expect(data.text).toEqual([
            billingAddress.formattedAddress,
            billingAddress.country.name,
          ]);
        })
        .unsubscribe();

      expect(component.getBillingAddressCardContent).toHaveBeenCalledWith(
        billingAddress
      );
    });

    describe('getDeliveryAddressCard', () => {
      it('should return card with formatted address when country name is provided', (doneFn) => {
        const mockAddress = { ...mockDeliveryAddress };
        spyOn(translationService, 'translate').and.returnValues(
          of('shipTo'),
          of('phoneNumber'),
          of('mobileNumber')
        );

        component.getDeliveryAddressCard(mockAddress, 'UK').subscribe((card) => {
          expect(card.title).toBe('shipTo');
          expect(card.textBold).toBe(`${mockAddress.firstName} ${mockAddress.lastName}`);
          expect(card.text).toEqual([
            mockAddress.line1,
            mockAddress.line2,
            `${mockAddress.town}, UK`,
            mockAddress.postalCode,
            'phoneNumber: ' + mockAddress.phone
          ]);
          doneFn();
        });
      });

      it('should return card with formatted address when country name is not provided', (doneFn) => {
        const mockAddress = { ...mockDeliveryAddress };
        spyOn(translationService, 'translate').and.returnValues(
          of('shipTo'),
          of('phoneNumber'),
          of('mobileNumber')
        );

        component.getDeliveryAddressCard(mockAddress).subscribe((card) => {
          expect(card.title).toBe('shipTo');
          expect(card.textBold).toBe(`${mockAddress.firstName} ${mockAddress.lastName}`);
          expect(card.text).toEqual([
            mockAddress.line1,
            mockAddress.line2,
            `${mockAddress.town}, ${mockAddress.country.name}`,
            mockAddress.postalCode,
            'phoneNumber: ' + mockAddress.phone
          ]);
          doneFn();
        });
      });
    });
  });

  describe('when deliveryAddress is defined', () => {
    let spy;
    beforeEach(() => {
      spy = spyOn(translationService, 'translate');
      spy.and.callThrough();
    });

    it('should call getDeliveryAddressCard(deliveryAddress: Address, countryName?: string)', () => {
      spyOn(component, 'getDeliveryAddressCard').and.callThrough();

      const deliveryAddress = mockOrder.deliveryAddress;
      component
        .getDeliveryAddressCard(deliveryAddress)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('addressCard.shipTo');
          expect(data.textBold).toEqual(
            mockOrder.paymentInfo.accountHolderName
          );
          expect(data.text).toEqual([
            deliveryAddress.line1,
            deliveryAddress.line2,
            deliveryAddress.town + ', ' + deliveryAddress.country.name,
            deliveryAddress.postalCode,
            'addressCard.phoneNumber: ' + deliveryAddress.phone
          ]);
        })
        .unsubscribe();

      expect(component.getDeliveryAddressCard).toHaveBeenCalledWith(
        mockOrder.deliveryAddress
      );
    });

    it('should call getDeliveryModeCard(deliveryMode: DeliveryMode)', () => {
      spyOn(component, 'getDeliveryModeCard').and.callThrough();
      const apmOrder = {
        ...mockOrder,
        paymentInfo: { expiryYear: null },
        worldpayAPMPaymentInfo: { name: 'iDeal' }
      };
      component
        .getDeliveryModeCard(apmOrder.deliveryMode)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('checkoutMode.deliveryMethod');
          expect(data.textBold).toEqual('Standard order-detail-shipping');
          expect(data.text).toEqual(['3-5 days', 'test-formatted-cosg']);
        })
        .unsubscribe();

      expect(component.getDeliveryModeCard).toHaveBeenCalledWith(apmOrder.deliveryMode);
    });
  });

  describe('common column in all types of order', () => {
    beforeEach(() => {
      spyOn(translationService, 'translate').and.returnValue(of('test'));
    });

    it('should call getAddressCardContent(deliveryAddress: Address)', () => {
      spyOn(component, 'getAddressCardContent').and.callThrough();

      const deliveryAddress = mockOrder.deliveryAddress;

      component
        .getAddressCardContent(deliveryAddress)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.textBold).toEqual(
            `${deliveryAddress.firstName} ${deliveryAddress.lastName}`
          );
          expect(data.text).toEqual([
            deliveryAddress.formattedAddress,
            deliveryAddress.country.name,
          ]);
        })
        .unsubscribe();

      expect(component.getAddressCardContent).toHaveBeenCalledWith(
        deliveryAddress
      );
    });

    it('should call getDeliveryModeCardContent(deliveryMode: DeliveryMode)', () => {
      spyOn(component, 'getDeliveryModeCardContent').and.callThrough();

      component
        .getDeliveryModeCardContent(mockOrder.deliveryMode)
        .subscribe((data) => {
          expect(data).toBeTruthy();
          expect(data.title).toEqual('test');
          expect(data.textBold).toEqual(mockOrder.deliveryMode.name);
          expect(data.text).toEqual([
            mockOrder.deliveryMode.description,
            mockOrder.deliveryMode.deliveryCost.formattedValue,
          ]);
        })
        .unsubscribe();

      expect(component.getDeliveryModeCardContent).toHaveBeenCalledWith(
        mockOrder.deliveryMode
      );
    });
  });

  describe('normalize formatted address', () => {
    it('should normalize address when line 2 is empty in address', () => {
      const address = component['normalizeFormattedAddress'](
        mockUnformattedAddress
      );

      expect(address).toEqual('test1, test3, test4');
    });

    it('should not change the format when line 2 exist in address', () => {
      const address =
        component['normalizeFormattedAddress'](mockFormattedAddress);

      expect(address).toEqual(mockFormattedAddress);
    });
  });

  describe('isPaymentInfoCardFull', () => {
    it('should return true when card number, expiry month, and expiry year are defined', () => {
      const payment: PaymentDetails = {
        cardNumber: '1234',
        expiryMonth: '12',
        expiryYear: '2026',
      };
      expect(component.isPaymentInfoCardFull(payment)).toBeTrue();
    });

    it('should return false when card number is undefined', () => {
      const payment: PaymentDetails = {
        cardNumber: undefined,
        expiryMonth: '12',
        expiryYear: '2026',
      };
      expect(component.isPaymentInfoCardFull(payment)).toBeFalse();
    });

    it('should return false when expiry month is undefined', () => {
      const payment: PaymentDetails = {
        cardNumber: '1234',
        expiryMonth: undefined,
        expiryYear: '2026',
      };
      expect(component.isPaymentInfoCardFull(payment)).toBeFalse();
    });

    it('should return false when expiry year is undefined', () => {
      const payment: PaymentDetails = {
        cardNumber: '1234',
        expiryMonth: '12',
        expiryYear: undefined,
      };
      expect(component.isPaymentInfoCardFull(payment)).toBeFalse();
    });

    it('should return false when payment is null', () => {
      const payment: PaymentDetails = null;
      expect(component.isPaymentInfoCardFull(payment)).toBeFalse();
    });
  });

  describe('getPaymentDetailsLineTranslation', () => {
    it('should return translation for card expiry date when expiryYear is defined', (doneFn) => {
      const mockOrder = {
        paymentInfo: {
          expiryMonth: '12',
          expiryYear: '2026'
        }
      } as Order;
      spyOn(translationService, 'translate').and.returnValue(of('expires 12/2026'));

      component.getPaymentDetailsLineTranslation(mockOrder).subscribe((translation) => {
        expect(translation).toBe('expires 12/2026');
        doneFn();
      });
    });

    it('should return translation for APM name when expiryYear is not defined but APM name is defined', (doneFn) => {
      const mockOrder = { worldpayAPMPaymentInfo: { name: 'iDeal' } } as Order;
      spyOn(translationService, 'translate').and.returnValue(of('APM iDeal'));

      component.getPaymentDetailsLineTranslation(mockOrder).subscribe((translation) => {
        expect(translation).toBe('APM iDeal');
        doneFn();
      });
    });

    it('should return undefined when neither expiryYear nor APM name is defined', (doneFn) => {
      const mockOrder = {
        paymentInfo: {},
        worldpayAPMPaymentInfo: {}
      } as Order;

      component.getPaymentDetailsLineTranslation(mockOrder).subscribe((translation) => {
        expect(translation).toBeUndefined();
        doneFn();
      });
    });
  });

  describe('getDeliveryModeCard', () => {
    it('should return card with delivery mode details', (doneFn) => {
      const mockDeliveryMode: DeliveryMode = {
        name: 'Standard Shipping',
        description: '3-5 days',
        deliveryCost: {
          formattedValue: '$5.00',
        },
      };
      spyOn(translationService, 'translate').and.returnValue(of('Delivery Method'));

      component.getDeliveryModeCard(mockDeliveryMode).subscribe((card) => {
        expect(card.title).toBe('Delivery Method');
        expect(card.textBold).toBe(mockDeliveryMode.name);
        expect(card.text).toEqual([mockDeliveryMode.description, mockDeliveryMode.deliveryCost.formattedValue]);
        doneFn();
      });
    });
  });

});
