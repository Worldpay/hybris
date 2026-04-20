import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { WorldpayGuaranteedPaymentsComponent } from './worldpay-guaranteed-payments.component';

@NgModule({
  declarations: [WorldpayGuaranteedPaymentsComponent],
  exports: [WorldpayGuaranteedPaymentsComponent],
  imports: [CommonModule]
})
export class WorldpayGuaranteedPaymentsComponentModule {
}
