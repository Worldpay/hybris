import { NgModule } from '@angular/core';
import { worldpayGuaranteedPaymentsAdapterProviders } from './adapters/worldpay-guaranteed-payments/worldpay-guaranteed-payments-adapter.providers';

@NgModule({
  providers: worldpayGuaranteedPaymentsAdapterProviders()
})
export class WorldpayGuaranteedPaymentsOccModule {
}
