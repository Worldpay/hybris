import { NgModule } from '@angular/core';
import { APP_BASE_HREF, CommonModule, PlatformLocation } from '@angular/common';
import { I18nConfig, provideConfig, provideFeatureTogglesFactory } from '@spartacus/core';
import { OccWorldpayModule } from '../core/occ/occ-worldpay.module';
import { worldpayTranslations } from '../assets/worldpay-translations';
import { getBaseHref } from '../core/utils/get-base-href';
import { WorldpayCheckoutPaymentMethodModule } from '../storefrontlib/cms-components/worldpay-checkout-payment-method/worldpay-checkout-payment-method.module';
import { worldpayFacadeProviders } from '../core/services/facade-providers';
import { WorldpayCheckoutPlaceOrderModule } from '../storefrontlib/cms-components/worldpay-checkout-place-order/worldpay-checkout-place-order.module';
import { WorldpayCheckoutDeliveryAddressModule } from '../storefrontlib/cms-components/worldpay-checkout-delivery-address/worldpay-checkout-delivery-address.module';
import { Worldpay3dsChallengeIframeModule } from '../storefrontlib/pages/worldpay-3ds-challenge-iframe/worldpay-3ds-challenge-iframe.module';
import { WorldpayDdcIframeModule } from '../storefrontlib/pages/worldpay-ddc-iframe/worldpay-ddc-iframe.module';
import { WorldpayDdcIframeRoutingModule } from '../storefrontlib/pages/worldpay-ddc-iframe/worldpay-ddc-iframe-routing.module';
import {
  WorldpayCheckoutReviewPaymentModule
} from '../storefrontlib/cms-components/worldpay-checkout-review/worldpay-checkout-review-payment/worldpay-checkout-review-payment.module';
import { WorldpayOrderConfirmationModule } from '../storefrontlib/cms-components/worldpay-order-confirmation/worldpay-order-confirmation.module';
import { WorldpayOrderDetailsModule } from '../storefrontlib/cms-components/worldpay-order-details/worldpay-order-details.module';
import { WorldpayCartSharedModule } from '../storefrontlib/cms-components';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    OccWorldpayModule,
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
  ],
  providers: [
    provideConfig({
      i18n: {
        resources: worldpayTranslations
      },
    } as I18nConfig),
    {
      provide: APP_BASE_HREF,
      useFactory: getBaseHref,
      deps: [PlatformLocation]
    },
    ...worldpayFacadeProviders,

    provideFeatureTogglesFactory(() => {
      const appFeatureToggles: any = {
        a11yCheckoutDeliveryFocus: true,
        useExtractedBillingAddressComponent: true,
      };
      return appFeatureToggles;
    })
  ],
})

export class WorldpayModule {
}
