import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable } from 'rxjs';
import { WorldpayGuaranteedPaymentsSessionIdEffects } from './worldpay-guaranteed-payments-session-id.effects';

describe('WorldpayGuaranteedPaymentsSessionIdEffects', () => {
  let actions$: Observable<any>;
  let effects: WorldpayGuaranteedPaymentsSessionIdEffects;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        WorldpayGuaranteedPaymentsSessionIdEffects,
        provideMockActions(() => actions$)
      ]
    });

    effects = TestBed.inject(WorldpayGuaranteedPaymentsSessionIdEffects);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });
});
