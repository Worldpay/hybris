import { APP_BASE_HREF, CommonModule, PlatformLocation } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nConfig, provideConfig, provideFeatureTogglesFactory } from '@spartacus/core';
import { WorldpayEventsModule } from 'worldpay-sap-composable-events';
import { OccWorldpayModule } from 'worldpay-sap-composable-occ';
import { Worldpay3dsChallengeIframeModule, WorldpayDdcIframeModule, WorldpayDdcIframeRoutingModule } from 'worldpay-sap-composable-pages';
import { worldpayFacadeProviders } from 'worldpay-sap-composable-services';
import { worldpayTranslations } from '../assets/worldpay-translations';
import { getBaseHref } from '../core/utils/get-base-href';
import {
  WorldpayCartSharedModule,
  WorldpayCheckoutDeliveryAddressModule,
  WorldpayCheckoutPaymentMethodModule,
  WorldpayCheckoutPlaceOrderModule,
  WorldpayCheckoutReviewPaymentModule,
  WorldpayOrderConfirmationModule,
  WorldpayOrderDetailsModule
} from '../storefrontlib/cms-components';

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
    WorldpayEventsModule,
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
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    provideFeatureTogglesFactory((): any =>
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      ({
        a11yCheckoutDeliveryFocus: true,
        useExtractedBillingAddressComponent: true,
      })
    )
  ],
})

export class WorldpayModule {
}
