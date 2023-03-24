import { StateWithWorldpay } from '../../worldpay.state';
import { select, Store, StoreModule } from '@ngrx/store';
import { TestBed } from '@angular/core/testing';
import { WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_KEY } from '../worldpay-guaranteed-payments.state';
import { getReducersWorldpayGuaranteedPayments } from '../reducers/worldpay-guaranteed-payments.reducer';
import {
  getWorldpayGuaranteedPaymentsEnabledFail,
  getWorldpayGuaranteedPaymentsEnabledSuccess,
  getWorldpayGuaranteedPaymentsEnabledValue
} from './worldpay-guaranteed-payments-enabled.selectors';
import {
  IsWorldpayGuaranteedPaymentsEnabled,
  IsWorldpayGuaranteedPaymentsEnabledFail,
  IsWorldpayGuaranteedPaymentsEnabledSuccess
} from '../actions/worldpay-guaranteed-payments-enabled.actions';

describe('WorldpayGuaranteedPayments Enabled Selectors', () => {
  let store: Store<StateWithWorldpay>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
        StoreModule.forFeature(WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_KEY, getReducersWorldpayGuaranteedPayments())
      ]
    });

    store = TestBed.inject(Store);
    spyOn(store, 'dispatch').and.callThrough();

  });

  it('getWorldpayGuaranteedPaymentsEnabledValue', () => {
    let result: boolean;
    store.pipe(select(getWorldpayGuaranteedPaymentsEnabledValue))
      .subscribe((value) => {
        result = value;
        return result;
      });

    expect(result).toEqual(false);

    store.dispatch(new IsWorldpayGuaranteedPaymentsEnabled());

    store.dispatch(new IsWorldpayGuaranteedPaymentsEnabledSuccess(true));

    expect(result).toEqual(true);
  });

  it('getWorldpayGuaranteedPaymentsEnabledSuccess', () => {
    let result: boolean;
    store.pipe(select(getWorldpayGuaranteedPaymentsEnabledSuccess))
      .subscribe((value) => {
        result = value;
        return result;
      });

    expect(result).toEqual(false);

    store.dispatch(new IsWorldpayGuaranteedPaymentsEnabled());
    expect(result).toEqual(false);

    store.dispatch(new IsWorldpayGuaranteedPaymentsEnabledSuccess(true));
    expect(result).toEqual(true);
  });

  it('getWorldpayGuaranteedPaymentsEnabledFail', () => {
    let result: boolean;
    store.pipe(select(getWorldpayGuaranteedPaymentsEnabledFail))
      .subscribe((value) => {
        result = value;
        return result;
      });

    expect(result).toEqual(false);

    store.dispatch(new IsWorldpayGuaranteedPaymentsEnabled());
    expect(result).toEqual(false);

    store.dispatch(new IsWorldpayGuaranteedPaymentsEnabledFail(true));
    expect(result).toEqual(true);
  });
});
