import createSpy = jasmine.createSpy;
import { Order } from '@spartacus/order/root';
import { Observable, of } from 'rxjs';
import { APMRedirectResponse, PlaceOrderResponse, ThreeDsDDCInfo, WorldpayOrderService } from '../../core';

export class MockWorldpayOrderService implements Partial<WorldpayOrderService> {
  placeOrder = createSpy().and.returnValue(of({}));

  clearOrder = createSpy();

  getOrderDetails(): Observable<Order | undefined> {
    return of({
      code: 'order-0001',
      entries: []
    });
  }

  getLoading(): Observable<boolean> {
    return of(false);
  }

  startLoading(): void {

  }

  getAPMRedirectUrl(): Observable<APMRedirectResponse> {
    return of({
      postUrl: 'https://postURL.com',
      parameters: {
        entry: []
      },
      mappingLabels: {}
    });
  }

  clearLoading(): void {
  }

  executeDDC3dsJwtCommand(): Observable<ThreeDsDDCInfo> {
    return of({
      ddcUrl: 'https://centinelapistag.cardinalcommerce.com',
      jwt: 'jwt'
    });
  }

  initialPaymentRequest(): Observable<PlaceOrderResponse> {
    return of({
      threeDSecureNeeded: false,
      transactionStatus: 'SUCCESS',
      order: {
        code: '00001'
      }
    });
  }

  setPlacedOrder(): void {
  }

  placeACHOrder(): Observable<Order> {
    return of(null);
  }

  challengeAccepted(): void {

  }

  challengeFailed(): void {
  }
}