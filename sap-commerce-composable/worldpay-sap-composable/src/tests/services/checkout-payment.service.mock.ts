import { CheckoutPaymentFacade } from '@spartacus/checkout/base/root';
import { EMPTY, of } from 'rxjs';
import createSpy = jasmine.createSpy;

export class MockCheckoutPaymentFacade implements Partial<CheckoutPaymentFacade> {
  loadSupportedCardTypes = createSpy();
  getPaymentCardTypes = createSpy().and.returnValue(EMPTY);
  getSetPaymentDetailsResultProcess = createSpy().and.returnValue(
    of({ loading: false })
  );
}

export class MockCheckoutPaymentService extends MockCheckoutPaymentFacade {
  constructor() {
    super();
  }
}