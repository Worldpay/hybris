import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { WorldpayAdapter } from '../connectors/worldpay.adapter';
import { OccWorldpayAdapter } from './occ-worldpay.adapter';
import { ApmNormalizer } from '../normalizers/apm.normalizer';
import { APM_NORMALIZER, COMPONENT_APM_NORMALIZER } from './converters';
import { ComponentApmNormalizer } from '../normalizers/component-apm.normalizer';
import { WorldpayConnector } from '../connectors/worldpay.connector';
import { OccConfig, provideConfig } from '@spartacus/core';
import { wordlpayOccConfig } from './adapters/worldpayEndpointConfiguration';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule
  ],
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
    provideConfig(wordlpayOccConfig as OccConfig),
  ]
})
export class OccWorldpayModule {
}
