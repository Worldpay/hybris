import { NgModule } from '@angular/core';
import { WorldpayCheckoutDeliveryAddressComponent } from './worldpay-checkout-delivery-address.component';
import { WORLDPAY_CHECKOUT_DELIVERY_ADDRESS_FEATURE_PROVIDERS } from './worldpay-checkout-delivery-address.providers';

/**
 * Angular module for the Worldpay Checkout Delivery Address feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayCheckoutDeliveryAddressComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayCheckoutDeliveryAddress()`.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * CMS configuration and exports the standalone component.
 *
 * @since 221121.11.0
 * - `WorldpayCheckoutDeliveryAddressComponent` is now a standalone component.
 * - All feature-specific providers and CMS mappings have been moved to `provideWorldpayCheckoutDeliveryAddress()`.
 */
@NgModule({
  imports: [WorldpayCheckoutDeliveryAddressComponent],
  exports: [WorldpayCheckoutDeliveryAddressComponent],
  providers: WORLDPAY_CHECKOUT_DELIVERY_ADDRESS_FEATURE_PROVIDERS
})
export class WorldpayCheckoutDeliveryAddressModule {
}
