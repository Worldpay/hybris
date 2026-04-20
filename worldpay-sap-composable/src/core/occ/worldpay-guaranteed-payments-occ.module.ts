import { NgModule } from '@angular/core';
import { worldpayGuaranteedPaymentsAdapterProvider } from './adapters/worldpay-guaranteed-payments/worldpay-guaranteed-payments-adapter.providers';

@NgModule({
  providers: [...worldpayGuaranteedPaymentsAdapterProvider]
})
export class WorldpayGuaranteedPaymentsOccModule {
}
