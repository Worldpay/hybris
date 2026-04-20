import { NgModule } from '@angular/core';
import { worldpayGuaranteedPaymentsConnectorProvider } from './connectors';
import { worldpayGuaranteedPaymentsFacadeProviders } from './facade';

@NgModule({
  providers: [
    ...worldpayGuaranteedPaymentsConnectorProvider,
    ...worldpayGuaranteedPaymentsFacadeProviders
  ]
})
export class WorldpayGuaranteedPaymentsCoreModule {
}