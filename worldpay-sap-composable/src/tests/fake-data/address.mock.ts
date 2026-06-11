import { faker } from '@faker-js/faker';
import { Address, Country, Region } from '@spartacus/core';
import { generateFirstName, generateLastName } from './user.mock';

export interface MockFormattedAddress {
  line1: string;
  line2: string;
  town: string;
  country: Country;
  postalCode: string;
  region: Region;
}

export const generateLine1 = (): string => faker.location.streetAddress();

export const generateLine2 = (): string => faker.location.secondaryAddress();

export const generateTown = (): string => faker.location.city();

export const generateOneCountry = (): Country => ({
  isocode: faker.location.countryCode(),
  name: faker.location.country()
});

export const generateManyCountries = (size: number): Country[] => {
  const countries: Country[] = [];
  for (let i: number = 0; i < size; i++) {
    countries.push(generateOneCountry());
  }
  return countries;
};

export const generateOneRegion = (): Region => {
  const isocode = faker.location.state({ abbreviated: true });
  return {
    isocode,
    name: faker.location.state(),
    isocodeShort: isocode
  };
};

export const generateManyRegions = (size: number): Region[] => {
  const regions: Region[] = [];
  for (let i: number = 0; i < size; i++) {
    regions.push(generateOneRegion());
  }
  return regions;
};

export const generatePostalCode = (): string => faker.location.zipCode();

export const generateFormattedAddress = (): MockFormattedAddress => {
  const line1: string = generateLine1();
  const line2: string = generateLine2();
  const town: string = generateTown();
  const country: Country = generateOneCountry();
  const postalCode: string = generatePostalCode();
  const region: Region = generateOneRegion();
  return {
    line1,
    line2,
    town,
    country,
    postalCode,
    region,
  };
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const generateFormattedAddressAsString = (formattedAddress: any): string => {
  const {
    line1,
    line2,
    town,
    country,
    postalCode
  } = formattedAddress;
  return `${line1} ${line2}, ${town}, ${country.name}, ${postalCode}`;
};

export const generateOneAddress = (): Address => {
  const address: MockFormattedAddress = generateFormattedAddress();

  return {
    id: faker.string.uuid(),
    title: faker.person.prefix(),
    titleCode: faker.person.prefix(),
    email: faker.internet.email(),
    firstName: generateFirstName(),
    lastName: generateLastName(),
    companyName: faker.company.name(),
    line1: address.line1,
    line2: address.line2,
    postalCode: address.postalCode,
    town: address.town,
    country: address.country,
    region: address.region,
    cellphone: faker.phone.number(),
    defaultAddress: false,
    shippingAddress: true,
    formattedAddress: generateFormattedAddressAsString(address),
    visibleInAddressBook: true,
  };
};

export const generateAddressFromAddress = (address: Address): Address => {
  return {
    firstName: address.firstName,
    lastName: address.lastName,
    line1: address.line1,
    line2: address.line2,
    postalCode: address.postalCode,
    town: address.town,
    country: address.country,
    region: address.region,
    cellphone: address.cellphone,
  };
};

export const generateBillingFromAddress = (address: Address): Address => {
  const region = address.region ? { isocodeShort: address.region.isocodeShort } : undefined;
  const country = address.country ? { isocode: address.country.isocode } : undefined;
  const fields = [
    'firstName',
    'lastName',
    'line1',
    'line2',
    'town',
    'region',
    'country',
    'postalCode'
  ];

  return fields.reduce((acc: Partial<Address>, key: string): Partial<Address> => {
    if (key === 'region' && region) {
      acc[key] = region; // Assign modified region object
    } else if (key === 'country' && region) {
      acc[key] = country;
    } else if (address[key as keyof Address] !== undefined) {
      // @ts-ignore
      acc[key] = address[key as keyof Address]; // Copy other values
    }
    return acc;
  }, {} as Partial<Address>);
};


