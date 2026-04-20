import { Address, AddressValidation, Country, Region, UserAddressService } from '@spartacus/core';
import { EMPTY, Observable, of } from 'rxjs';
import createSpy = jasmine.createSpy;

const mockRegions: Region[] = [
  {
    isocode: 'CA-ON',
    name: 'Ontario',
  },
  {
    isocode: 'CA-QC',
    name: 'Quebec',
  },
];

export class MockUserAddressService implements Partial<UserAddressService> {
  getAddressesLoading = createSpy().and.returnValue(of(false));
  loadAddresses = createSpy();

  verifyAddress(): Observable<AddressValidation> {
    return of({});
  }

  getRegions() {
    return of(mockRegions);
  }

  getDeliveryCountries(): Observable<Country[]> {
    return EMPTY;
  }

  loadDeliveryCountries(): void {
  }

  getAddresses(): Observable<Address[]> {
    return of([]);
  }
}