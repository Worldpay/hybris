import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { EffectsModule } from '@ngrx/effects';
import { effects } from './worldpay.effect';
import { reducer, reducerProvider } from './worldpay.reducer';
import { StoreModule } from '@ngrx/store';
import { WORLDPAY_FEATURE } from './worldpay.state';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    EffectsModule.forFeature(effects),
    StoreModule.forFeature(WORLDPAY_FEATURE, reducer)
  ],
  providers: [reducerProvider]
})
export class WorldpayStoreModule {
}
