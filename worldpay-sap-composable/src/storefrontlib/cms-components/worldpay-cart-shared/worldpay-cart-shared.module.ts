import { NgModule } from '@angular/core';
import { WorldpayCartItemComponent } from './worldpay-cart-item/worldpay-cart-item.component';
import { WorldpayCartItemListComponent } from './worldpay-cart-item-list/worldpay-cart-item-list.component';
import { WorldpayCartItemListRowComponent } from './worldpay-cart-item-list-row/worldpay-cart-item-list-row.component';
import { WORLDPAY_CART_SHARED_FEATURE_PROVIDERS } from './worldpay-cart-shared.providers';

const components: (
  typeof WorldpayCartItemListRowComponent |
  typeof WorldpayCartItemListComponent |
  typeof WorldpayCartItemComponent
  ) [] = [
    WorldpayCartItemListRowComponent,
    WorldpayCartItemListComponent,
    WorldpayCartItemComponent
  ];

/**
 * Angular module for the Worldpay Cart Shared feature.
 *
 * Since 221121.11.0, this module is maintained for backward compatibility.
 * It is recommended to use the standalone approach for new implementations.
 *
 * ### Standalone Usage:
 * 1. Import `WorldpayCartItemListRowComponent`, `WorldpayCartItemListComponent`, and `WorldpayCartItemComponent` directly into your standalone components.
 * 2. Register providers in your `app.config.ts` (or equivalent) using `provideWorldpayCartShared()`.
 *
 * ### Module Usage:
 * Simply import this module as usual. It automatically registers the required
 * configuration and exports the standalone components.
 *
 * @since 221121.11.0
 * - All cart shared components are now standalone components.
 * - All feature-specific providers and configuration have been moved to `provideWorldpayCartShared()`.
 */
@NgModule({
  exports: components,
  imports: components,
  providers: WORLDPAY_CART_SHARED_FEATURE_PROVIDERS
})
export class WorldpayCartSharedModule {
}