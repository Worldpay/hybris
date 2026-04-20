import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { OccConfig, OccEndpointsService } from '@spartacus/core';
import { MockOccEndpointsService } from '../../../../tests/services/occ-endpoint.service.mock';
import { OccWorldpayFraudsightAdapter } from './occ-worldpay-fraudsight.adapter';

const MockOccModuleConfig: OccConfig = {
  backend: {
    occ: {
      baseUrl: '',
      prefix: ''
    }
  },

  context: {
    baseSite: ['']
  }
};

describe('OccWorldpayFraudsightAdapter', () => {
  let service: OccWorldpayFraudsightAdapter;
  let httpMock: HttpTestingController;
  let occEndpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [
        OccWorldpayFraudsightAdapter,
        {
          provide: OccConfig,
          useValue: MockOccModuleConfig
        },
        {
          provide: OccEndpointsService,
          useClass: MockOccEndpointsService
        },
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
      ]
    });

    service = TestBed.inject(OccWorldpayFraudsightAdapter);
    httpMock = TestBed.inject(HttpTestingController);
    occEndpointsService = TestBed.inject(OccEndpointsService);
    spyOn(occEndpointsService, 'buildUrl').and.callThrough();
  });

  it('should trigger isFraudSightEnabled request', () => {
    service.isFraudSightEnabled().subscribe();

    expect(occEndpointsService.buildUrl).toHaveBeenCalledWith('isFraudSightEnabled');

    const mockReq = httpMock.expectOne(req =>
      req.method === 'GET'
    );

    expect(mockReq.cancelled).toBeFalsy();
    mockReq.flush({});
  });
});
