import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayGuaranteedPaymentsComponent } from './worldpay-guaranteed-payments.component';
import { UserAccountAdapter, UserAccountConnector, UserAccountService } from '@spartacus/user/account/core';
import { UserAccountFacade } from '@spartacus/user/account/root';
import { OccUserAccountAdapter } from '@spartacus/user/account/occ';

@NgModule({
  declarations: [
    WorldpayGuaranteedPaymentsComponent
  ],
  exports: [
    WorldpayGuaranteedPaymentsComponent
  ],
  imports: [
    CommonModule
  ],
  providers: [
    UserAccountService,
    UserAccountConnector,
    {
      provide: UserAccountAdapter,
      useClass: OccUserAccountAdapter
    },
    {
      provide: UserAccountFacade,
      useExisting: UserAccountService,
    },
  ]
})
export class WorldpayGuaranteedPaymentsModule {
}
