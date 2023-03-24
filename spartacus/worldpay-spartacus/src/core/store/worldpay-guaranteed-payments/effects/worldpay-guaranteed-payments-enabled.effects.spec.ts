import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable, of } from 'rxjs';

import { WorldpayGuaranteedPaymentsEnabledEffects } from './worldpay-guaranteed-payments-enabled.effects';
import { WorldpayConnector } from '../../../connectors/worldpay.connector';

class MockWorldpayConnector {
  isGuaranteedPaymentsEnabled() {
    return of(true);
  }
}

describe('WorldpayGuaranteedPaymentsEffects', () => {
  let actions$: Observable<any>;
  let effects: WorldpayGuaranteedPaymentsEnabledEffects;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        WorldpayGuaranteedPaymentsEnabledEffects,
        provideMockActions(() => actions$),
        {
          provide: WorldpayConnector,
          useClass: MockWorldpayConnector,
        },
      ],
    });

    effects = TestBed.inject(WorldpayGuaranteedPaymentsEnabledEffects);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });
});
