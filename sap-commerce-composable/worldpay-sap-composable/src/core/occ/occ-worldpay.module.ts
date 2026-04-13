import { CommonModule } from '@angular/common';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { OccConfig, provideConfig } from '@spartacus/core';
import { WorldpayAdapter, WorldpayConnector } from '../connectors';
import { ApmNormalizer, ComponentApmNormalizer } from '../normalizers';
import { worldpayOccConfig } from './adapters/worldpayEndpointConfiguration';
import { APM_NORMALIZER, COMPONENT_APM_NORMALIZER } from './converters';
import { OccWorldpayAdapter } from './occ-worldpay.adapter';

@NgModule({
  imports: [CommonModule],
  providers: [
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
    provideConfig(worldpayOccConfig as OccConfig),
    provideHttpClient(withInterceptorsFromDi()),
  ]
})
export class OccWorldpayModule {
}
