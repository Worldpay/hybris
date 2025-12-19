import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ActiveCartService } from '@spartacus/cart/base/core';
import { UserAccountAdapter, UserAccountConnector, UserAccountService } from '@spartacus/user/account/core';
import { OccUserAccountAdapter } from '@spartacus/user/account/occ';
import { UserAccountFacade } from '@spartacus/user/account/root';
import { WorldpayGuaranteedPaymentsAdapter } from '../../../core/connectors/worldpay-guaranteed-payments/worldpay-guaranteed-payments.adapter';
import { WorldpayGuaranteedPaymentsConnector } from '../../../core/connectors/worldpay-guaranteed-payments/worldpay-guaranteed-payments.connector';
import { OccWorldpayGuaranteedPaymentsAdapter } from '../../../core/occ/adapters/worldpay-guaranteed-payments/occ-worldpay-guaranteed-payments.adapter';
import { WorldpayGuaranteedPaymentsComponent } from './worldpay-guaranteed-payments.component';

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
    ActiveCartService,
    UserAccountConnector,
    WorldpayGuaranteedPaymentsConnector,
    {
      provide: UserAccountAdapter,
      useClass: OccUserAccountAdapter
    },
    {
      provide: UserAccountFacade,
      useExisting: UserAccountService,
    },
    {
      provide: WorldpayGuaranteedPaymentsAdapter,
      useClass: OccWorldpayGuaranteedPaymentsAdapter
    }
  ]
})
export class WorldpayGuaranteedPaymentsModule {
}
