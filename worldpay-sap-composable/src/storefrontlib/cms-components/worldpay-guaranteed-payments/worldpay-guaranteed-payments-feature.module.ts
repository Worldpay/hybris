/* TODO: REMOVE
import { NgModule } from '@angular/core';
import { provideConfig } from '@spartacus/core';
import { WORLDPAY_GUARANTEED_PAYMENTS_FEATURE } from '../../../core';

@NgModule({
  providers: [
    provideConfig({
      featureModules: {
        [WORLDPAY_GUARANTEED_PAYMENTS_FEATURE]: {
          // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
          module: () =>
            // eslint-disable-next-line @typescript-eslint/typedef,@typescript-eslint/explicit-function-return-type
            import('./worldpay-guaranteed-payments-component.module').then(m => m.WorldpayGuaranteedPaymentsComponentModule),
        },
      },
    }),
  ],
})
export class WorldpayGuaranteedPaymentsFeatureModule {
}*/
