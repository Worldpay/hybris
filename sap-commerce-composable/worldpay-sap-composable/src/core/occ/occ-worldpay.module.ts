import { CommonModule } from '@angular/common';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { OccConfig, provideConfig } from '@spartacus/core';
import { WorldpayAdapter } from '../connectors/worldpay.adapter';
import { WorldpayConnector } from '../connectors/worldpay.connector';
import { ApmNormalizer } from '../normalizers/apm.normalizer';
import { ComponentApmNormalizer } from '../normalizers/component-apm.normalizer';
import { wordlpayOccConfig } from './adapters/worldpayEndpointConfiguration';
import { APM_NORMALIZER, COMPONENT_APM_NORMALIZER } from './converters';
import { OccWorldpayAdapter } from './occ-worldpay.adapter';

@NgModule({ imports: [CommonModule], providers: [
  WorldpayConnector,
  {
    provide: WorldpayAdapter,
    useClass: OccWorldpayAdapter
  },
  {
    provide: APM_NORMALIZER,
    useClass: ApmNormalizer,
    multi: true
  },
  {
    provide: COMPONENT_APM_NORMALIZER,
    useClass: ComponentApmNormalizer,
    multi: true
  },
  provideConfig(wordlpayOccConfig as OccConfig),
  provideHttpClient(withInterceptorsFromDi()),
] })
export class OccWorldpayModule {
}
