import { TestBed } from '@angular/core/testing';

import { WorldpayFraudsightService } from './worldpay-fraudsight.service';
import { Store, StoreModule } from '@ngrx/store';
import { GetFraudSightEnabled, SetFraudSightId } from '../../store/worldpay.action';

describe('WorldpayFraudsightService', () => {
  let service: WorldpayFraudsightService;
  let worldpayStore: Store;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StoreModule.forRoot({})],
    });
    service = TestBed.inject(WorldpayFraudsightService);

    worldpayStore = TestBed.inject(Store);
    spyOn(worldpayStore, 'dispatch').and.callThrough();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should dispatch SetFraudSightId event', () => {
    const id = 'elelgegorejigroe';
    service.setFraudSightId(id);

    expect(worldpayStore.dispatch).toHaveBeenCalledWith(
      new SetFraudSightId(id)
    );
  });

  it('should dispatch GetFraudSightEnabled event', () => {
    service.isFraudSightEnabled();

    expect(worldpayStore.dispatch).toHaveBeenCalledWith(
      new GetFraudSightEnabled()
    );
  });
});
