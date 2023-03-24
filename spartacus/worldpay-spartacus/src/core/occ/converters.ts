import { InjectionToken } from '@angular/core';
import { Converter } from '@spartacus/core';
import { ApmData, OccApmData, OccCmsComponentWithMedia } from '../interfaces';

export const APM_NORMALIZER = new InjectionToken<Converter<OccApmData, ApmData>>(
  'ApmNormalizer'
);

export const COMPONENT_APM_NORMALIZER = new InjectionToken<Converter<OccCmsComponentWithMedia, ApmData>>(
  'ComponentDataApmNormalizer'
);
