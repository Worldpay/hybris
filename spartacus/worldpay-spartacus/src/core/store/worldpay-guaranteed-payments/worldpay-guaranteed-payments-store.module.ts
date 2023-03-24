import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EffectsModule } from '@ngrx/effects';
import { reducerProviderWorldpayGuaranteedPayments, reducerTokenWorldpayGuaranteedPayments } from './reducers/worldpay-guaranteed-payments.reducer';
import { WorldpayGuaranteedPaymentsEnabledEffects } from './effects/worldpay-guaranteed-payments-enabled.effects';
import { StoreModule } from '@ngrx/store';
import { WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_KEY } from './worldpay-guaranteed-payments.state';
import { WorldpayGuaranteedPaymentsSessionIdEffects } from './effects/worldpay-guaranteed-payments-session-id.effects';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    EffectsModule.forFeature([
      WorldpayGuaranteedPaymentsSessionIdEffects,
      WorldpayGuaranteedPaymentsEnabledEffects
    ]),
    StoreModule.forFeature(WORLDPAY_GUARANTEED_PAYMENTS_FEATURE_KEY, reducerTokenWorldpayGuaranteedPayments)
  ],
  providers: [
    reducerProviderWorldpayGuaranteedPayments,

  ]
})
export class WorldpayGuaranteedPaymentsStoreModule {
}
