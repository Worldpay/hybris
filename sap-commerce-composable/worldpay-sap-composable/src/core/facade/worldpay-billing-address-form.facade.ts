import { Injectable } from '@angular/core';
import { Address, facadeFactory } from '@spartacus/core';
import { WORLDPAY_BILLING_ADDRESS_FORM_FEATURE } from './worldpay-feature-name';

@Injectable({
  providedIn: 'root',
  useFactory: (): WorldpayBillingAddressFormFacade =>
    facadeFactory({
      facade: WorldpayBillingAddressFormFacade,
      feature: WORLDPAY_BILLING_ADDRESS_FORM_FEATURE,
      methods: [
        'getSameAsDeliveryAddress',
        'setSameAsDeliveryAddress',
        'setDeliveryAddressAsBillingAddress',
        'setBillingAddress',
      ],
    }),
})
export abstract class WorldpayBillingAddressFormFacade {
  abstract getSameAsDeliveryAddress(billingAddress: Address, deliveryAddress?: Address): void;

  abstract setSameAsDeliveryAddress(value: boolean, deliveryAddress: Address): void;

  abstract setDeliveryAddressAsBillingAddress(deliveryAddress: Address): void;

  abstract setBillingAddress(billingAddress: Address, deliveryAddress?: Address): void;
}
