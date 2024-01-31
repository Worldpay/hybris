import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { WorldpayFraudsightAdapter } from './worldpay-fraudsight.adapter';
import { take } from 'rxjs/operators';
import { WorldpayFraudsightConnector } from './worldpay-fraudsight.connector';
import createSpy = jasmine.createSpy;

class MockWorldpayFraudsightAdapter implements WorldpayFraudsightAdapter {
  isFraudSightEnabled = createSpy().and.returnValue(
    of(true)
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
});
