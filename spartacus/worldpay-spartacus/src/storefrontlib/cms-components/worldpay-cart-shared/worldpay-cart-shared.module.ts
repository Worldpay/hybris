import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorldpayCartItemComponent } from './worldpay-cart-item/worldpay-cart-item.component';
import { WorldpayCartItemListComponent } from './worldpay-cart-item-list/worldpay-cart-item-list.component';
import { RouterModule } from '@angular/router';
import { CartCouponModule, IconModule, ItemCounterModule, MediaModule, ModalModule, OutletModule, PromotionsModule } from '@spartacus/storefront';
import { ReactiveFormsModule } from '@angular/forms';
import { FeaturesConfigModule, I18nModule, provideConfig, UrlModule } from '@spartacus/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { WorldpayCartItemValidationWarningModule } from './worldpay-cart-item-warning/worldpay-cart-item-validation-warning.module';

@NgModule({
  declarations: [
    WorldpayCartItemListComponent,
    WorldpayCartItemComponent,
  ],
  exports: [
    WorldpayCartItemComponent,
    WorldpayCartItemListComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    CartCouponModule,
    ReactiveFormsModule,
    UrlModule,
    NgbModule,
    PromotionsModule,
    I18nModule,
    IconModule,
    MediaModule,
    ItemCounterModule,
    FeaturesConfigModule,
    ModalModule,
    OutletModule,
    WorldpayCartItemValidationWarningModule,
  ],
  providers: [
    provideConfig({
      cmsComponents: {
        CartItemListComponent: {
          component: WorldpayCartItemListComponent,
        },
        CartItemComponent: {
          component: WorldpayCartItemComponent,
        },
      },
    })
  ]
})
export class WorldpayCartSharedModule {
}
