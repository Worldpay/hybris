import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { I18nModule } from '@spartacus/core';
import { WorldpayApplepayComponent } from './worldpay-applepay.component';
import { CheckoutService } from '@spartacus/checkout/core';
import { CheckoutFacade } from '@spartacus/checkout/root';
import { WorldpayBillingAddressModule } from '../../worldpay-billing-address/worldpay-billing-address.module';

@NgModule({
  declarations: [
    WorldpayApplepayComponent
  ],
  exports: [
    WorldpayApplepayComponent
  ],
  imports: [
    CommonModule,
    I18nModule,
    WorldpayBillingAddressModule,
  ],
  providers: [
    CheckoutService,
    {
      provide: CheckoutFacade,
      useExisting: CheckoutService,
    },
  ],
})
export class WorldpayApplepayModule {
}
