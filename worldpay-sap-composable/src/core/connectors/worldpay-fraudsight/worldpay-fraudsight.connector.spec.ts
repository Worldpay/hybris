import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { take } from 'rxjs/operators';
import { WorldpayFraudsightAdapter } from './worldpay-fraudsight.adapter';
import { WorldpayFraudsightConnector } from './worldpay-fraudsight.connector';
import createSpy = jasmine.createSpy;

class MockWorldpayFraudsightAdapter implements WorldpayFraudsightAdapter {
  isFraudSightEnabled = createSpy().and.returnValue(
    of(true)
  );
  getFraudPublicKey = createSpy().and.returnValue(
    of('publishable_key_test_xxx')
  );
}

describe('WorldpayFraudsightConnector', () => {
  let service: WorldpayFraudsightConnector;
  let adapter: WorldpayFraudsightAdapter;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: WorldpayFraudsightAdapter,
          useClass: MockWorldpayFraudsightAdapter
        }
      ]
    });
    service = TestBed.inject(WorldpayFraudsightConnector);
    adapter = TestBed.inject(WorldpayFraudsightAdapter);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('isFraudSightEnabled should call adapter', (done) => {
    service.isFraudSightEnabled().pipe(take(1)).subscribe((res) => {
      expect(res).toEqual(true);
      done();
    });
    expect(adapter.isFraudSightEnabled).toHaveBeenCalledWith();
  });

  it('getFraudPublicKey should call adapter', (done) => {
    service.getFraudPublicKey().pipe(take(1)).subscribe((res) => {
      expect(res).toEqual('publishable_key_test_xxx');
      done();
    });
    expect(adapter.getFraudPublicKey).toHaveBeenCalledWith();
  });
});
