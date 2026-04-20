import { Provider } from '@angular/core';
import { APM_NORMALIZER, COMPONENT_APM_NORMALIZER } from '../occ/converters';
import { ApmNormalizer } from './apm.normalizer';
import { ComponentApmNormalizer } from './component-apm.normalizer';

export const worldpayApmNormalizerProvider: Provider[] = [
  {
    provide: APM_NORMALIZER,
    useClass: ApmNormalizer,
    multi: true,
  },
];

export const worldpayComponentApmNormalizerProvider: Provider[] = [
  {
    provide: COMPONENT_APM_NORMALIZER,
    useClass: ComponentApmNormalizer,
    multi: true,
  },
];

/**
 * Aggregates all Worldpay normalizer providers into a single array.
 *
 * @since 2211.43.0
 */
export const provideWorldpayNormalizers: () => Provider[] = (): Provider[] => [
  ...worldpayApmNormalizerProvider,
  ...worldpayComponentApmNormalizerProvider,
];
