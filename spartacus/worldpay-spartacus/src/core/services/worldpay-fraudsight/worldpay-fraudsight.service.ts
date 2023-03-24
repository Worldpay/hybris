import { Injectable } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { getWorldpayFraudSightEnabled, getWorldpayFraudSightId } from '../../store/worldpay.selectors';
import { Observable } from 'rxjs';
import { GetFraudSightEnabled, SetFraudSightId } from '../../store/worldpay.action';

@Injectable({
  providedIn: 'root'
})
export class WorldpayFraudsightService {

  constructor(protected store: Store) {
  }

  setFraudSightId(id: string): void {
    this.store.dispatch(new SetFraudSightId(id));
  }

  isFraudSightEnabled(): void {
    this.store.dispatch(new GetFraudSightEnabled());
  }

  getFraudSightIdFromState(): Observable<string> {
    return this.store.pipe(select(getWorldpayFraudSightId));
  }

  isFraudSightEnabledFromState(): Observable<boolean> {
    return this.store.pipe(select(getWorldpayFraudSightEnabled));
  }
}
