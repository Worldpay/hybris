import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { OrderConfirmationTotalsComponent, OrderDetailsService } from '@spartacus/order/components';
import { OrderFacade } from '@spartacus/order/root';
import { WorldpayCheckoutPaymentRedirectGuard, WorldpayOrderService } from '../../../core';
import { WorldpayOrderDetailsBillingComponent } from '../worldpay-order-details/worldpay-order-details-billing/worldpay-order-details-billing.component';
import { WorldpayOrderOverviewComponent } from '../worldpay-order-details/worldpay-order-overview/worldpay-order-overview.component';
import { WorldpayOrderConfirmationItemsComponent } from './worldpay-order-confirmation-items/worldpay-order-confirmation-items.component';
import { WorldpayOrderConfirmationShippingComponent } from './worldpay-order-confirmation-shipping/worldpay-order-confirmation-shipping.component';
import { WorldpayOrderConfirmationThankYouMessageComponent } from './worldpay-order-confirmation-thank-you-message/worldpay-order-confirmation-thank-you-message.component';

/**
 * Providers for the Worldpay Order Confirmation feature.
 *
 * This array includes the CMS configuration for all order confirmation-related CMS components,
 * specifying the component, required guards for route protection, and provider overrides for each mapping.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayOrderConfirmation()`.
 * 2. The configuration ensures the correct component, guards, and service overrides are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAYORDER_CONFIRMATION_MESSAGE_COMPONENTS_CONFIG: CmsConfig = {
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
  },
};

export const WORLDPAYORDER_CONFIRMATION_ITEMS_COMPONENTS_CONFIG: CmsConfig = {
  cmsComponents: {
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
  },
};

export const WORLDPAYORDER_CONFIRMATION_TOTALS_COMPONENTS_CONFIG: CmsConfig = {
  cmsComponents: {
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
  },
};

export const WORLDPAYORDER_CONFIRMATION_OVERVIEW_COMPONENTS_CONFIG: CmsConfig = {
  cmsComponents: {
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
    ReplenishmentConfirmationOverviewComponent: {
      component: WorldpayOrderOverviewComponent,
      providers: [
        {
          provide: OrderDetailsService,
          useExisting: WorldpayOrderService,
        },
      ],
      guards: [WorldpayCheckoutPaymentRedirectGuard],
    },
  },
};

export const WORLDPAYORDER_CONFIRMATION_SHIPPING_COMPONENTS_CONFIG: CmsConfig = {
  cmsComponents: {
    OrderConfirmationShippingComponent: {
      component: WorldpayOrderConfirmationShippingComponent,
      providers: [
        {
          provide: OrderFacade,
          useExisting: WorldpayOrderService,
        },
      ],
      guards: [WorldpayCheckoutPaymentRedirectGuard],
    },
  },
};

export const WORLDPAYORDER_CONFIRMATION_BILLING_COMPONENTS_CONFIG: CmsConfig = {
  cmsComponents: {
    OrderConfirmationBillingComponent: {
      component: WorldpayOrderDetailsBillingComponent,
      providers: [
        {
          provide: OrderDetailsService,
          useExisting: WorldpayOrderService,
        },
      ],
      guards: [WorldpayCheckoutPaymentRedirectGuard],
    },
  },
};

/*
Maps the following Spartacus CMS component slots to their Checkout.com
counterparts, applying `WorldpayOrderConfirmationGuard` to each:
  - `OrderConfirmationThankMessageComponent` → `OrderConfirmationThankYouMessageComponent`
  - `ReplenishmentConfirmationMessageComponent` → `OrderConfirmationThankYouMessageComponent`
  - `OrderConfirmationItemsComponent` → `OrderConfirmationItemsComponent`
  - `ReplenishmentConfirmationItemsComponent` → `OrderConfirmationItemsComponent`
  - `OrderConfirmationTotalsComponent` → `OrderConfirmationTotalsComponent`
  - `ReplenishmentConfirmationTotalsComponent` → `OrderConfirmationTotalsComponent`
  - `OrderConfirmationOverviewComponent` → `WorldpayOrderOverviewComponent`
  - `ReplenishmentConfirmationOverviewComponent` → `OrderOverviewComponent
  - `OrderConfirmationShippingComponent` → `OrderConfirmationShippingComponent`
  - `OrderConfirmationBillingComponent` → `WorldpayOrderDetailBillingComponent`
*/
export const WORLDPAY_ORDER_CONFIRMATION_FEATURE_PROVIDERS: Provider[] = [
  provideConfig(<CmsConfig>WORLDPAYORDER_CONFIRMATION_MESSAGE_COMPONENTS_CONFIG),
  provideConfig(<CmsConfig>WORLDPAYORDER_CONFIRMATION_ITEMS_COMPONENTS_CONFIG),
  provideConfig(<CmsConfig>WORLDPAYORDER_CONFIRMATION_TOTALS_COMPONENTS_CONFIG),
  provideConfig(<CmsConfig>WORLDPAYORDER_CONFIRMATION_OVERVIEW_COMPONENTS_CONFIG),
  provideConfig(<CmsConfig>WORLDPAYORDER_CONFIRMATION_SHIPPING_COMPONENTS_CONFIG),
  provideConfig(<CmsConfig>WORLDPAYORDER_CONFIRMATION_BILLING_COMPONENTS_CONFIG),
];

/**
 * Factory function to provide all Worldpay Order Confirmation feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay Order Confirmation feature
 * @since 221121.11.0
 */
export function provideWorldpayOrderConfirmation(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_ORDER_CONFIRMATION_FEATURE_PROVIDERS);
}