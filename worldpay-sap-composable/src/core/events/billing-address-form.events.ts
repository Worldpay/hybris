import { Address, CxEvent } from '@spartacus/core';

/**
 * Event triggered when the billing address is set to be the same as the delivery address during the checkout process.
 *
 * @extends {CxEvent}
 * @since 2211.43.0
 */
export class WorldpayBillingAddressSameAsDeliveryAddressSetEvent extends CxEvent {
  static override readonly type: string = 'WorldpayBillingAddressSameAsDeliveryAddressSetEvent';
  billingAddress: Address;
  deliveryAddress: Address;
}

/**
 * Event triggered to clear the billing address form during the checkout process.
 *
 * This event is typically dispatched when the billing address form needs to be reset,
 * such as after an order is placed or when the user explicitly clears the form.
 *
 * @extends {CxEvent}
 * @since 2211.43.0
 */
export class WorldpayClearBillingAddressFormEvent extends CxEvent {
  static override readonly type: string = 'WorldpayClearBillingAddressFormEvent';
}
