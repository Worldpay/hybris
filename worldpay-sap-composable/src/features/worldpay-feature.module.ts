import { NgModule } from '@angular/core';
import { WorldpayOccModule } from '../core';
import { WorldpayEventsModule } from '../core/events';
import { WorldpayCoreModule } from '../core/worldpay-core.module';
import {
  Worldpay3dsChallengeIframeModule,
  WorldpayCartSharedModule,
  WorldpayCheckoutDeliveryAddressModule,
  WorldpayCheckoutPaymentMethodModule,
  WorldpayCheckoutPlaceOrderModule,
  WorldpayCheckoutReviewPaymentModule,
  WorldpayDdcIframeModule,
  WorldpayDdcIframeRoutingModule,
  WorldpayOrderConfirmationModule,
  WorldpayOrderDetailsModule,
  WorldpayPaymentMethodsModule
} from '../storefrontlib';
import { WorldpayFraudsightRiskFeatureModule } from './worldpay-fraudsight-risk-feature.module';

@NgModule({
  declarations: [],
  imports: [
    WorldpayCoreModule,
    WorldpayOccModule,
    WorldpayCheckoutPaymentMethodModule,
    WorldpayCheckoutDeliveryAddressModule,
    WorldpayDdcIframeModule,
    WorldpayDdcIframeRoutingModule,
    Worldpay3dsChallengeIframeModule,
    WorldpayCheckoutPlaceOrderModule,
    WorldpayCheckoutReviewPaymentModule,
    WorldpayCartSharedModule,
    WorldpayOrderConfirmationModule,
    WorldpayOrderDetailsModule,
    WorldpayEventsModule,
    WorldpayPaymentMethodsModule,
    WorldpayFraudsightRiskFeatureModule,
  ],
})

export class WorldpayFeatureModule {
}
