import { Injectable } from '@angular/core';
import { Converter, Image, OccConfig } from '@spartacus/core';
import { ApmData, OccCmsComponentWithMedia, PaymentMethod } from '../interfaces';

@Injectable({
  providedIn: 'root'
})
export class ComponentApmNormalizer implements Converter<OccCmsComponentWithMedia, ApmData> {

  /**
   * Constructor for the ComponentApmNormalizer class.
   *
   * @param config - The OccConfig object used to configure the backend URLs for media and OCC.
   */
  constructor(protected config: OccConfig) {
  }

  /**
   * Converts an OccCmsComponentWithMedia object to an ApmData object.
   *
   * If the target object is not provided, a new target object is created by copying the source object.
   * The method sets the code property to PaymentMethod.Card and the name property to the source name.
   * If the source has a media property, it sets the target media property with a mobile image URL and alt text.
   *
   * @param source - The source OccCmsComponentWithMedia object to be converted
   * @param target - The optional target ApmData object to be populated
   * @returns ApmData - The populated ApmData object
   * @since 6.4.0
   */
  convert(source: OccCmsComponentWithMedia, target?: ApmData): ApmData {
    if (target === undefined) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      target = { ...(source as any) };
    }

    target.code = PaymentMethod.Card;
    target.name = source.name;

    if (source.media) {
      target.media =
        {
          mobile: {
            url: this.normalizeImageUrl(source.media.url),
            alt: source.name
          } as Image
        };
    }
    return target;
  }

  /** taken from product-image-normalizer.ts
   * Traditionally, in an on-prem world, medias and other backend related calls
   * are hosted at the same platform, but in a cloud setup, applications are are
   * typically distributed cross different environments. For media, we use the
   * `backend.media.baseUrl` by default, but fallback to `backend.occ.baseUrl`
   * if none provided.
   * @param url - The image URL to be normalized
   * @returns string - The normalized image URL
   * @since 6.4.0
   */
  private normalizeImageUrl(url: string): string {
    if (new RegExp(/^(http|data:image|\/\/)/i).test(url)) {
      return url;
    }
    return (
      (this.config.backend.media.baseUrl ||
       this.config.backend.occ.baseUrl ||
       '') + url
    );
  }
}
