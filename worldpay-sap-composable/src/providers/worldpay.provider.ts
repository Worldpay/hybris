import { Provider } from '@angular/core';
import { provideWorldpayConnectors, provideWorldpayNormalizers, worldpayFacadesProviders } from '../core';
import { worldpayAppBaseRefProvider } from './worldpay-app-base-ref.provider';
import { worldpaySymbolsProvider } from './worldpay-symbols.provider';
import { worldpayTranslationsProvider } from './worldpay-translations.provider';

export const worldpayProviders: () => Provider[] = (): Provider[] => [
  ...worldpayFacadesProviders,
  ...provideWorldpayConnectors(),
  ...provideWorldpayNormalizers(),
  worldpayTranslationsProvider(),
  worldpaySymbolsProvider(),
  worldpayAppBaseRefProvider()
];