import { faker } from '@faker-js/faker';
import { OrderEntry } from '@spartacus/cart/base/root';
import { Price, Product } from '@spartacus/core';
import { generateOneProduct } from './product.mock';

export const generateBasePrice = (product: Product): Price => ({
  value: product.price.value,
  currencyIso: product.price.currencyIso,
  formattedValue: product.price.formattedValue
});

export const generateTotalPrice = (product: Product, quantity: number) => {
  const totalValue = product.price.value * quantity;
  return {
    value: totalValue,
    currencyIso: product.price.currencyIso,
    formattedValue: `$${totalValue}`
  };
};

export const generateOneProductEntry = (productQuantity?: number, productEntryNumber?: number): OrderEntry => {
  const quantity: number = productQuantity || faker.number.int({
    min: 1,
    max: 5
  });
  const product: Product = generateOneProduct();
  const entryNumber: number = productEntryNumber || 1;
  const basePrice: Price = generateBasePrice(product);
  const totalPrice: Price = generateTotalPrice(product, quantity);
  return {
    basePrice,
    product,
    quantity,
    entryNumber,
    totalPrice,
  };
};

export const generateMayProductEntries = (size: number): OrderEntry[] => {
  const entries: OrderEntry[] = [];
  for (let index = 0; index < size; index++) {
    entries.push(generateOneProductEntry());
  }
  return [...entries];
};