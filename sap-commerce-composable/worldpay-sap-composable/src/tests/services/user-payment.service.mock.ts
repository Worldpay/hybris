import { CardType, UserPaymentService } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import createSpy = jasmine.createSpy;

const mockCountries = [{
  isocode: 'US',
  name: 'United States'
}];

export class MockUserPaymentService implements Partial<UserPaymentService> {

  loadBillingCountries = createSpy().and.returnValue(of(mockCountries));

  getPaymentCardTypes(): Observable<CardType[]> {
    return of([{
      code: 'visa',
      name: 'VISA'
    }]);
  }

  getSetPaymentDetailsResultProcess() {
    return of({ loading: false });
  }

  getAllBillingCountries() {
    return of(mockCountries);
  }
}