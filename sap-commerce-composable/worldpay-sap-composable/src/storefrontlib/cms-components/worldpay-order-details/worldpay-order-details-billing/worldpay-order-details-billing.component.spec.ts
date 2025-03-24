import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { I18nTestingModule, PaymentDetails, TranslationService } from '@spartacus/core';
import { OrderDetailsService } from '@spartacus/order/components';
import { Order } from '@spartacus/order/root';
import { Observable, of } from 'rxjs';
import { WorldpayOrderDetailsBillingComponent } from './worldpay-order-details-billing.component';

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

const mockPaymentDetails2 = {
  ...mockPaymentDetails,
  billingAddress: {
    ...mockPaymentDetails.billingAddress,
    line2: 'line2'
  }
};
const mockOrder: Order = {
  code: '1',
  statusDisplay: 'Shipped',
  paymentInfo: mockPaymentDetails,
};

class MockOrderDetailsService {
  getOrderDetails() {
    return of(mockOrder);
  }
}

class MockTranslationService {
  translate(text: string): Observable<string> {
    return of(text);
  }
}

describe('OrderDetailsBillingComponent', () => {
  let component: WorldpayOrderDetailsBillingComponent;
  let fixture: ComponentFixture<WorldpayOrderDetailsBillingComponent>;
  let translationService: TranslationService;
  let orderDetailsService: OrderDetailsService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [I18nTestingModule],
        declarations: [
          WorldpayOrderDetailsBillingComponent
        ],
        providers: [
          {
            provide: OrderDetailsService,
            useClass: MockOrderDetailsService,
          },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldpayOrderDetailsBillingComponent);
    component = fixture.componentInstance;
    translationService = TestBed.inject(TranslationService);
    orderDetailsService = TestBed.inject(OrderDetailsService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getPaymentMethodCard to get payment card data', () => {
    component.getPaymentMethodCard(mockOrder).subscribe((card) => {
      expect(card.title).toEqual('paymentForm.payment');
      expect(card.textBold).toEqual(mockPaymentDetails.accountHolderName);
      expect(card.text).toEqual([
        mockPaymentDetails.cardNumber,
        `paymentCard.expires month:${mockPaymentDetails.expiryMonth} year:${mockPaymentDetails.expiryYear}`,
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
    component.getBillingAddressCard(mockPaymentDetails2).subscribe((card) => {
      expect(card.title).toEqual('paymentForm.billingAddress');
      expect(card.text).toEqual([
        'addressCard.billTo',
        mockPaymentDetails2.billingAddress?.firstName + ' ' + mockPaymentDetails2.billingAddress?.lastName,
        mockPaymentDetails2.billingAddress?.line1,
        mockPaymentDetails2.billingAddress?.line2,
        mockPaymentDetails2.billingAddress?.town + ', ' + mockPaymentDetails2.billingAddress?.region?.isocode + ', ' + mockPaymentDetails2.billingAddress?.country?.isocode,
        mockPaymentDetails2.billingAddress?.postalCode,
      ]);
    });
  });

  it('should return APM payment details translation when APM name is provided', () => {
    const mockOrder = {
      worldpayAPMPaymentInfo: {
        name: 'PayPal'
      }
    };
    spyOn(translationService, 'translate').and.returnValue(of('paymentCard.apm'));

    component.getPaymentDetailsLineTranslation(mockOrder).subscribe((translation) => {
      expect(translation).toEqual('paymentCard.apm');
      expect(translationService.translate).toHaveBeenCalledWith('paymentCard.apm', { apm: 'PayPal' });
    });
  });

  it('should return undefined when neither expiry year nor APM name is provided', () => {
    const mockOrder = {
      paymentInfo: {}
    };
    spyOn(translationService, 'translate');

    component.getPaymentDetailsLineTranslation(mockOrder).subscribe((translation) => {
      expect(translation).toBeUndefined();
      expect(translationService.translate).not.toHaveBeenCalled();
    });
  });
});
