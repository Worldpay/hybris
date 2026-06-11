import { NgModule } from '@angular/core';
import { worldpayFraudsightAdapterProviders } from './adapters/worldpay-fraudsight/worldpay-fraudsight-adapter.providers';

@NgModule({
  providers: worldpayFraudsightAdapterProviders()
})
export class WorldpayFraudsightRiskOccModule {
}
