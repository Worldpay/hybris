import { APP_BASE_HREF, CommonModule, PlatformLocation } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nConfig, provideConfig, provideFeatureTogglesFactory } from '@spartacus/core';
import { WorldpayCheckoutDeliveryAddressModule } from '@worldpay-components/worldpay-checkout-delivery-address/worldpay-checkout-delivery-address.module';
import { WorldpayCheckoutPaymentMethodModule } from '@worldpay-components/worldpay-checkout-payment-method/worldpay-checkout-payment-method.module';
import { WorldpayCheckoutPlaceOrderModule } from '@worldpay-components/worldpay-checkout-place-order/worldpay-checkout-place-order.module';
import { Worldpay3dsChallengeIframeModule } from '@worldpay-pages/worldpay-3ds-challenge-iframe/worldpay-3ds-challenge-iframe.module';
import { WorldpayDdcIframeRoutingModule } from '@worldpay-pages/worldpay-ddc-iframe/worldpay-ddc-iframe-routing.module';
import { WorldpayDdcIframeModule } from '@worldpay-pages/worldpay-ddc-iframe/worldpay-ddc-iframe.module';
import { worldpayTranslations } from '../assets/worldpay-translations';
import { OccWorldpayModule } from '../core/occ/occ-worldpay.module';
import { worldpayFacadeProviders } from '../core/services/facade-providers';
import { getBaseHref } from '../core/utils/get-base-href';
import { WorldpayCartSharedModule, WorldpayCheckoutReviewPaymentModule, WorldpayOrderConfirmationModule, WorldpayOrderDetailsModule } from '../storefrontlib/cms-components';

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
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
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
