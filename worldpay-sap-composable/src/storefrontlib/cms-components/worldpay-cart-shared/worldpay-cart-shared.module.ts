import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CartCouponModule } from '@spartacus/cart/base/components';
import { AddToCartModule } from '@spartacus/cart/base/components/add-to-cart';
import { CartOutlets } from '@spartacus/cart/base/root';
import { FeaturesConfigModule, I18nModule, UrlModule } from '@spartacus/core';
import { AtMessageModule, IconModule, ItemCounterModule, MediaModule, OutletModule, PromotionsModule, provideOutlet } from '@spartacus/storefront';
import { WorldpayCartItemComponent } from './worldpay-cart-item/worldpay-cart-item.component';
import { WorldpayCartItemListComponent } from './worldpay-cart-item-list/worldpay-cart-item-list.component';
import { WorldpayCartItemListRowComponent } from './worldpay-cart-item-list-row/worldpay-cart-item-list-row.component';
import { WorldpayCartItemValidationWarningModule } from './worldpay-cart-item-warning/worldpay-cart-item-validation-warning.module';

const components: (
  typeof WorldpayCartItemListRowComponent |
  typeof WorldpayCartItemListComponent |
  typeof WorldpayCartItemComponent
  ) [] = [
    WorldpayCartItemListRowComponent,
    WorldpayCartItemListComponent,
    WorldpayCartItemComponent
  ];

@NgModule({
  declarations: components,
  exports: components,
  imports: [
    AtMessageModule,
    CartCouponModule,
    WorldpayCartItemValidationWarningModule,
    CommonModule,
    I18nModule,
    IconModule,
    ItemCounterModule,
    MediaModule,
    OutletModule,
    PromotionsModule,
    ReactiveFormsModule,
    RouterModule,
    UrlModule,
    AddToCartModule,
    FeaturesConfigModule,
  ],
  providers: [
    provideOutlet({
      id: CartOutlets.WORLDPAY_CART_ITEM_LIST,
      component: WorldpayCartItemListComponent,
    }),
  ]
})
export class WorldpayCartSharedModule {
}
