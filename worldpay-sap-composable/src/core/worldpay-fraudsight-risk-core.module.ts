import { NgModule } from '@angular/core';
import { worldpayFraudsightConnectorProvider } from './connectors';
import { worldpayFraudsightFacadeProviders } from './facade';

@NgModule({
  providers: [
    ...worldpayFraudsightConnectorProvider,
    ...worldpayFraudsightFacadeProviders
  ]
})
export class WorldpayFraudsightRiskCoreModule {
}
