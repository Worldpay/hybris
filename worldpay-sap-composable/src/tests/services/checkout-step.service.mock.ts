import createSpy = jasmine.createSpy;
import { CheckoutStepService } from '@spartacus/checkout/base/components';
import { CheckoutStep, CheckoutStepType } from '@spartacus/checkout/base/root';

const mockCheckoutStep: CheckoutStep = {
  id: 'step',
  name: 'name',
  routeName: '/route',
  type: [CheckoutStepType.PAYMENT_DETAILS],
};

export class MockCheckoutStepService implements Partial<CheckoutStepService> {
  next = createSpy();
  back = createSpy();
  getCheckoutStepRoute = createSpy().and.returnValue(
    mockCheckoutStep.routeName
  );

  getBackBntText(): string {
    return 'common.back';
  }

  getCheckoutStep(currentStepType: CheckoutStepType): CheckoutStep | undefined {
    return mockCheckoutStep;
  };

}
