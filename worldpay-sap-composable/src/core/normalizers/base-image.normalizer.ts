import { Injectable } from '@angular/core';
import { Converter, Image, OccConfig } from '@spartacus/core';

/**
 * Base abstract normalizer that provides common image URL normalization functionality.
 *
 * This class should be extended by specific normalizers that need to normalize image URLs
 * using the backend media or OCC base URLs from the OccConfig.
 *
 * @since 2211.43.0
 */
@Injectable()
export abstract class BaseImageNormalizer<S, T> implements Converter<S, T> {

  /**
   * Constructor
   *
   * @param config - The OccConfig object used to configure the backend URLs for media and OCC.
   */
  constructor(protected config: OccConfig) {
  }

  /**
   * Abstract convert method to be implemented by child classes
   *
   * @param source - The source object to be converted
   * @param target - The optional target object to be populated
   * @returns T - The populated target object
   */
  abstract convert(source: S, target?: T): T;

  /**
   * Normalizes an image URL using the backend media or OCC base URL.
   *
   * Traditionally, in an on-prem world, medias and other backend related calls
   * are hosted at the same platform, but in a cloud setup, applications are
   * typically distributed across different environments. For media, we use the
   * `backend.media.baseUrl` by default, but fallback to `backend.occ.baseUrl`
   * if none provided.
   *
   * @param url - The image URL to be normalized
   * @returns string - The normalized image URL
   * @protected
   * @since 2211.43.0
   */
  protected normalizeImageUrl(url: string): string {
    if (!url) {
      return '';
    }
    
    if (new RegExp(/^(http|data:image|\/\/)/i).test(url)) {
      return url;
    }

    const baseUrl: string =
      this.config.backend?.media?.baseUrl ||
      this.config.backend?.occ?.baseUrl ||
      '';

    if (!baseUrl) {
      return url;
    }

    const normalizedBase: string = baseUrl.endsWith('/') ? baseUrl.slice(0, -1) : baseUrl;
    const normalizedUrl: string = url.startsWith('/') ? url : '/' + url;

    return normalizedBase + normalizedUrl;
  }

  /**
   * Creates an Image object with a normalized URL and optional alt text.
   *
   * @param url - The image URL to be normalized
   * @param altText - Optional alternative text for the image
   * @returns Image - The Image object with normalized URL
   * @protected
   * @since 2211.43.0
   */
  protected createNormalizedImage(url: string, altText?: string): Image {
    return {
      url: this.normalizeImageUrl(url),
      altText: altText || '',
      format: 'product'
    } as Image;
  }

  /**
   * Creates a media object with mobile image configuration.
   *
   * @param url - The image URL to be normalized
   * @param altText - Optional alternative text for the image
   * @returns object - Media object with mobile image
   * @protected
   * @since 2211.43.0
   */
  protected createMobileMedia(url: string, altText?: string): { mobile: Image } {
    return {
      mobile: this.createNormalizedImage(url, altText)
    };
  }
}

