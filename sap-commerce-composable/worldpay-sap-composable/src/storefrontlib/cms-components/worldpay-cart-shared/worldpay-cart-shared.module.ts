import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CartItemContextSource } from '@spartacus/cart/base/components';
import { CartItemContext, CartOutlets } from '@spartacus/cart/base/root';
import { FeaturesConfigModule, I18nModule, UrlModule } from '@spartacus/core';
import { AtMessageModule, IconModule, ItemCounterModule, MediaModule, OutletModule, PromotionsModule, provideOutlet } from '@spartacus/storefront';
import { WorldpayCartItemListRowComponent } from './worldpay-cart-item-list-row/worldpay-cart-item-list-row.component';
import { WorldpayCartItemListComponent } from './worldpay-cart-item-list/worldpay-cart-item-list.component';
import { WorldpayCartItemValidationWarningModule } from './worldpay-cart-item-warning/worldpay-cart-item-validation-warning.module';
import { WorldpayCartItemComponent } from './worldpay-cart-item/worldpay-cart-item.component';

// eslint-disable-next-line @typescript-eslint/typedef
const components = [
  WorldpayCartItemComponent,
  WorldpayCartItemListRowComponent,
  WorldpayCartItemListComponent,
];

@NgModule({
  declarations: components,
  exports: components,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    UrlModule,
    PromotionsModule,
    I18nModule,
    IconModule,
    MediaModule,
    ItemCounterModule,
    FeaturesConfigModule,
    OutletModule,
    WorldpayCartItemValidationWarningModule,
    AtMessageModule,
  ],
  providers: [
    CartItemContextSource,
    {
      provide: CartItemContext,
      useExisting: CartItemContextSource
    },
    provideOutlet({
      id: CartOutlets.WORLDPAY_CART_ITEM_LIST,
      component: WorldpayCartItemListComponent,
    }),
  ]
})
export class WorldpayCartSharedModule {
}
