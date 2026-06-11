import { Provider } from '@angular/core';
import { worldpayConnectorsProviders, worldpayNormalizersProviders, worldpayFacadesProviders } from '../core';
import { worldpayAppBaseRefProvider } from './worldpay-app-base-ref.provider';
import { worldpaySymbolsProvider } from './worldpay-symbols.provider';
import { worldpayTranslationsProvider } from './worldpay-translations.provider';

export const worldpayCoreProviders: () => Provider[] = (): Provider[] => [
  ...worldpayFacadesProviders,
  ...worldpayConnectorsProviders(),
  ...worldpayNormalizersProviders(),
  worldpayTranslationsProvider(),
  worldpaySymbolsProvider(),
  worldpayAppBaseRefProvider()
];