import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CartValidationGuard } from '@spartacus/cart/base/core';
import { CartNotEmptyGuard, CheckoutAuthGuard } from '@spartacus/checkout/base/components';
import { CmsConfig, provideConfig } from '@spartacus/core';
import { WorldpayCheckoutB2BCheckoutDeliveryAddressComponent } from './worldpay-b2b-checkout-delivery-address.component';

/**
 * Providers for the Worldpay Checkout Delivery Address feature.
 *
 * This array includes the CMS configuration for the CheckoutDeliveryAddress CMS component,
 * specifying the component and required guards for route protection.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayCheckoutB2bDeliveryAddress()`.
 * 2. The configuration ensures the correct component and guards are set for the CMS mapping.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_CHECKOUT_B2B_DELIVERY_ADDRESS_FEATURE_PROVIDERS: Provider[] = [
  provideConfig(<CmsConfig>{
    cmsComponents: {
      CheckoutDeliveryAddress: {
        component: WorldpayCheckoutB2BCheckoutDeliveryAddressComponent,
        guards: [CheckoutAuthGuard, CartNotEmptyGuard, CartValidationGuard],
      },
    },
  }),
];

/**
 * Factory function to provide all Worldpay Checkout Delivery Address feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay Checkout Delivery Address feature
 * @since 221121.11.0
 */
export function provideWorldpayCheckoutB2bDeliveryAddress(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_CHECKOUT_B2B_DELIVERY_ADDRESS_FEATURE_PROVIDERS);
}