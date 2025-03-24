import { TestBed } from '@angular/core/testing';
import { Image, OccConfig } from '@spartacus/core';
import { of } from 'rxjs';
import { ApmData, OccApmData, PaymentMethod, WorldpayApmPaymentInfo } from '../interfaces';

import { ApmNormalizer } from './apm.normalizer';

const MockOccModuleConfig = {
  backend: {
    media: {
      baseUrl: 'https://localhost:9002'
    },
    occ: {
      baseUrl: 'https://occ.localhost:9002',
    }
  }
};

describe('ApmNormalizerService', () => {
  let service: ApmNormalizer;
  let source: OccApmData;
  let occConfig: OccConfig;

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
    occConfig = TestBed.inject(OccConfig);
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

  describe('normalizeImageUrl', () => {
    it('should return the same URL if it is an absolute URL', () => {
      const absoluteUrl = 'http://example.com/image.jpg';
      expect(service['normalizeImageUrl'](absoluteUrl)).toEqual(absoluteUrl);
    });

    it('should return the same URL if it is a data URL', () => {
      const dataUrl = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA';
      expect(service['normalizeImageUrl'](dataUrl)).toEqual(dataUrl);
    });

    it('should return the same URL if it starts with //', () => {
      const protocolRelativeUrl = '//example.com/image.jpg';
      expect(service['normalizeImageUrl'](protocolRelativeUrl)).toEqual(protocolRelativeUrl);
    });

    it('should prepend the base URL if the URL is relative', () => {
      const relativeUrl = '/media/image.jpg';
      const expectedUrl = 'https://localhost:9002/media/image.jpg';
      expect(service['normalizeImageUrl'](relativeUrl)).toEqual(expectedUrl);
    });
  });

  describe('normalizeApmData', () => {
    it('should return a target object with the same properties as the source object when target is undefined', () => {
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
        name: 'name',
        shopperBankCode: 'bankCode',
        billingAddress: { firstName: 'firstName' },
        subscriptionId: 'subscriptionId',
        code: 'code'
      });
    });

    it('should set the code property to PaymentMethod.Card when source has a cardType property', () => {
      const source: WorldpayApmPaymentInfo = {
        cardType: {
          code: 'VISA',
          name: 'visa'
        }
      };
      const target: WorldpayApmPaymentInfo = {};

      const result = service.normalizeApmData(source, target);

      expect(result.code).toEqual(PaymentMethod.Card);
    });

    it('should set the code property to the value of the apmCode property when source has an apmCode property', () => {
      const source: WorldpayApmPaymentInfo = {
        apmCode: 'code'
      };
      const target: WorldpayApmPaymentInfo = {};

      const result = service.normalizeApmData(source, target);

      expect(result.code).toEqual('code');
    });

    it('should set the shopperBankCode property to the value of the apmName property when source name is "ideal"', () => {
      const source: WorldpayApmPaymentInfo = {
        name: 'iDeal',
        apmName: 'bankName'
      };
      const target: WorldpayApmPaymentInfo = {};

      const result = service.normalizeApmData(source, target);

      expect(result.shopperBankCode).toEqual('bankName');
    });

    it('should populate achPaymentForm when source apmCode is ACH', (done) => {
      const source: WorldpayApmPaymentInfo = {
        apmCode: PaymentMethod.ACH
      };
      const target: WorldpayApmPaymentInfo = {};

      spyOn(service['worldpayACHFacade'], 'getACHPaymentFormValue').and.returnValue(of({ accountNumber: '123456' }));

      service.normalizeApmData(source, target);

      setTimeout(() => {
        expect(target.achPaymentForm).toEqual({ accountNumber: '123456' });
        done();
      }, 0);
    });

    it('should delete apmCode and apmName properties when both are present in source', () => {
      const source: WorldpayApmPaymentInfo = {
        apmCode: 'code',
        apmName: 'name'
      };
      const target: WorldpayApmPaymentInfo = {};

      const result = service.normalizeApmData(source, target);

      // @ts-ignore
      expect(result.apmCode).toBeUndefined();
      // @ts-ignore
      expect(result.apmName).toBeUndefined();
      expect(result.code).toBe('code');
      expect(result.name).toBeUndefined();
    });
  });
});
