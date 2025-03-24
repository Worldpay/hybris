import { InjectionToken } from '@angular/core';
import { Converter } from '@spartacus/core';
import { ApmData, OccApmData, OccCmsComponentWithMedia } from '../interfaces';

// eslint-disable-next-line @typescript-eslint/typedef
export const APM_NORMALIZER = new InjectionToken<Converter<OccApmData, ApmData>>(
  'ApmNormalizer'
);

// eslint-disable-next-line @typescript-eslint/typedef
export const COMPONENT_APM_NORMALIZER = new InjectionToken<Converter<OccCmsComponentWithMedia, ApmData>>(
  'ComponentDataApmNormalizer'
);
