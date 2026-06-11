import { Pipe, PipeTransform } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ActiveCartFacade, Cart, DeliveryMode, OrderEntry, PaymentType, } from '@spartacus/cart/base/root';
import { CheckoutCostCenterFacade, CheckoutPaymentTypeFacade, } from '@spartacus/checkout/b2b/root';
import { CheckoutStepService } from '@spartacus/checkout/base/components';
import { CheckoutDeliveryAddressFacade, CheckoutDeliveryModesFacade, CheckoutStep, CheckoutStepType, } from '@spartacus/checkout/base/root';
import { Address, CostCenter, Country, I18nTestingModule, PaymentDetails, QueryState, UrlPipe, UserCostCenterService, } from '@spartacus/core';
import { CardComponent, IconTestingModule, OutletModule, PromotionsModule } from '@spartacus/storefront';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { MockActivatedRoute } from 'worldpay-sap-composable-tests';
import { PaymentMethod, WorldpayApmPaymentInfo, WorldpayCheckoutPaymentFacade } from '../../../core';
import { queryDebugElementByCss } from '../../../tests/finders.mock';
import { WorldpayB2BCheckoutReviewSubmitComponent } from './worldpay-b2b-checkout-review-submit.component';
import createSpy = jasmine.createSpy;

const mockCart: Cart = {
  guid: 'test',
  code: 'test',
  deliveryItemsQuantity: 123,
  totalPrice: { formattedValue: '$999.98' },
};
const mockCountry: Country = {
  isocode: 'JP',
  name: 'Japan'
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
const addressBS = new BehaviorSubject<Country>(mockCountry);

const mockDeliveryMode: DeliveryMode = {
  name: 'standard-gross',
  description: 'Delivery mode test description',
};
const deliveryModeBS = new BehaviorSubject<QueryState<DeliveryMode>>({
  loading: false,
  error: false,
  data: mockDeliveryMode,
});

const mockPaymentDetails: PaymentDetails = {
  accountHolderName: 'Name',
  cardNumber: '123456789',
  cardType: {
    code: 'Visa',
    name: 'Visa'
  },
  code: 'Card',
  expiryMonth: '01',
  expiryYear: '2022',
  cvn: '123',
};

const mockEntries: OrderEntry[] = [{ entryNumber: 123 }, { entryNumber: 456 }];

const mockCostCenter: CostCenter = {
  code: 'test-cost-center',
  name: 'test-cc-name',
  unit: { name: 'test-unit-name' },
};

const mockPaymentTypes: PaymentType[] = [
  { code: 'test-account' },
  { code: 'test-card' },
];

class MockCheckoutDeliveryAddressService implements Partial<CheckoutDeliveryAddressFacade> {
  getDeliveryAddressState(): Observable<QueryState<Address | undefined>> {
    return of({
      loading: false,
      error: false,
      data: mockAddress,
    });
  }
}

class MockCheckoutDeliveryModesService implements Partial<CheckoutDeliveryModesFacade> {
  loadSupportedDeliveryModes = createSpy();

  getSelectedDeliveryModeState(): Observable<QueryState<DeliveryMode | undefined>> {
    return deliveryModeBS.asObservable();
  }
}

class MockWorldpayCheckoutPaymentFacade implements Partial<WorldpayCheckoutPaymentFacade> {
  getPaymentDetailsState(): Observable<QueryState<PaymentDetails | undefined>> {
    return of({
      loading: false,
      error: false,
      data: mockPaymentDetails
    });
  }

  paymentProcessSuccess(): void {
  }
}

class MockActiveCartService implements Partial<ActiveCartFacade> {
  getActive(): Observable<Cart> {
    return of(mockCart);
  }

  getEntries(): Observable<OrderEntry[]> {
    return of(mockEntries);
  }
}

const mockPaymentTypeCheckoutStep: CheckoutStep = {
  id: 'step1',
  name: 'step1',
  routeName: 'payment-type',
  type: [CheckoutStepType.PAYMENT_TYPE],
};

const mockDeliveryAddressCheckoutStep: CheckoutStep = {
  id: 'step2',
  name: 'delivery-address',
  routeName: 'delivery-address',
  type: [CheckoutStepType.DELIVERY_ADDRESS],
};

const mockDeliveryModeStepCheckoutStep: CheckoutStep = {
  id: 'step3',
  name: 'delivery-mode',
  routeName: 'delivery-mode',
  type: [CheckoutStepType.DELIVERY_MODE],
};

const mockTypePaymentDetailsCheckoutStep: CheckoutStep = {
  id: 'step4',
  name: 'step4',
  routeName: 'payment-details',
  type: [CheckoutStepType.PAYMENT_DETAILS],
};

const mockReviewOrderStepCheckoutStep: CheckoutStep = {
  id: 'step5',
  name: 'step5',
  routeName: 'review-order',
  type: [CheckoutStepType.REVIEW_ORDER],
};

class MockCheckoutStepService {

  stepsSubject = new BehaviorSubject<CheckoutStep[]>([
    mockPaymentTypeCheckoutStep,
    mockReviewOrderStepCheckoutStep,
  ]);

  steps$ = this.stepsSubject.asObservable();

  getCheckoutStep(): CheckoutStep {
    return mockDeliveryAddressCheckoutStep;
  }

  setSteps(steps: CheckoutStep[]) {
    this.stepsSubject.next(steps);
  }
}

class MockCheckoutPaymentTypeFacade implements Partial<CheckoutPaymentTypeFacade> {
  getPurchaseOrderNumberState(): Observable<QueryState<string | undefined>> {
    return of({
      loading: false,
      error: false,
      data: 'test-po'
    });
  }

  getSelectedPaymentTypeState(): Observable<QueryState<PaymentType | undefined>> {
    return of({
      loading: false,
      error: false,
      data: { code: mockPaymentTypes[0].code },
    });
  }

  isAccountPayment(): Observable<boolean> {
    return of(true);
  }
}

class MockCheckoutCostCenterService implements Partial<CheckoutCostCenterFacade> {
  getCostCenterState(): Observable<QueryState<CostCenter | undefined>> {
    return of({
      loading: false,
      error: false,
      data: mockCostCenter,
    });
  }
}

class MockUserCostCenterService implements Partial<UserCostCenterService> {
  getActiveCostCenters(): Observable<CostCenter[]> {
    return of([mockCostCenter]);
  }
}

@Pipe({ name: 'cxUrl' })
export class MockUrlPipe implements PipeTransform {
  transform(route: any): any {
    if (route?.cxRoute) {
      return `/${route.cxRoute}`;
    }
    return '/';
  }
}

describe('WorldpayB2BCheckoutReviewSubmitComponent', () => {
  let component: WorldpayB2BCheckoutReviewSubmitComponent;
  let fixture: ComponentFixture<WorldpayB2BCheckoutReviewSubmitComponent>;
  let checkoutPaymentFacade: WorldpayCheckoutPaymentFacade;
  let checkoutPaymentTypeFacade: CheckoutPaymentTypeFacade;
  let checkoutStepService: MockCheckoutStepService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        I18nTestingModule,
        PromotionsModule,
        IconTestingModule,
        OutletModule,
        RouterLink,
        CardComponent,
        WorldpayB2BCheckoutReviewSubmitComponent,
      ],
      providers: [
        {
          provide: CheckoutDeliveryAddressFacade,
          useClass: MockCheckoutDeliveryAddressService,
        },
        {
          provide: CheckoutDeliveryModesFacade,
          useClass: MockCheckoutDeliveryModesService,
        },
        {
          provide: WorldpayCheckoutPaymentFacade,
          useClass: MockWorldpayCheckoutPaymentFacade,
        },
        {
          provide: ActiveCartFacade,
          useClass: MockActiveCartService
        },
        {
          provide: CheckoutStepService,
          useClass: MockCheckoutStepService,
        },
        {
          provide: CheckoutPaymentTypeFacade,
          useClass: MockCheckoutPaymentTypeFacade,
        },
        {
          provide: CheckoutCostCenterFacade,
          useClass: MockCheckoutCostCenterService,
        },
        {
          provide: UserCostCenterService,
          useClass: MockUserCostCenterService,
        },
        {
          provide: ActivatedRoute,
          useClass: MockActivatedRoute
        },
      ],
    }).overrideComponent(WorldpayB2BCheckoutReviewSubmitComponent, {
      remove: {
        imports: [ UrlPipe]
      },
      add: {
        imports: [ MockUrlPipe]
      }
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayB2BCheckoutReviewSubmitComponent);
    component = fixture.componentInstance;
    checkoutPaymentFacade = TestBed.inject(WorldpayCheckoutPaymentFacade);
    checkoutPaymentTypeFacade = TestBed.inject(CheckoutPaymentTypeFacade);
    checkoutStepService = TestBed.inject(CheckoutStepService) as unknown as MockCheckoutStepService;

    addressBS.next(mockCountry);
    deliveryModeBS.next({
      loading: false,
      error: false,
      data: mockDeliveryMode,
    });
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should be able to get cart', () => {
    let cart: Cart | undefined;
    component.cart$.subscribe((data: Cart) => {
      cart = data;
    });

    expect(cart).toEqual(mockCart);
  });

  it('should be able to get entries', () => {
    let entries: OrderEntry[] | undefined;
    component.entries$.subscribe((data: OrderEntry[]) => {
      entries = data;
    });

    expect(entries).toEqual(mockEntries);
  });

  it('should be able to get steps', () => {
    let steps: CheckoutStep[] | undefined;
    component.steps$.subscribe((data) => (steps = data));

    expect(steps?.[0]).toEqual({
      id: 'step1',
      name: 'step1',
      routeName: 'payment-type',
      type: [CheckoutStepType.PAYMENT_TYPE],
    });
    expect(steps?.[1]).toEqual({
      id: 'step5',
      name: 'step5',
      routeName: 'review-order',
      type: [CheckoutStepType.REVIEW_ORDER],
    });
  });

  it('should be able to get deliveryAddress', () => {
    let deliveryAddress: Address | undefined;
    component.deliveryAddress$.subscribe((data) => {
      deliveryAddress = data;
    });

    expect(deliveryAddress).toEqual(mockAddress);
  });

  it('should be able to get paymentDetails', () => {
    let paymentDetails: PaymentDetails | undefined;
    component.paymentDetails$.subscribe((data) => {
      paymentDetails = data;
    });

    expect(paymentDetails).toEqual(mockPaymentDetails);
  });

  it('should be able to get deliveryMode if a mode is selected', () => {
    let deliveryMode: DeliveryMode | undefined;
    component.deliveryMode$.subscribe((data) => {
      deliveryMode = data;
    });

    expect(deliveryMode).toEqual(mockDeliveryMode);
  });

  it('should be able to get po number', () => {
    let po: string | undefined;
    component.poNumber$.subscribe((data) => {
      po = data;
    });

    expect(po).toEqual('test-po');
  });

  it('should be able to get cost center', () => {
    let costCenter: CostCenter | undefined;
    component.costCenter$.subscribe((data) => {
      costCenter = data;
    });

    expect(costCenter).toEqual(mockCostCenter);
  });

  it('should get selected payment type', (done) => {
    component.paymentType$.subscribe((data) => {
      expect(data).toEqual(mockPaymentDetails);
      done();
    });
  });

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

  it('should return a payment card with payment details when apmCode is not provided', (done) => {
    const paymentDetails: WorldpayApmPaymentInfo = {
      accountHolderName: 'John Doe',
      cardNumber: '1234',
      expiryMonth: '12',
      expiryYear: '2030',
      apmCode: undefined,
      name: 'Visa',
      billingAddress: mockAddress,
    };

    component.getPaymentMethodCard(paymentDetails).subscribe((card) => {
      expect(card.title).toEqual('paymentForm.payment');
      expect(card.text).toEqual([
        'paymentCard.expires month:12 year:2030',
        'John Doe',
        'Toyosaki 2 create on cart',
        'town, JP-27, JP',
        'zip'
      ]);
      expect(card.textBold).toEqual(undefined);
      done();
    });
  });

  it('should return a billing address card when apmCode is provided', (done) => {
    const paymentDetails = {
      accountHolderName: 'John Doe',
      cardNumber: '1234',
      expiryMonth: '12',
      expiryYear: '2030',
      apmCode: PaymentMethod.PayPal,
      name: 'PayPal',
    };

    component.getPaymentMethodCard(paymentDetails).subscribe((card) => {
      expect(card.title).toEqual('paymentForm.billingAddress');
      expect(card.text).toEqual(['', 'undefined undefined', undefined, 'undefined, undefined', undefined]);
      expect(card.textBold).toEqual(undefined);
      done();
    });
  });

  it('should call getPoNumberCard(po) to get po card data', () => {
    component.getPoNumberCard('test-po').subscribe((card) => {
      expect(card.title).toEqual('checkoutB2B.review.poNumber');
      expect(card.textBold).toEqual('test-po');
    });
  });

  it('should call getCostCenter(costCenter) to get cost center ard data', () => {
    component.getCostCenterCard(mockCostCenter).subscribe((card) => {
      expect(card.title).toEqual('checkoutB2B.costCenter');
      expect(card.textBold).toEqual(mockCostCenter.name);
      expect(card.text).toEqual(['(' + mockCostCenter.unit?.name + ')']);
    });
  });

  it('should call getPaymentTypeCard(paymentType) to get payment type data', () => {
    component.getPaymentTypeCard(mockPaymentTypes[0]).subscribe((card) => {
      expect(card.title).toEqual('checkoutB2B.progress.methodOfPayment');
      expect(card.textBold).toEqual('paymentTypes.paymentType_test-account');
    });
  });

  it('should get checkout step url', () => {
    expect(
      component.getCheckoutStepUrl(CheckoutStepType.DELIVERY_ADDRESS)
    ).toEqual(mockDeliveryAddressCheckoutStep.routeName);
  });

  describe('UI cart total section', () => {
    const getCartTotalText = () =>
      fixture.debugElement.query(By.css('.cx-review-cart-total')).nativeElement
        .textContent;

    it('should contain total number of items', () => {
      fixture.detectChanges();
      expect(getCartTotalText()).toContain(123);
    });

    it('should contain total price', () => {
      fixture.detectChanges();
      expect(getCartTotalText()).toContain('$999.98');
    });

    it('should render purchase order number when available', () => {
      fixture.detectChanges();

      const purchaseOrderNumberCard = queryDebugElementByCss(fixture, '#poNumberCard');
      const title = purchaseOrderNumberCard.query(By.css('.cx-card-title'));
      const cardLabelBold = purchaseOrderNumberCard.query(By.css('.cx-card-label-bold'));
      const link = purchaseOrderNumberCard.query(By.css('.cx-review-summary-edit-step a'));

      expect(purchaseOrderNumberCard).toBeTruthy();
      expect(title.nativeElement.textContent).toContain('checkoutB2B.review.poNumber');
      expect(cardLabelBold.nativeElement.textContent).toContain('test-po');
      expect(link.nativeElement.getAttribute('href')).toContain('/delivery-address');
    });

    it('should render payment type as card when available', () => {
      fixture.detectChanges();

      const paymentTypeCard = queryDebugElementByCss(fixture, '#paymentTypeCard');
      const title = paymentTypeCard.query(By.css('.cx-card-title'));
      const cardLabelBold = paymentTypeCard.query(By.css('.cx-card-label-bold'));
      const link = paymentTypeCard.query(By.css('.cx-review-summary-edit-step a'));

      expect(paymentTypeCard).toBeTruthy();
      expect(title.nativeElement.textContent).toContain('checkoutB2B.progress.methodOfPayment');
      expect(cardLabelBold.nativeElement.textContent).toContain('paymentTypes.paymentType_Card');
      expect(link.nativeElement.getAttribute('href')).toContain('/delivery-address');
    });

    it('should render payment type as APM when available', () => {
      const mockApmDetails: WorldpayApmPaymentInfo = {
        apmCode: PaymentMethod.PayPal,
        apmName: 'ACH Direct Debit'
      };

      spyOn(checkoutPaymentFacade, 'getPaymentDetailsState').and.returnValue(of({
        data: mockApmDetails,
        loading: false,
        error: false,
      }));

      fixture.detectChanges();

      const paymentTypeCard = queryDebugElementByCss(fixture, '#paymentTypeCard');
      const title = paymentTypeCard.query(By.css('.cx-card-title'));
      const cardLabelBold = paymentTypeCard.query(By.css('.cx-card-label-bold'));
      const link = paymentTypeCard.query(By.css('.cx-review-summary-edit-step a'));

      expect(paymentTypeCard).toBeTruthy();
      expect(title.nativeElement.textContent).toContain('checkoutB2B.progress.methodOfPayment');
      expect(cardLabelBold.nativeElement.textContent).toContain(mockApmDetails.apmName);
      expect(link.nativeElement.getAttribute('href')).toContain('/delivery-address');
    });

    it('should render cost center card when available', () => {
      const steps = [
        mockPaymentTypeCheckoutStep,
        mockDeliveryAddressCheckoutStep,
        mockReviewOrderStepCheckoutStep
      ];
      checkoutStepService.setSteps(steps);
      spyOn(checkoutPaymentTypeFacade, 'isAccountPayment').and.returnValue(of(true));
      fixture.detectChanges();

      const costCenterCard = queryDebugElementByCss(fixture, '#costCenterCard');
      const title = costCenterCard.query(By.css('.cx-card-title'));
      const cardLabelBold = costCenterCard.query(By.css('.cx-card-label-bold'));
      const link = costCenterCard.query(By.css('.cx-review-summary-edit-step a'));

      expect(costCenterCard).toBeTruthy();
      expect(title.nativeElement.textContent).toContain('checkoutB2B.costCenter');
      expect(cardLabelBold.nativeElement.textContent).toContain('test-cc-name');
      expect(link.nativeElement.getAttribute('href')).toContain('/delivery-address');
    });

    it('should render delivery address card when available', () => {
      const steps = [
        mockPaymentTypeCheckoutStep,
        mockDeliveryAddressCheckoutStep,
        mockReviewOrderStepCheckoutStep
      ];
      checkoutStepService.setSteps(steps);
      fixture.detectChanges();

      const deliveryAddressCard = queryDebugElementByCss(fixture, '#deliveryAddressCard');
      const title = deliveryAddressCard.query(By.css('.cx-card-title'));
      const cardLabelBold = deliveryAddressCard.query(By.css('.cx-card-label-bold'));
      const link = deliveryAddressCard.query(By.css('.cx-review-summary-edit-step a'));
      const cardLabels = deliveryAddressCard.queryAll(By.css('.cx-card-label'));
      const line1 = cardLabels[0];
      const line2 = cardLabels[1];
      const line3 = cardLabels[2];
      const line4 = cardLabels[3];

      expect(deliveryAddressCard).toBeTruthy();
      expect(title.nativeElement.textContent).toContain('addressCard.shipTo');
      expect(cardLabelBold.nativeElement.textContent).toContain(`${mockAddress.firstName} ${mockAddress.lastName}`);
      expect(link.nativeElement.getAttribute('href')).toContain('/delivery-address');
      expect(line1.nativeElement.textContent).toContain(mockAddress.line1);
      expect(line2.nativeElement.textContent).toContain(mockAddress.line2);
      expect(line3.nativeElement.textContent).toContain(`${mockAddress.town}, ${mockAddress.region?.isocode}, ${mockAddress.country?.name}`);
      expect(line4.nativeElement.textContent).toContain(mockAddress.postalCode);
      expect(link.nativeElement.getAttribute('href')).toContain('/delivery-address');
    });

    it('should render delivery mode card when available', () => {
      spyOn(checkoutStepService, 'getCheckoutStep').and.returnValue(mockDeliveryModeStepCheckoutStep);
      const steps = [
        mockPaymentTypeCheckoutStep,
        mockDeliveryModeStepCheckoutStep,
        mockReviewOrderStepCheckoutStep,
      ];
      checkoutStepService.setSteps(steps);
      fixture.detectChanges();

      const deliveryModeCard = queryDebugElementByCss(fixture, '#deliveryModeCard');
      const title = deliveryModeCard.query(By.css('.cx-card-title'));
      const cardLabelBold = deliveryModeCard.query(By.css('.cx-card-label-bold'));
      const cardLabels = deliveryModeCard.queryAll(By.css('.cx-card-label'));
      const line1 = cardLabels[0];
      const line2 = cardLabels[1];
      const link = deliveryModeCard.query(By.css('.cx-review-summary-edit-step a'));

      expect(deliveryModeCard).toBeTruthy();
      expect(title.nativeElement.textContent).toContain('checkoutMode.deliveryMethod');
      expect(cardLabelBold.nativeElement.textContent).toContain(mockDeliveryMode.name);
      expect(line1.nativeElement.textContent).toContain(mockDeliveryMode.description);
      expect(line2.nativeElement.textContent).toContain('');
      expect(link.nativeElement.getAttribute('href')).toContain('/delivery-mode');
    });

    it('should render payment method card when available', () => {
      spyOn(checkoutStepService, 'getCheckoutStep').and.returnValue(mockTypePaymentDetailsCheckoutStep);
      const steps = [
        mockPaymentTypeCheckoutStep,
        mockDeliveryAddressCheckoutStep,
        mockTypePaymentDetailsCheckoutStep,
        mockDeliveryModeStepCheckoutStep,
        mockReviewOrderStepCheckoutStep,
      ];
      checkoutStepService.setSteps(steps);
      fixture.detectChanges();

      const deliveryModeCard = queryDebugElementByCss(fixture, '#paymentDetailsCard');
      const title = deliveryModeCard.query(By.css('.cx-card-title'));
      const cardLabelBold = deliveryModeCard.query(By.css('.cx-card-label-bold'));
      const cardLabels = deliveryModeCard.queryAll(By.css('.cx-card-label'));
      const line1 = cardLabels[0];
      const line2 = cardLabels[1];
      const link = deliveryModeCard.query(By.css('.cx-review-summary-edit-step a'));

      expect(deliveryModeCard).toBeTruthy();
      expect(title.nativeElement.textContent).toContain('paymentForm.payment');
      expect(cardLabelBold).toBeNull();
      expect(line1.nativeElement.textContent).toContain('paymentCard.expires month:01 year:2022');
      expect(line2.nativeElement.textContent).toContain('undefined undefined');
      expect(link.nativeElement.getAttribute('href')).toContain('/payment-details');
    });
  });

  describe('getPaymentDetailsLineTranslation', () => {
    it('should return payment card expires translation when expiryYear is provided', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        expiryMonth: '12',
        expiryYear: '2030',
        name: 'Visa',
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe((translation) => {
        expect(translation).toEqual('paymentCard.expires month:12 year:2030');
        done();
      });
    });

    it('should return payment card apm translation when expiryYear is not provided', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        name: 'PayPal',
        apmCode: PaymentMethod.PayPal,
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe((translation) => {
        expect(translation).toEqual('paymentCard.apm apm:PayPal');
        done();
      });
    });

    it('should use expiryMonth and expiryYear when expiryYear exists', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        expiryMonth: '01',
        expiryYear: '2025',
        name: 'MasterCard',
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe((translation) => {
        expect(translation).toEqual('paymentCard.expires month:01 year:2025');
        done();
      });
    });

    it('should use payment name for apm translation when expiryYear is not provided', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        name: 'Google Pay',
        apmCode: PaymentMethod.GooglePay,
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe((translation) => {
        expect(translation).toEqual('paymentCard.apm apm:Google Pay');
        done();
      });
    });

    it('should handle null expiryYear', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        expiryMonth: '12',
        expiryYear: null,
        name: 'ACH',
        apmCode: PaymentMethod.ACH,
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe((translation) => {
        expect(translation).toEqual('paymentCard.apm apm:ACH');
        done();
      });
    });

    it('should handle undefined expiryYear', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        expiryMonth: '06',
        expiryYear: undefined,
        name: 'Apple Pay',
        apmCode: PaymentMethod.ApplePay,
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe((translation) => {
        expect(translation).toEqual('paymentCard.apm apm:Apple Pay');
        done();
      });
    });

    it('should handle empty string expiryYear', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        expiryMonth: '03',
        expiryYear: '',
        name: 'Directdebit',
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe((translation) => {
        expect(translation).toEqual('paymentCard.apm apm:Directdebit');
        done();
      });
    });

    it('should pass correct parameters to translate service for payment card', (done) => {
      spyOn(component['translationService'], 'translate').and.callThrough();

      const paymentDetails: WorldpayApmPaymentInfo = {
        expiryMonth: '05',
        expiryYear: '2028',
        name: 'Amex',
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe(() => {
        expect(component['translationService'].translate).toHaveBeenCalledWith(
          'paymentCard.expires',
          {
            month: '05',
            year: '2028',
          }
        );
        done();
      });
    });

    it('should pass correct parameters to translate service for apm', (done) => {
      spyOn(component['translationService'], 'translate').and.callThrough();

      const paymentDetails: WorldpayApmPaymentInfo = {
        name: 'SEPA',
        apmCode: PaymentMethod.SEPA,
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe(() => {
        expect(component['translationService'].translate).toHaveBeenCalledWith(
          'paymentCard.apm',
          { apm: 'SEPA' }
        );
        done();
      });
    });

    it('should return observable for payment card expires', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        expiryMonth: '11',
        expiryYear: '2027',
        name: 'Visa',
      };

      const result = component.getPaymentDetailsLineTranslation(paymentDetails);

      expect(result instanceof Observable).toBeTrue();
      result.subscribe((translation) => {
        expect(typeof translation).toBe('string');
        done();
      });
    });

    it('should return observable for payment card apm', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        name: 'iDEAL',
        apmCode: PaymentMethod.iDeal,
      };

      const result = component.getPaymentDetailsLineTranslation(paymentDetails);

      expect(result instanceof Observable).toBeTrue();
      result.subscribe((translation) => {
        expect(typeof translation).toBe('string');
        done();
      });
    });

    it('should handle zero month and year values', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        expiryMonth: '00',
        expiryYear: '0000',
        name: 'Test Card',
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe((translation) => {
        expect(translation).toEqual('paymentCard.expires month:00 year:0000');
        done();
      });
    });

    it('should handle special characters in payment name', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        name: 'PayPal (Test Account)',
        apmCode: PaymentMethod.PayPal,
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe((translation) => {
        expect(translation).toEqual('paymentCard.apm apm:PayPal (Test Account)');
        done();
      });
    });

    it('should prioritize expiryYear check over apmCode when both are present', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        expiryMonth: '08',
        expiryYear: '2026',
        name: 'Credit Card',
        apmCode: PaymentMethod.PayPal,
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe((translation) => {
        expect(translation).toEqual('paymentCard.expires month:08 year:2026');
        done();
      });
    });

    it('should handle very long payment names', (done) => {
      const longName = 'A'.repeat(100);
      const paymentDetails: WorldpayApmPaymentInfo = {
        name: longName,
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe((translation) => {
        expect(translation).toContain(longName);
        done();
      });
    });

    it('should handle numeric string months and years', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        expiryMonth: '02',
        expiryYear: '2099',
        name: 'Visa',
      };

      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe((translation) => {
        expect(translation).toEqual('paymentCard.expires month:02 year:2099');
        done();
      });
    });

    it('should complete observable after emitting translation for card', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        expiryMonth: '04',
        expiryYear: '2024',
        name: 'Mastercard',
      };

      let emitted = false;
      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe({
        next: () => {
          emitted = true;
        },
        complete: () => {
          expect(emitted).toBeTrue();
          done();
        }
      });
    });

    it('should complete observable after emitting translation for apm', (done) => {
      const paymentDetails: WorldpayApmPaymentInfo = {
        name: 'Alipay',
      };

      let emitted = false;
      component.getPaymentDetailsLineTranslation(paymentDetails).subscribe({
        next: () => {
          emitted = true;
        },
        complete: () => {
          expect(emitted).toBeTrue();
          done();
        }
      });
    });
  });
});
