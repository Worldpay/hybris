import { NgModule } from '@angular/core';
import { WorldpayOrderConfirmationItemsComponent } from './worldpay-order-confirmation-items/worldpay-order-confirmation-items.component';
import { WorldpayOrderConfirmationShippingComponent } from './worldpay-order-confirmation-shipping/worldpay-order-confirmation-shipping.component';
import { WorldpayOrderConfirmationThankYouMessageComponent } from './worldpay-order-confirmation-thank-you-message/worldpay-order-confirmation-thank-you-message.component';
import { WORLDPAY_ORDER_CONFIRMATION_FEATURE_PROVIDERS } from './worldpay-order-confirmation.providers';

const orderConfirmationComponents: (typeof WorldpayOrderConfirmationItemsComponent |
  typeof WorldpayOrderConfirmationThankYouMessageComponent |
  typeof WorldpayOrderConfirmationShippingComponent
  )[] = [
    WorldpayOrderConfirmationItemsComponent,
    WorldpayOrderConfirmationThankYouMessageComponent,
    WorldpayOrderConfirmationShippingComponent,
  ];

/**
 * Angular module for the Worldpay Order Confirmation feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayOrderConfirmationItemsComponent`, `WorldpayOrderConfirmationThankYouMessageComponent`,
 *    `WorldpayOrderConfirmationShippingComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayOrderConfirmation()`.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * CMS configuration and exports the standalone components.
 *
 * @since 221121.11.0
 * - All order confirmation components are now standalone components.
 * - All feature-specific providers and CMS mappings have been moved to `provideWorldpayOrderConfirmation()`.
 */
@NgModule({
  exports: orderConfirmationComponents,
  imports: orderConfirmationComponents,
  providers: WORLDPAY_ORDER_CONFIRMATION_FEATURE_PROVIDERS,
})
export class WorldpayOrderConfirmationModule {
}
