import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { EffectsModule } from '@ngrx/effects';
import { reducerProvider, reducerWorldpay } from './worldpay.reducer';
import { StoreModule } from '@ngrx/store';
import { WORLDPAY_FEATURE } from './worldpay.state';
import { WorldpayEffects } from './worldpay.effect';
import { WorldpayGuaranteedPaymentsStoreModule } from './worldpay-guaranteed-payments/worldpay-guaranteed-payments-store.module';

export const effects: any[] = [
  WorldpayEffects,
];

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    EffectsModule.forFeature(effects),
    StoreModule.forFeature(WORLDPAY_FEATURE, reducerWorldpay),
    WorldpayGuaranteedPaymentsStoreModule,
  ],
  providers: [reducerProvider]
})
export class WorldpayStoreModule {
}
