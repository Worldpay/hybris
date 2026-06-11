import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CartValidationGuard } from '@spartacus/cart/base/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayCheckoutDeliveryAddressComponent } from './worldpay-checkout-delivery-address.component';

/**
 * Providers for the Worldpay Checkout Delivery Address feature.
 *
 * This array includes the CMS configuration for the CheckoutDeliveryAddress CMS component,
 * specifying the component and required guards for route protection.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayCheckoutDeliveryAddress()`.
 * 2. The configuration ensures the correct component and guards are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_CHECKOUT_DELIVERY_ADDRESS_FEATURE_PROVIDERS: Provider[] = [
  provideConfig({
    cmsComponents: {
      CheckoutDeliveryAddress: {
        component: WorldpayCheckoutDeliveryAddressComponent,
        guards: [CheckoutAuthGuard, CartNotEmptyGuard, CartValidationGuard],
      },
    },
  } as CmsConfig)
];

/**
 * Factory function to provide all Worldpay Checkout Delivery Address feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay Checkout Delivery Address feature
 * @since 221121.11.0
 */
export function provideWorldpayCheckoutDeliveryAddress(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_CHECKOUT_DELIVERY_ADDRESS_FEATURE_PROVIDERS);
}