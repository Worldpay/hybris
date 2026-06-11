import { NgModule } from '@angular/core';
import { WorldpayGuaranteedPaymentsComponent } from './worldpay-guaranteed-payments.component';
import { WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_PROVIDERS } from './worldpay-guaranteed-payments.providers';

/**
 * @since 221121.11.0. Use the `WorldpayGuaranteedPaymentsComponent` (standalone) instead.
 */
@NgModule({
  exports: [WorldpayGuaranteedPaymentsComponent],
  imports: [WorldpayGuaranteedPaymentsComponent],
  providers: WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_PROVIDERS
})
export class WorldpayGuaranteedPaymentsComponentModule {
}
