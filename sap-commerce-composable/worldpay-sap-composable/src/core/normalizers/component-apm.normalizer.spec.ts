import { TestBed } from '@angular/core/testing';
import { Image, OccConfig } from '@spartacus/core';
import { ComponentApmNormalizer } from '@worldpay-normalizers/component-apm.normalizer';
import { ApmData, OccApmData, OccCmsComponentWithMedia, PaymentMethod } from '../interfaces';

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

describe('ComponentApmNormalizer', () => {
  let service: ComponentApmNormalizer;
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

    service = TestBed.inject(ComponentApmNormalizer);
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

  describe('convert', () => {
    it('should return a target object with the same properties as the source object when target is undefined', () => {
      const source: OccCmsComponentWithMedia = {
        name: 'Component Name',
        media: {
          code: '/media/image.jpg',
          url: '/media/image.jpg',
          mime: 'image/jpeg',
        }
      };

      const result = service.convert(source);

      expect(result).toEqual({
        code: PaymentMethod.Card,
        name: 'Component Name',
        media: {
          mobile: {
            url: 'https://localhost:9002/media/image.jpg',
            alt: 'Component Name'
          } as Image
        }
      });
    });

    it('should return a target object with the same properties as the source object when target is defined', () => {
      const source: OccCmsComponentWithMedia = {
        name: 'Component Name',
        media: {
          code: '/media/image.jpg',
          url: '/media/image.jpg',
          mime: 'image/jpeg',
        }
      };
      const target: ApmData = {};

      const result = service.convert(source, target);

      expect(result).toEqual({
        code: PaymentMethod.Card,
        name: 'Component Name',
        media: {
          mobile: {
            url: 'https://localhost:9002/media/image.jpg',
            alt: 'Component Name'
          } as Image
        }
      });
    });

    it('should return a target object without media when source has no media', () => {
      const source: OccCmsComponentWithMedia = {
        name: 'Component Name'
      };

      const result = service.convert(source);

      expect(result).toEqual({
        code: PaymentMethod.Card,
        name: 'Component Name'
      });
    });

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
});
