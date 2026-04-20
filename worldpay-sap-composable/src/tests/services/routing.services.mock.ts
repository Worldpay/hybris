import { RoutingService } from '@spartacus/core';
import { of } from 'rxjs';
import createSpy = jasmine.createSpy;

export class MockRoutingService implements Partial<RoutingService> {
  go = createSpy().and.returnValue(of(true).toPromise());
}