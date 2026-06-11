/* TODO: REMOVE
import { NgModule } from '@angular/core';
import { provideConfig } from '@spartacus/core';
import { WORLDPAY_FRAUD_SIGHT_FEATURE } from '../../../core';

@NgModule({
  providers: [
    provideConfig({
      featureModules: {
        [WORLDPAY_FRAUD_SIGHT_FEATURE]: {
          // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
          module: () =>
            // eslint-disable-next-line @typescript-eslint/typedef,@typescript-eslint/explicit-function-return-type
            import('./worldpay-fraudsight-risk-component.module').then(m => m.WorldpayFraudsightRiskComponentModule),
        },
      },
    }),
  ],
})
export class WorldpayFraudsightRiskFeatureModule {
}*/
