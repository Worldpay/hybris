import { Injectable } from '@angular/core';
import { Image } from '@spartacus/core';
import { ApmData, OccCmsComponentWithMedia, PaymentMethod } from '../interfaces';
import { BaseImageNormalizer } from './base-image.normalizer';

@Injectable({
  providedIn: 'root'
})
export class ComponentApmNormalizer extends BaseImageNormalizer<OccCmsComponentWithMedia, ApmData> {
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
}
