import { faker } from '@faker-js/faker';
import { Category } from '@spartacus/core';

export const generateOneCategory = (): Category => ({
  code: faker.commerce.department(),
  name: faker.commerce.department(),
  url: faker.internet.url(),
  image: {
    url: faker.image.url(),
  }
});

export const generateManyCategories = (size: number): Category[] => {
  const users: Category[] = [];
  for (let index = 0; index < size; index++) {
    users.push(generateOneCategory());
  }
  return [...users];
};