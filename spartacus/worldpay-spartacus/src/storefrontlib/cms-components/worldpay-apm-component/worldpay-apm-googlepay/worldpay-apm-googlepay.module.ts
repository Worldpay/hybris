import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayApmGooglepayComponent } from './worldpay-apm-googlepay.component';
import { WorldpayBillingAddressModule } from '../../worldpay-billing-address/worldpay-billing-address.module';
import { CheckoutService } from '@spartacus/checkout/core';
import { CheckoutFacade } from '@spartacus/checkout/root';

@NgModule({
  declarations: [WorldpayApmGooglepayComponent],
  exports: [
    WorldpayApmGooglepayComponent
  ],
  imports: [
    CommonModule,
    WorldpayBillingAddressModule
  ],
  providers: [
    CheckoutService,
    {
      provide: CheckoutFacade,
      useExisting: CheckoutService,
    },
    /*provideConfig({
      cmsComponents: {
        WorldpayGooglePayComponent: {
          component: WorldpayApmGooglepayComponent
        }
      }
    })*/
  ]
})
export class WorldpayApmGooglepayModule {
}
