import { of } from 'rxjs';

export class MockActivatedRoute {
  snapshot = {
    params: {},
    queryParams: {}
  };
  params = of({});
  queryParams = of({});
}