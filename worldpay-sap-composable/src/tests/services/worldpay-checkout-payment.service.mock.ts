import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Address, PaymentDetails } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { ThreeDsDDCInfo, ThreeDsInfo, WorldpayCheckoutPaymentFacade } from '../../core';

const mockBillingAddress: Address = {
  formattedAddress: 'address',
  id: '0001',
};
const mockCreditCard: PaymentDetails = {
  accountHolderName: 'user',
  billingAddress: mockBillingAddress,
  cardNumber: '1111222233334444',
  cardType: {
    code: 'visa',
    name: 'Visa'
  },
  cvn: '123',
  defaultPayment: false,
  expiryMonth: '12',
  expiryYear: '24',
  id: '0001',
  subscriptionId: '000000000',
};

export class MockWorldpayCheckoutPaymentService implements Partial<WorldpayCheckoutPaymentFacade> {
  constructor(private sanitizer: DomSanitizer) {
  }

  getPaymentDetailsState(): Observable<any> {
    return of({
      loading: false,
      error: false,
      data: mockCreditCard
    });
  }

  getCseTokenFromState(): Observable<string> {
    return of('token');
  }

  getThreeDsDDCIframeUrlFromState(): Observable<SafeResourceUrl> {
    return of(this.sanitizer.bypassSecurityTrustResourceUrl('iframeUrl'));
  }

  getThreeDsChallengeIframeUrlFromState(): Observable<SafeResourceUrl> {
    return of(this.sanitizer.bypassSecurityTrustResourceUrl('https://centinelapistag.cardinalcommerce.com'));
  }

  getDDCInfoFromState(): Observable<ThreeDsDDCInfo> {
    return of({
      ddcUrl: 'https://centinelapistag.cardinalcommerce.com',
      jwt: 'jwt'
    });
  }

  getThreeDsChallengeInfoFromState(): Observable<ThreeDsInfo> {
    return of({
      threeDSFlexData: {
        autoSubmitThreeDSecureFlexUrl: 'https://autosubmiturl.aws.e2y.io',
        jwt: 'jwt',
        challengeUrl: 'https://challengeurl.aws.e2y.io',
        entry: []
      },
      merchantData: '123-456'
    });
  }

  setThreeDsChallengeIframeUrl(): void {
  }

  setThreeDsDDCIframeUrl(): void {
  }

  listenSetThreeDsDDCInfoEvent(): void {
  }

  setSaveCreditCardValue(): void {
  }

  setSaveAsDefaultCardValue(): void {
  }
}