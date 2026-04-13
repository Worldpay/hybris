import { NgModule } from '@angular/core';
import { OccConfig, provideConfig } from '@spartacus/core';
import { worldpayB2BOccConfig } from './adapters';

@NgModule({
  imports: [],
  providers: [
    provideConfig(worldpayB2BOccConfig as OccConfig),
  ]
})
export class OccWorldpayB2bModule {
}
