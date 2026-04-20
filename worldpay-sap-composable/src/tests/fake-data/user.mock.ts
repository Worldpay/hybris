import { faker } from '@faker-js/faker';
import { User } from '@spartacus/core';

export const mockUserId = 'userId';

export const generateFirstName = (): string => faker.person.firstName();

export const generateLastName = (): string => faker.person.lastName();

export const generateUser = (): User => ({
  uid: faker.string.uuid(),
  firstName: generateFirstName(),
  lastName: generateLastName(),
  customerId: faker.string.uuid(),
  title: faker.person.prefix(),
  titleCode: faker.person.prefix(),
  displayUid: faker.internet.username(),
});