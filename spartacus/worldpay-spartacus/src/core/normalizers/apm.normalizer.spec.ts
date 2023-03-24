import { TestBed } from '@angular/core/testing';

import { ApmNormalizer } from './apm.normalizer';
import { Image, OccConfig } from '@spartacus/core';
import { ApmData, OccApmData, PaymentMethod } from '../interfaces';

const MockOccModuleConfig: OccConfig = {
  backend: {
    media: {
      baseUrl: 'https://localhost:9002'
    }
  }
};

describe('ApmNormalizerService', () => {
  let service: ApmNormalizer;
  let source: OccApmData;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: OccConfig,
          useValue: MockOccModuleConfig
        }
      ]
    });
    service = TestBed.inject(ApmNormalizer);
    source = {
      apmConfiguration: {
        code: PaymentMethod.iDeal,
        name: 'iDeal',
        bankConfigurations: [
          {bankName: 'RABO BANK', bankCode: 'RABOBANK'}
        ],
      },
    };
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should populate fields', () => {
    source.media = {
      code: 'ing-icon',
      url: '/media/blob',
      mime: 'image/jpeg',
    };

    const target: ApmData = {};
    service.convert(source, target);

    expect(target.code).toEqual(PaymentMethod.iDeal);
    expect(target.name).toEqual('iDeal');

    const image: Image = target.media.mobile;
    expect(image).not.toBeNull();
    expect(image.url).toEqual('https://localhost:9002/media/blob');
    expect(image.altText).toEqual('iDeal');
  });

  it('should handle apm without image', () => {
    expect(service.convert(source, undefined).media).toBeFalsy();
  });

  it('should handle empty target', () => {
    const target = service.convert(source, undefined);

    expect(target).not.toBeNull();
    expect(target.code).toEqual(PaymentMethod.iDeal);
    expect(target.name).toEqual('iDeal');
  });

});
