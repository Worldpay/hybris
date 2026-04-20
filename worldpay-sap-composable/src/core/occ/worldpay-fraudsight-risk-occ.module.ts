import { NgModule } from '@angular/core';
import { worldpayFraudsightAdapterProvider } from './adapters/worldpay-fraudsight/worldpay-fraudsight-adapter.providers';

@NgModule({
  providers: [...worldpayFraudsightAdapterProvider]
})
export class WorldpayFraudsightRiskOccModule {
}
