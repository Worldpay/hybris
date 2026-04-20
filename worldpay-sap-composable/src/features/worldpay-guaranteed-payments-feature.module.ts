import { NgModule } from '@angular/core';
import { WorldpayGuaranteedPaymentsOccModule } from '../core/occ/worldpay-guaranteed-payments-occ.module';
import { WorldpayGuaranteedPaymentsCoreModule } from '../core/worldpay-guaranteed-payments-core.module';

@NgModule({
  imports: [
    WorldpayGuaranteedPaymentsCoreModule,
    WorldpayGuaranteedPaymentsOccModule,
  ]
})
export class WorldpayGuaranteedPaymentsFeatureModule {
}
