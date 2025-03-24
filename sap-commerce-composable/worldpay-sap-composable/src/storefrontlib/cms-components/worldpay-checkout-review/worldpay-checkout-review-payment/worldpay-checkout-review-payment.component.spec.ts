import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { CheckoutStepService } from '@spartacus/checkout/base/components';
import { CheckoutStep, CheckoutStepType } from '@spartacus/checkout/base/root';
import { I18nTestingModule, PaymentDetails, TranslationService } from '@spartacus/core';
import { IconTestingModule } from '@spartacus/storefront';
import { WorldpayCheckoutPaymentFacade } from '@worldpay-facade/worldpay-checkout-payment.facade';
import { MockCxCardComponent } from '@worldpay-tests/components';
import { MockUrlPipe } from '@worldpay-tests/pipes';
import { of } from 'rxjs';
import { WorldpayApmPaymentInfo } from '../../../../core/interfaces';
import { WorldpayCheckoutReviewPaymentComponent } from './worldpay-checkout-review-payment.component';
import createSpy = jasmine.createSpy;

const mockPaymentDetails: PaymentDetails = {
  accountHolderName: 'Name',
  cardNumber: '123456789',
  cardType: {
    code: 'Visa',
    name: 'Visa'
  },
  expiryMonth: '01',
  expiryYear: '2022',
  cvn: '123',
  billingAddress: {
    firstName: 'John',
    lastName: 'Smith',
    line1: '2343 test address',
    town: 'Montreal',
    region: {
      isocode: 'QC',
    },
    country: {
      isocode: 'CAN',
    },
    postalCode: 'H2N 1E3',
  },
};

const mockPaymentDetailsWithLine2 = {
  ...mockPaymentDetails,
  billingAddress: {
    ...mockPaymentDetails.billingAddress,
    line2: 'line2'
  }
};

const mockCheckoutStep: CheckoutStep = {
  id: 'step',
  name: 'name',
  routeName: '/route',
  type: [CheckoutStepType.PAYMENT_DETAILS],
};

class MockCheckoutPaymentService implements Partial<WorldpayCheckoutPaymentFacade> {
  getPaymentDetailsState = createSpy().and.returnValue(
    of({
      loading: false,
      error: false,
      data: mockPaymentDetails
    })
  );
}

class MockCheckoutStepService {
  steps$ = of([
    {
      id: 'step1',
      name: 'step1',
      routeName: 'route1',
      type: [CheckoutStepType.PAYMENT_DETAILS],
    },
  ]);
  getCheckoutStepRoute = createSpy().and.returnValue(
    mockCheckoutStep.routeName
  );
}

describe('WorldpayCheckoutReviewPaymentComponent', () => {
  let component: WorldpayCheckoutReviewPaymentComponent;
  let fixture: ComponentFixture<WorldpayCheckoutReviewPaymentComponent>;
  let translationService: TranslationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [I18nTestingModule, RouterTestingModule, IconTestingModule],
      declarations: [
        WorldpayCheckoutReviewPaymentComponent,
        MockUrlPipe,
        MockCxCardComponent,
      ],
      providers: [
        {
          provide: WorldpayCheckoutPaymentFacade,
          useClass: MockCheckoutPaymentService,
        },
        {
          provide: CheckoutStepService,
          useClass: MockCheckoutStepService,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(WorldpayCheckoutReviewPaymentComponent);
    component = fixture.componentInstance;
    translationService = TestBed.inject(TranslationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be able to get paymentDetails', () => {
    let paymentDetails: PaymentDetails | undefined;
    component.paymentDetails$.subscribe((data) => {
      paymentDetails = data;
    });

    expect(paymentDetails).toEqual(mockPaymentDetails);
  });

  it('should call getPaymentMethodCard(paymentDetails) to get payment card data', () => {
    component.getPaymentMethodCard(mockPaymentDetails).subscribe((card) => {
      expect(card.title).toEqual('paymentForm.payment');
      expect(card.text).toEqual([
        mockPaymentDetails.cardType?.name,
        mockPaymentDetails.accountHolderName,
        mockPaymentDetails.cardNumber,
        `paymentCard.expires month:${mockPaymentDetails.expiryMonth} year:${mockPaymentDetails.expiryYear}`,
      ]);
    });
  });

  it('should call getPaymentMethodCard(paymentDetails) to get APM card data', () => {
    const apmPaymentDetails = {
      ...mockPaymentDetails,
      worldpayAPMPaymentInfo: { name: 'Paypal' }
    };
    component.getPaymentMethodCard(mockPaymentDetails).subscribe((card) => {
      expect(card.title).toEqual('paymentForm.payment');
      expect(card.text).toEqual([
        apmPaymentDetails.cardType?.name,
        apmPaymentDetails.accountHolderName,
        apmPaymentDetails.cardNumber,
        `paymentCard.expires month:${apmPaymentDetails.expiryMonth} year:${apmPaymentDetails.expiryYear}`,
      ]);
    });
  });

  it('should call getBillingAddressCard to get billing address card data', () => {
    component.getBillingAddressCard(mockPaymentDetails).subscribe((card) => {
      expect(card.title).toEqual('paymentForm.billingAddress');
      expect(card.text).toEqual([
        'addressCard.billTo',
        mockPaymentDetails.billingAddress?.firstName + ' ' + mockPaymentDetails.billingAddress?.lastName,
        mockPaymentDetails.billingAddress?.line1,
        mockPaymentDetails.billingAddress?.town + ', ' + mockPaymentDetails.billingAddress?.region?.isocode + ', ' + mockPaymentDetails.billingAddress?.country?.isocode,
        mockPaymentDetails.billingAddress?.postalCode,
      ]);
    });
  });

  it('should call getBillingAddressCard to get billing address card data with line2', () => {
    component.getBillingAddressCard(mockPaymentDetailsWithLine2).subscribe((card) => {
      expect(card.title).toEqual('paymentForm.billingAddress');
      expect(card.text).toEqual([
        'addressCard.billTo',
        mockPaymentDetailsWithLine2.billingAddress?.firstName + ' ' + mockPaymentDetailsWithLine2.billingAddress?.lastName,
        mockPaymentDetailsWithLine2.billingAddress?.line1,
        mockPaymentDetailsWithLine2.billingAddress?.line2,
        mockPaymentDetailsWithLine2.billingAddress?.town + ', ' + mockPaymentDetailsWithLine2.billingAddress?.region?.isocode + ', ' + mockPaymentDetailsWithLine2.billingAddress?.country?.isocode,
        mockPaymentDetailsWithLine2.billingAddress?.postalCode,
      ]);
    });
  });

  it('should translate payment details line for APM', () => {
    const paymentDetails = { name: 'Paypal' } as WorldpayApmPaymentInfo;
    let translation: string | undefined;
    spyOn(translationService, 'translate').and.returnValue(of('translated APM'));

    component['getPaymentDetailsLineTranslation'](paymentDetails).subscribe((result) => {
      translation = result;
    });

    expect(translation).toEqual('translated APM');
    expect(translationService.translate).toHaveBeenCalledWith('paymentCard.apm', { apm: paymentDetails.name });
  });

  it('should translate payment details line for card with expiry date', () => {
    const paymentDetails = {
      expiryMonth: '12',
      expiryYear: '2023'
    } as WorldpayApmPaymentInfo;
    let translation: string | undefined;
    spyOn(translationService, 'translate').and.returnValue(of('expires month:12 year:2023'));

    component['getPaymentDetailsLineTranslation'](paymentDetails).subscribe((result) => {
      translation = result;
    });

    expect(translation).toEqual('expires month:12 year:2023');
    expect(translationService.translate).toHaveBeenCalledWith('paymentCard.expires', {
      month: paymentDetails.expiryMonth,
      year: paymentDetails.expiryYear
    });
  });

  it('should get checkout step route', () => {
    expect(component.paymentDetailsStepRoute).toEqual(
      mockCheckoutStep.routeName
    );
  });
});
