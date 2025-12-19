import { faker } from '@faker-js/faker';
import { Product } from '@spartacus/core';
import { generateManyCategories } from './categories.mock';

export const generateOneProduct = (productQuantity?: number, currencyIso: string = 'USD'): Product => {
  const priceValue: number = parseFloat(faker.commerce.price());
  return {
    averageRating: faker.number.int({
      min: 1,
      max: 5
    }),
    code: faker.commerce.productName(),
    name: faker.commerce.productName(),
    summary: faker.commerce.productDescription(),
    price: {
      formattedValue: `$${faker.commerce.price()}`,
      value: priceValue,
      currencyIso
    },
    categories: generateManyCategories(2)
  };
};

export const generateManyProducts = (size: number): Product[] => {
  const products: Product[] = [];
  for (let index = 0; index < size; index++) {
    products.push(generateOneProduct());
  }
  return [...products];
};