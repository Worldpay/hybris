import { TestBed } from '@angular/core/testing';

import { WorldpayFraudsightService } from './worldpay-fraudsight.service';
import { QueryService } from '@spartacus/core';
import { of } from 'rxjs';
import { WorldpayFraudsightConnector } from '../../connectors/worldpay-fraudsight/worldpay-fraudsight.connector';
import createSpy = jasmine.createSpy;

class MockWorldpayFraudsightConnector implements Partial<WorldpayFraudsightConnector> {
  isFraudSightEnabled = createSpy().and.returnValue(of(true));
}

describe('WorldpayFraudsightService', () => {
  let service: WorldpayFraudsightService;
  let worldpayFraudsightConnector: WorldpayFraudsightConnector;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        QueryService,
        {
          provide: WorldpayFraudsightConnector,
          useClass: MockWorldpayFraudsightConnector,
        }
      ],
    });
    service = TestBed.inject(WorldpayFraudsightService);
    worldpayFraudsightConnector = TestBed.inject(WorldpayFraudsightConnector);
    spyOn(service, 'setFraudSightId').and.callThrough();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch isFraudSightEnabled query', () => {
    service.isFraudSightEnabled();

    expect(worldpayFraudsightConnector.isFraudSightEnabled).toHaveBeenCalled();

    service.isFraudSightEnabled().subscribe((response) => {
      expect(response.data).toBeTrue();
    });

  });

  it('should call getFraudSightIdFromState function', () => {
    const id = 'elelgegorejigroe';
    service.setFraudSightId(id);

    service.getFraudSightIdFromState().subscribe((response) => {
      expect(response).toEqual(id);
    });

  });

  it('should call setFraudSightId function', () => {
    service.setFraudSightId('elelgegorejigroe');
    expect(service.setFraudSightId).toHaveBeenCalledWith('elelgegorejigroe');

    service.getFraudSightIdFromState().subscribe((response) => {
      expect(response).toEqual('elelgegorejigroe');
    });
  });

  it('should call isFraudSightEnabledFromState function', () => {
    const id = 'elelgegorejigroe';
    service.setFraudSightId(id);

    expect(service.setFraudSightId).toHaveBeenCalledWith(id);
  });

  it('should call setFraudSightEnabled function', () => {
    let enabled = false;
    service.setFraudSightEnabled({
      error: false,
      loading: false,
      data: true
    });

    service.isFraudSightEnabledFromState().subscribe((response) => {
      enabled = response;
    });
    expect(enabled).toBeTrue();
  });
});
