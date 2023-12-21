import { TestBed } from '@angular/core/testing';

import { ApmNormalizer } from './apm.normalizer';
import { Image, OccConfig } from '@spartacus/core';
import { ApmData, OccApmData, PaymentMethod, WorldpayApmPaymentInfo } from '../interfaces';

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
          {
            bankName: 'RABO BANK',
            bankCode: 'RABOBANK'
          }
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

  it('should return a target object with the same properties as the source object when source and target are both defined', function() {
    const source: WorldpayApmPaymentInfo = {
      apmCode: 'code',
      apmName: 'name',
      shopperBankCode: 'bankCode',
      billingAddress: {
        firstName: 'firstName',
      },
      name: 'name',
      subscriptionId: 'subscriptionId'
    };

    const target: WorldpayApmPaymentInfo = {
      code: 'code',
      name: 'name',
      shopperBankCode: 'bankCode',
      billingAddress: {
        firstName: 'firstName',
      },
      subscriptionId: 'subscriptionId'
    };

    const result = service.normalizeApmData(source, target);

    expect(result).toEqual(target);
  });

  it('should return a new object with the same properties as the source object when source and target are both undefined', function() {
    const source: WorldpayApmPaymentInfo = {
      apmCode: 'code',
      apmName: 'name',
      shopperBankCode: 'bankCode',
      billingAddress: { firstName: 'firstName' },
      name: 'name',
      subscriptionId: 'subscriptionId'
    };

    const result = service.normalizeApmData(source);

    expect(result).toEqual({
      shopperBankCode: 'bankCode',
      billingAddress: { firstName: 'firstName' },
      name: 'name',
      subscriptionId: 'subscriptionId',
      code: 'code'
    });
  });

  it('should set the code property of the target object to the value of the apmCode property when source has an apmCode property', function() {
    const source: WorldpayApmPaymentInfo = {
      apmCode: 'code',
      apmName: 'name',
      shopperBankCode: 'bankCode',
      billingAddress: { firstName: 'firstName' },
      name: 'name',
      subscriptionId: 'subscriptionId'
    };
    const target: WorldpayApmPaymentInfo = {};

    const result = service.normalizeApmData(source, target);

    expect(result.code).toEqual(source.apmCode);
  });

  it('should throw an error when source is undefined', function() {
    const source: WorldpayApmPaymentInfo = undefined;
    const target: WorldpayApmPaymentInfo = {};

    expect(() => service.normalizeApmData(source, target)).toThrowError();
  });

  it('should return a new object with no properties when source is an empty object', function() {
    const source: WorldpayApmPaymentInfo = {};
    const target: WorldpayApmPaymentInfo = {};

    const result = service.normalizeApmData(source, target);

    expect(result).toEqual({});
  });

});
