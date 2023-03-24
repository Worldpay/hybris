import { select, Store, StoreModule } from '@ngrx/store';
import { StateWithWorldpay } from '../../worldpay.state';
import { TestBed } from '@angular/core/testing';
import { WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_KEY } from '../worldpay-guaranteed-payments.state';
import { getReducersWorldpayGuaranteedPayments } from '../reducers/worldpay-guaranteed-payments.reducer';
import { getWorldpayGuaranteedPaymentsSessionIdSuccess, getWorldpayGuaranteedPaymentsSessionIdValue } from './worldpay-guaranteed-payments-session-id.selectors';
import { LoadWorldpayGuaranteedPayments, LoadWorldpayGuaranteedPaymentsSuccess } from '../actions/worldpay-guaranteed-payments-session-id.actions';

describe('WorldpayGuaranteedPayments Session Id Selectors', () => {
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

  it('getWorldpayGuaranteedPaymentsSessionIdValue', () => {
    let result: string;
    store.pipe(select(getWorldpayGuaranteedPaymentsSessionIdValue))
      .subscribe((value) => {
        result = value;
        return result;
      });

    expect(result).toEqual('');

    store.dispatch(new LoadWorldpayGuaranteedPayments('user_id_cart'));
    expect(result).toEqual('');

    store.dispatch(new LoadWorldpayGuaranteedPaymentsSuccess('user_id_cart'));
    expect(result).toEqual('user_id_cart');
  });

  it('getWorldpayGuaranteedPaymentsSessionIdSuccess', () => {
    let result: boolean;
    store.pipe(select(getWorldpayGuaranteedPaymentsSessionIdSuccess))
      .subscribe((value) => {
        result = value;
        return result;
      });

    expect(result).toEqual(false);

    store.dispatch(new LoadWorldpayGuaranteedPayments('user_id_cart'));
    expect(result).toEqual(false);

    store.dispatch(new LoadWorldpayGuaranteedPaymentsSuccess('user_id_cart'));
    expect(result).toEqual(true);
  });
});
