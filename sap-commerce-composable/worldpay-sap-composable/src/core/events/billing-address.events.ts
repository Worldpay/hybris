import { Address, CxEvent } from '@spartacus/core';

/**
 * Event triggered when the billing address is created
 *
 * @extends {CxEvent}
 */
export class WorldpayBillingAddressCreatedEvent extends CxEvent {
  static override readonly type: string = 'WorldpayBillingAddressCreatedEvent';
  billingAddress: Address;
}

/**
 * Event triggered when the billing address is updated
 *
 * @extends {CxEvent}
 */
export class WorldpayBillingAddressUpdatedEvent extends CxEvent {
  static override readonly type: string = 'WorldpayBillingAddressUpdatedEvent';
  billingAddress: Address;
  deliveryAddress: Address;
}

