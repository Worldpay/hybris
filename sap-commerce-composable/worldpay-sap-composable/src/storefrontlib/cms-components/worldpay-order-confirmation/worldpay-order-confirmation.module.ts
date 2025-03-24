import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { CmsConfig, FeaturesConfigModule, I18nModule, provideConfig } from '@spartacus/core';
import { OrderConfirmationOrderEntriesContext, OrderConfirmationTotalsComponent, OrderDetailBillingComponent, OrderDetailsService } from '@spartacus/order/components';
import { OrderConfirmationOrderEntriesContextToken, OrderFacade } from '@spartacus/order/root';
import { CardModule, FormErrorsModule, OutletModule, PasswordVisibilityToggleModule, PromotionsModule, PwaModule } from '@spartacus/storefront';
import { WorldpayCheckoutPaymentRedirectGuard } from '../../../core/guards';
import { WorldpayOrderService } from '../../../core/services';
import { WorldpayOrderOverviewComponent } from '../worldpay-order-details/worldpay-order-overview/worldpay-order-overview.component';
import { WorldpayOrderConfirmationItemsComponent } from './worldpay-order-confirmation-items/worldpay-order-confirmation-items.component';
import { WorldpayOrderConfirmationShippingComponent } from './worldpay-order-confirmation-shipping/worldpay-order-confirmation-shipping.component';
import { WorldpayOrderConfirmationThankYouMessageComponent } from './worldpay-order-confirmation-thank-you-message/worldpay-order-confirmation-thank-you-message.component';
import { WorldpayOrderGuestRegisterFormComponent } from './worldpay-order-guest-register-form/worldpay-order-guest-register-form.component';

// eslint-disable-next-line @typescript-eslint/typedef
const orderConfirmationComponents = [
  WorldpayOrderConfirmationItemsComponent,
  WorldpayOrderConfirmationThankYouMessageComponent,
  WorldpayOrderGuestRegisterFormComponent,
  WorldpayOrderConfirmationShippingComponent,
];

@NgModule({
  declarations: [...orderConfirmationComponents, WorldpayOrderGuestRegisterFormComponent],
  exports: [...orderConfirmationComponents],
  imports: [
    CommonModule,
    CardModule,
    PwaModule,
    PromotionsModule,
    I18nModule,
    ReactiveFormsModule,
    FormErrorsModule,
    OutletModule,
    PasswordVisibilityToggleModule,
    FeaturesConfigModule,
    PasswordVisibilityToggleModule,
    FeaturesConfigModule,
  ],
  providers: [
    provideConfig({
      cmsComponents: {
        OrderConfirmationThankMessageComponent: {
          component: WorldpayOrderConfirmationThankYouMessageComponent,
          providers: [
            {
              provide: OrderFacade,
              useExisting: WorldpayOrderService,
            },
          ],
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },
        ReplenishmentConfirmationMessageComponent: {
          component: WorldpayOrderConfirmationThankYouMessageComponent,
          providers: [
            {
              provide: OrderFacade,
              useExisting: WorldpayOrderService,
            },
          ],
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },

        OrderConfirmationItemsComponent: {
          component: WorldpayOrderConfirmationItemsComponent,
          providers: [
            {
              provide: OrderFacade,
              useExisting: WorldpayOrderService,
            },
          ],
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },
        ReplenishmentConfirmationItemsComponent: {
          component: WorldpayOrderConfirmationItemsComponent,
          providers: [
            {
              provide: OrderFacade,
              useExisting: WorldpayOrderService,
            },
          ],
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },

        OrderConfirmationTotalsComponent: {
          component: OrderConfirmationTotalsComponent,
          providers: [
            {
              provide: OrderFacade,
              useExisting: WorldpayOrderService,
            },
          ],
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },
        ReplenishmentConfirmationTotalsComponent: {
          component: OrderConfirmationTotalsComponent,
          providers: [
            {
              provide: OrderFacade,
              useExisting: WorldpayOrderService,
            },
          ],
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },

        OrderConfirmationOverviewComponent: {
          component: WorldpayOrderOverviewComponent,
          providers: [
            {
              provide: OrderDetailsService,
              useExisting: WorldpayOrderService,
            },
          ],
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },

        OrderConfirmationShippingComponent: {
          component: WorldpayOrderConfirmationShippingComponent,
          providers: [
            {
              provide: OrderDetailsService,
              useExisting: WorldpayOrderService,
            },
          ],
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },
        OrderConfirmationBillingComponent: {
          component: OrderDetailBillingComponent,
          providers: [
            {
              provide: OrderDetailsService,
              useExisting: WorldpayOrderService,
            },
          ],
          guards: [WorldpayCheckoutPaymentRedirectGuard],
        },
      }
    } as CmsConfig),
    {
      provide: OrderConfirmationOrderEntriesContextToken,
      useExisting: OrderConfirmationOrderEntriesContext,
    },
  ],
})
export class WorldpayOrderConfirmationModule {
}
