import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { OccConfig, Image } from '@spartacus/core';
import { BaseImageNormalizer } from './base-image.normalizer';

@Injectable()
class TestImageNormalizer extends BaseImageNormalizer<any, any> {
  convert(source: any, target?: any): any {
    return target || source;
  }

  // Expose protected methods for testing
  public testNormalizeImageUrl(url: string): string {
    return this.normalizeImageUrl(url);
  }

  public testCreateNormalizedImage(url: string, altText?: string): Image {
    return this.createNormalizedImage(url, altText);
  }

  public testCreateMobileMedia(url: string, altText?: string): { mobile: Image } {
    return this.createMobileMedia(url, altText);
  }
}

describe('BaseImageNormalizer', () => {
  let normalizer: TestImageNormalizer;
  let mockConfig: OccConfig;

  beforeEach(() => {
    mockConfig = {
      backend: {
        media: {
          baseUrl: 'https://media.example.com'
        },
        occ: {
          baseUrl: 'https://api.example.com'
        }
      }
    } as OccConfig;

    TestBed.configureTestingModule({
      providers: [
        TestImageNormalizer,
        { provide: OccConfig, useValue: mockConfig }
      ]
    });

    normalizer = TestBed.inject(TestImageNormalizer);
  });

  it('should be created', () => {
    expect(normalizer).toBeTruthy();
  });

  describe('normalizeImageUrl', () => {
    it('should return absolute HTTP URL unchanged', () => {
      const url = 'http://external.com/image.png';
      expect(normalizer.testNormalizeImageUrl(url)).toBe(url);
    });

    it('should return absolute HTTPS URL unchanged', () => {
      const url = 'https://external.com/image.png';
      expect(normalizer.testNormalizeImageUrl(url)).toBe(url);
    });

    it('should return data:image URL unchanged', () => {
      const url = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==';
      expect(normalizer.testNormalizeImageUrl(url)).toBe(url);
    });

    it('should return protocol-relative URL unchanged', () => {
      const url = '//cdn.example.com/image.png';
      expect(normalizer.testNormalizeImageUrl(url)).toBe(url);
    });

    it('should prepend media baseUrl to relative URL', () => {
      const url = '/images/product.png';
      const expected = 'https://media.example.com/images/product.png';
      expect(normalizer.testNormalizeImageUrl(url)).toBe(expected);
    });

    it('should prepend OCC baseUrl when media baseUrl is not configured', () => {
      mockConfig.backend.media.baseUrl = '';
      const url = '/images/product.png';
      const expected = 'https://api.example.com/images/product.png';
      expect(normalizer.testNormalizeImageUrl(url)).toBe(expected);
    });

    it('should return just the URL when no base URLs are configured', () => {
      mockConfig.backend.media.baseUrl = '';
      mockConfig.backend.occ.baseUrl = '';
      const url = '/images/product.png';
      expect(normalizer.testNormalizeImageUrl(url)).toBe(url);
    });

    it('should handle URLs without leading slash', () => {
      const url = 'images/product.png';
      const expected = 'https://media.example.com/images/product.png';
      expect(normalizer.testNormalizeImageUrl(url)).toBe(expected);
    });

    it('should handle empty URL', () => {
      const url = '';
      expect(normalizer.testNormalizeImageUrl(url)).toBe('');
    });

    it('should be case-insensitive for protocol detection', () => {
      const httpUrl = 'HTTP://example.com/image.png';
      const httpsUrl = 'HTTPS://example.com/image.png';
      const dataUrl = 'DATA:IMAGE/png;base64,abc';

      expect(normalizer.testNormalizeImageUrl(httpUrl)).toBe(httpUrl);
      expect(normalizer.testNormalizeImageUrl(httpsUrl)).toBe(httpsUrl);
      expect(normalizer.testNormalizeImageUrl(dataUrl)).toBe(dataUrl);
    });
  });

  describe('createNormalizedImage', () => {
    it('should create Image object with normalized URL and altText', () => {
      const url = '/images/product.png';
      const altText = 'Product Image';
      const result = normalizer.testCreateNormalizedImage(url, altText);

      expect(result.url).toBe('https://media.example.com/images/product.png');
      expect(result.altText).toBe(altText);
      expect(result.format).toBe('product');
    });

    it('should create Image object with empty altText when not provided', () => {
      const url = '/images/product.png';
      const result = normalizer.testCreateNormalizedImage(url);

      expect(result.url).toBe('https://media.example.com/images/product.png');
      expect(result.altText).toBe('');
      expect(result.format).toBe('product');
    });

    it('should create Image object with empty altText when undefined', () => {
      const url = '/images/product.png';
      const result = normalizer.testCreateNormalizedImage(url, undefined);

      expect(result.url).toBe('https://media.example.com/images/product.png');
      expect(result.altText).toBe('');
      expect(result.format).toBe('product');
    });

    it('should handle absolute URLs', () => {
      const url = 'https://external.com/image.png';
      const altText = 'External Image';
      const result = normalizer.testCreateNormalizedImage(url, altText);

      expect(result.url).toBe(url);
      expect(result.altText).toBe(altText);
    });

    it('should handle data URLs', () => {
      const url = 'data:image/png;base64,abc123';
      const altText = 'Data Image';
      const result = normalizer.testCreateNormalizedImage(url, altText);

      expect(result.url).toBe(url);
      expect(result.altText).toBe(altText);
    });
  });

  describe('createMobileMedia', () => {
    it('should create mobile media object with normalized image', () => {
      const url = '/images/product.png';
      const altText = 'Mobile Product Image';
      const result = normalizer.testCreateMobileMedia(url, altText);

      expect(result.mobile).toBeDefined();
      expect(result.mobile.url).toBe('https://media.example.com/images/product.png');
      expect(result.mobile.altText).toBe(altText);
      expect(result.mobile.format).toBe('product');
    });

    it('should create mobile media object without altText', () => {
      const url = '/images/product.png';
      const result = normalizer.testCreateMobileMedia(url);

      expect(result.mobile).toBeDefined();
      expect(result.mobile.url).toBe('https://media.example.com/images/product.png');
      expect(result.mobile.altText).toBe('');
      expect(result.mobile.format).toBe('product');
    });

    it('should handle absolute URLs in mobile media', () => {
      const url = 'https://cdn.example.com/mobile-image.png';
      const altText = 'CDN Mobile Image';
      const result = normalizer.testCreateMobileMedia(url, altText);

      expect(result.mobile.url).toBe(url);
      expect(result.mobile.altText).toBe(altText);
    });

    it('should return object with mobile property', () => {
      const url = '/images/product.png';
      const result = normalizer.testCreateMobileMedia(url);

      expect(Object.keys(result)).toEqual(['mobile']);
      expect(result.mobile).toEqual(jasmine.any(Object));
    });
  });

  describe('convert (abstract method)', () => {
    it('should be implemented by concrete class', () => {
      const source = { test: 'data' };
      const result = normalizer.convert(source);
      expect(result).toBe(source);
    });

    it('should return target if provided', () => {
      const source = { test: 'data' };
      const target = { other: 'data' };
      const result = normalizer.convert(source, target);
      expect(result).toBe(target);
    });
  });

  describe('integration scenarios', () => {
    it('should work with complete workflow: normalize -> create image -> create mobile media', () => {
      const relativeUrl = '/images/product.png';
      const altText = 'Product';

      // Step 1: Normalize URL
      const normalizedUrl = normalizer.testNormalizeImageUrl(relativeUrl);
      expect(normalizedUrl).toBe('https://media.example.com/images/product.png');

      // Step 2: Create normalized image
      const image = normalizer.testCreateNormalizedImage(relativeUrl, altText);
      expect(image.url).toBe(normalizedUrl);
      expect(image.altText).toBe(altText);

      // Step 3: Create mobile media
      const mobileMedia = normalizer.testCreateMobileMedia(relativeUrl, altText);
      expect(mobileMedia.mobile.url).toBe(normalizedUrl);
      expect(mobileMedia.mobile.altText).toBe(altText);
    });

    it('should prioritize media baseUrl over OCC baseUrl', () => {
      mockConfig.backend.media.baseUrl = 'https://media-priority.com';
      mockConfig.backend.occ.baseUrl = 'https://occ-fallback.com';

      const url = '/images/test.png';
      const result = normalizer.testNormalizeImageUrl(url);

      expect(result).toBe('https://media-priority.com/images/test.png');
      expect(result).not.toContain('occ-fallback');
    });
  });
});