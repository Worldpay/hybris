import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { WorldpayApmSubmitButtonsComponent } from './worldpay-apm-submit-buttons.component';



@NgModule({
  declarations: [WorldpayApmSubmitButtonsComponent],
  exports: [WorldpayApmSubmitButtonsComponent],
  imports: [
    CommonModule,
    I18nModule
  ]
})
export class WorldpayApmSubmitButtonsModule { }
