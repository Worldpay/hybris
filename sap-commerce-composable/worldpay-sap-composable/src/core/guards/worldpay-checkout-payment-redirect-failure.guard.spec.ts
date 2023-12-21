import { TestBed } from '@angular/core/testing';
import { GlobalMessageService, GlobalMessageType } from '@spartacus/core';
import { WorldpayCheckoutPaymentRedirectFailureGuard } from './worldpay-checkout-payment-redirect-failure.guard';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import createSpy = jasmine.createSpy;

const drop = new Subject();

describe('WorldpayCheckoutPaymentRedirectFailureGuard', () => {
  let service: WorldpayCheckoutPaymentRedirectFailureGuard;
  let globalMessageService: GlobalMessageService;

  class MockGlobalMessageService {
    add = createSpy('add').and.callThrough();
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: GlobalMessageService,
          useClass: MockGlobalMessageService
        }
      ]
    });
    service = TestBed.inject(WorldpayCheckoutPaymentRedirectFailureGuard);

    globalMessageService = TestBed.inject(GlobalMessageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should show message when payment status is REFUSED', () => {
    service.canActivate({ queryParams: { reason: 'error' } } as any).pipe(takeUntil(drop)).subscribe(response => response);
    expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'checkoutReview.redirectPaymentFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
  });

  it('should show message when user cancelled', () => {
    service.canActivate({ queryParams: { reason: 'cancel' } } as any).pipe(takeUntil(drop)).subscribe(response => response);
    expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'checkoutReview.redirectPaymentCancelled' }, GlobalMessageType.MSG_TYPE_ERROR,);
  });

  it('should show message there is a failure', () => {
    service.canActivate({ queryParams: { reason: 'failure' } } as any).pipe(takeUntil(drop)).subscribe(response => response);
    expect(globalMessageService.add).toHaveBeenCalledWith({ key: 'checkoutReview.redirectPaymentFailed' }, GlobalMessageType.MSG_TYPE_ERROR);
  });

});
