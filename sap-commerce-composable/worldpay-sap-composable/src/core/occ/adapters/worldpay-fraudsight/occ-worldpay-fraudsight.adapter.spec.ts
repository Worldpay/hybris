import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ConverterService, OccConfig, OccEndpointsService } from '@spartacus/core';
import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
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

class MockOccEndpointsService {
  buildUrl(url) {
    return url;
  }
}

describe('OccWorldpayFraudsightAdapter', () => {
  let service: OccWorldpayFraudsightAdapter;
  let httpMock: HttpTestingController;
  let converter: ConverterService;
  let occEndpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
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
      ]
    });

    service = TestBed.inject(OccWorldpayFraudsightAdapter);
    httpMock = TestBed.inject(HttpTestingController);
    converter = TestBed.inject(ConverterService);
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
