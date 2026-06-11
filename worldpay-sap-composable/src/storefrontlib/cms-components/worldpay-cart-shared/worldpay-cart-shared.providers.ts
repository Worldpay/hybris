import { EnvironmentProviders, makeEnvironmentProviders, Provider } from '@angular/core';
import { CartOutlets } from '@spartacus/cart/base/root';
import { provideOutlet } from '@spartacus/storefront';
import { WorldpayCartItemListComponent } from './worldpay-cart-item-list/worldpay-cart-item-list.component';

/**
 * Providers for the Worldpay Cart Shared feature.
 *
 * This array includes the outlet configuration for the Worldpay cart item list,
 * specifying the outlet ID and the component to render. Register these providers in your
 * `app.config.ts` (or equivalent) using `provideWorldpayCartShared()` to enable the outlet.
 *
 * ### Usage:
 * 1. Register these providers in your `app.config.ts` (or equivalent) using `provideWorldpayCartShared()`.
 * 2. The configuration ensures the correct outlet and component are set for the cart item list.
 *
 * @since 221121.11.0
 */
export const WORLDPAY_CART_SHARED_FEATURE_PROVIDERS: Provider[] = [
  provideOutlet({
    id: CartOutlets.WORLDPAY_CART_ITEM_LIST,
    component: WorldpayCartItemListComponent,
  }),
];

/**
 * Factory function to provide all Worldpay Cart Shared feature providers as environment providers.
 *
 * @returns EnvironmentProviders for the Worldpay Cart Shared feature
 * @since 221121.11.0
 */
export function provideWorldpayCartShared(): EnvironmentProviders {
  return makeEnvironmentProviders(WORLDPAY_CART_SHARED_FEATURE_PROVIDERS);
}