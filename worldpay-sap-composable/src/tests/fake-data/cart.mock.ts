import { faker } from '@faker-js/faker';
import { Cart, OrderEntry } from '@spartacus/cart/base/root';
import { Price } from '@spartacus/core';
import { generateOneProductEntry } from './product-entries.mock';

export const generateFormattedValue = (value: number, currencyIso: string = 'USD'): string => {
  return `$${value.toFixed(2)} ${currencyIso}`;
};

export const generateTotalPrice = (currencyIso: string = 'USD', value: number): Price => {
  const maxQuantity = faker.number.int({
    min: 1,
    max: 5
  });

  return {
    currencyIso,
    formattedValue: generateFormattedValue(value),
    maxQuantity,
    value
  };
};

export const generateOneCart = (productEntries: number = 1, currencyIso: string = 'USD'): Cart => {
  const entries: OrderEntry[] = productEntries > 0 ? Array.from({ length: productEntries }, () => generateOneProductEntry()) : [];
  const totalItems: number = entries.length;
  const totalValue: number = entries.reduce((acc, entry: OrderEntry) => acc + entry.quantity * entry.totalPrice.value, 0);
  const quantity: number = productEntries || faker.number.int({
    min: 1,
    max: 5
  });
  return {
    code: faker.string.uuid(),
    entries,
    totalItems,
    totalPrice: generateTotalPrice(currencyIso, totalValue),
  };
};