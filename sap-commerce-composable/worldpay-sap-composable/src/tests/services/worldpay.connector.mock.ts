import { Order } from '@spartacus/order/root';
import { Observable, of } from 'rxjs';
import { mockOrder } from 'worldpay-sap-composable-tests';
import { APMRedirectResponse, ThreeDsDDCInfo, WorldpayConnector } from '../../core';
import createSpy = jasmine.createSpy;

const order: Order = mockOrder;

export class MockWorldpayConnector implements Partial<WorldpayConnector> {
  initialPaymentRequest = createSpy('initialPaymentRequest').and.returnValue(of({
    threeDSecureNeeded: false,
    threeDSecureInfo: 'info',
    transactionStatus: 'AUTHORISED',
    order: {
      code: '0001'
    }
  }));
  getOrder = createSpy('WorldpayAdapter.getOrder').and.callFake(() => of(order));

  getDDC3dsJwt(): Observable<ThreeDsDDCInfo> {
    return of({});
  }

  authoriseApmRedirect(): Observable<APMRedirectResponse> {
    return of({
      postUrl: 'https://test.com',
      mappingLabels: {},
      parameters: {
        entry: []
      }
    });
  }
}