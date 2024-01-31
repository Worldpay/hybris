import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ConverterService, OccConfig, OccEndpointsService } from '@spartacus/core';
import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { OccWorldpayGuaranteedPaymentsAdapter } from './occ-worldpay-guaranteed-payments.adapter';

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

describe('OccWorldpayGuaranteedPaymentsAdapter', () => {
  let service: OccWorldpayGuaranteedPaymentsAdapter;
  let httpMock: HttpTestingController;
  let converter: ConverterService;
  let occEndpointsService: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
      providers: [
        OccWorldpayGuaranteedPaymentsAdapter,
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

    service = TestBed.inject(OccWorldpayGuaranteedPaymentsAdapter);
    httpMock = TestBed.inject(HttpTestingController);
    converter = TestBed.inject(ConverterService);
    occEndpointsService = TestBed.inject(OccEndpointsService);
    spyOn(occEndpointsService, 'buildUrl').and.callThrough();
  });

  it('should trigger isFraudSightEnabled request', () => {
    service.isGuaranteedPaymentsEnabled().subscribe();

    expect(occEndpointsService.buildUrl).toHaveBeenCalledWith('isGuaranteedPaymentsEnabled');

    const mockReq = httpMock.expectOne(req =>
      req.method === 'GET'
    );

    expect(mockReq.cancelled).toBeFalsy();
    mockReq.flush({});
  });
});
