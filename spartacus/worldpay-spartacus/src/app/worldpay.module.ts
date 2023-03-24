import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { I18nConfig, provideConfig } from '@spartacus/core';
import { OccWorldpayModule } from '../core/occ/occ-worldpay.module';
import { WorldpayStoreModule } from '../core/store/worldpay-store.module';
import { WorldpayDdcIframeModule } from '../storefrontlib/pages/worldpay-ddc-iframe/worldpay-ddc-iframe.module';
import { WorldpayDdcIframeRoutingModule } from '../storefrontlib/pages/worldpay-ddc-iframe/worldpay-ddc-iframe-routing.module';
import { WorldpayPaymentComponentModule } from '../storefrontlib/cms-components/worldpay-payment-component/worldpay-payment-component.module';
import { Worldpay3dsChallengeIframeModule } from '../storefrontlib/pages/worldpay-3ds-challenge-iframe/worldpay-3ds-challenge-iframe.module';
import { WorldpayPlaceOrderComponentModule } from '../storefrontlib/cms-components/worldpay-place-order-component/worldpay-place-order-component.module';
import { Worldpay3dsChallengeIframeRoutingModule } from '../storefrontlib/pages/worldpay-3ds-challenge-iframe/worldpay-3ds-challenge-iframe-routing.module';
import { worldpayTranslations } from '../assets/worldpay-translations';
import { WorldpayCheckoutReviewSubmitModule } from '../storefrontlib/cms-components/worldpay-checkout-review-submit/worldpay-checkout-review-submit.module';
import { WorldpayFraudsightRiskModule } from '../storefrontlib/cms-components/worldpay-fraudsight-risk/worldpay-fraudsight-risk.module';
import { WorldpayOrderConfirmationModule } from '../storefrontlib/cms-components/worldpay-order-confirmation/worldpay-order-confirmation.module';
import { WorldpayOrderDetailShippingModule } from '../storefrontlib/cms-components/worldpay-order-detail-shipping/worldpay-order-detail-shipping.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    OccWorldpayModule,
    WorldpayStoreModule,
    WorldpayDdcIframeModule,
    WorldpayFraudsightRiskModule,
    WorldpayDdcIframeRoutingModule,
    WorldpayPaymentComponentModule,
    Worldpay3dsChallengeIframeModule,
    WorldpayPlaceOrderComponentModule,
    WorldpayCheckoutReviewSubmitModule,
    Worldpay3dsChallengeIframeRoutingModule,
    WorldpayOrderConfirmationModule,
    WorldpayOrderDetailShippingModule,
  ],
  providers: [
    provideConfig({
      i18n: {
        resources: worldpayTranslations
      },
    } as I18nConfig),
  ],
})

export class WorldpayModule {
}
