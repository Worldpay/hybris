import { BehaviorSubject, Observable, of } from 'rxjs';
import createSpy = jasmine.createSpy;

let fsEnabled = new BehaviorSubject<boolean>(false);

export class MockWorldpayFraudsightService {
  isFraudSightEnabled = createSpy('WorldpayFraudsightService.isFraudSightEnabled').and.callThrough();

  isFraudSightEnabledFromState() {
    return fsEnabled;
  }

  getFraudSightIdFromState(): Observable<string> {
    return of('fraudSightId');
  }
}