import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { WorldpayGuaranteedPaymentsAdapter } from './worldpay-guaranteed-payments.adapter';
import { take } from 'rxjs/operators';
import { WorldpayGuaranteedPaymentsConnector } from './worldpay-guaranteed-payments.connector';
import createSpy = jasmine.createSpy;

class MockWorldpayFraudsightAdapter implements WorldpayGuaranteedPaymentsAdapter {
  isGuaranteedPaymentsEnabled = createSpy().and.returnValue(
    of(true)
  );

  getSessionId = createSpy().and.returnValue('sessionId');
}

describe('WorldpayGuaranteedPaymentsConnector', () => {
  let service: WorldpayGuaranteedPaymentsConnector;
  let adapter: WorldpayGuaranteedPaymentsAdapter;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: WorldpayGuaranteedPaymentsAdapter,
          useClass: MockWorldpayFraudsightAdapter
        }
      ]
    });
    service = TestBed.inject(WorldpayGuaranteedPaymentsConnector);
    adapter = TestBed.inject(WorldpayGuaranteedPaymentsAdapter);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('isGuaranteedPaymentsEnabled should call adapter', (done) => {
    service.isGuaranteedPaymentsEnabled().pipe(take(1)).subscribe((res) => {
      expect(res).toEqual(true);
      done();
    });
    expect(adapter.isGuaranteedPaymentsEnabled).toHaveBeenCalled();
  });
});
