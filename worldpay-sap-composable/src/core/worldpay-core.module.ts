import { NgModule } from '@angular/core';
import { worldpayProviders } from '../providers';

@NgModule({
  providers: [
    ...worldpayProviders()
  ]
})
export class WorldpayCoreModule {
}