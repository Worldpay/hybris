import { faker } from '@faker-js/faker';
import { OrderEntry } from '@spartacus/cart/base/root';
import { Order } from '@spartacus/order/root';
import { generateOneAddress } from './address.mock';
import { generateOneProductEntry } from './product-entries.mock';

export const mockOrder = {
  code: '0001',
  guid: '0001',
};
export const generateOrder = (productEntries: number = 1, currencyIso: string = 'USD'): Order => {
  const entries: OrderEntry[] = productEntries > 0 ? Array.from({ length: productEntries }, () => generateOneProductEntry()) : [];
  const totalItems: number = entries.length;
  const totalValue: number = entries.reduce((acc, entry: OrderEntry) => acc + entry.quantity * entry.totalPrice.value, 0);
  return {
    code: faker.string.uuid(),
    guid: faker.string.uuid(),
    guestCustomer: false,
    paymentInfo: {
      billingAddress: generateOneAddress(),
      id: faker.string.uuid(),
    },
    entries,
    totalItems,
    totalPrice: {
      currencyIso,
      formattedValue: `$${totalValue.toFixed(2)}`,
      value: totalValue
    },
  };
};