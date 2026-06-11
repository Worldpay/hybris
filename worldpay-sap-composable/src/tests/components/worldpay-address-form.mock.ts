import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Address, FeaturesConfigModule, I18nTestingModule } from '@spartacus/core';

@Component({
  selector: 'y-worldpay-address-form',
  template: '',
  imports: [
    I18nTestingModule,
    FeaturesConfigModule
  ]
})
export class MockWorldpayAddressFormComponent {
  @Input() showTitleCode: boolean;
  @Input() setAsDefaultField: boolean;
  @Input() addressData: Address;
  @Input() cancelBtnLabel: string;
  @Output() submitAddress = new EventEmitter<any>();
  @Output() backToAddress = new EventEmitter<any>();
}
