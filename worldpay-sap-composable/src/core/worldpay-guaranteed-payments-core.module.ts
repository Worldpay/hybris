import { NgModule } from '@angular/core';
import { worldpayGuaranteedPaymentsCoreProviders } from '../providers/worldpay-guaranteed-payment-core.provider';

@NgModule({
  providers: worldpayGuaranteedPaymentsCoreProviders()
})
export class WorldpayGuaranteedPaymentsCoreModule {
}