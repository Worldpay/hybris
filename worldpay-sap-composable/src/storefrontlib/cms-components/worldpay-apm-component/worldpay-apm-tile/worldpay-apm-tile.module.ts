import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MediaModule } from '@spartacus/storefront';
import { WorldpayApmTileComponent } from './worldpay-apm-tile.component';

@NgModule({
  declarations: [WorldpayApmTileComponent],
  exports: [WorldpayApmTileComponent],
  imports: [
    CommonModule,
    MediaModule
  ]
})
export class WorldpayApmTileModule {
}
