import { APP_BASE_HREF, CommonModule, PlatformLocation } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nConfig, provideConfig, provideFeatureTogglesFactory } from '@spartacus/core';
import { IconConfig, IconResourceType } from '@spartacus/storefront';
import { getBaseHref, getWorldpayIconSymbols, OccWorldpayModule, WORLDPAY_ICONS, worldpayFacadeProviders } from '../core';
import { WorldpayEventsModule } from '../core/events';
import { worldpayTranslations } from '../i18n';
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
    WorldpayPaymentMethodsModule
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
    ),
    provideConfig({
      icon: {
        symbols: getWorldpayIconSymbols(),
        resources: [
          {
            type: IconResourceType.SVG,
            url: 'assets/worldpay/worldpay-icons.svg',
            types: Object.values(WORLDPAY_ICONS),
          },
        ]
      }
    } as IconConfig),
  ],
})

export class WorldpayModule {
}
