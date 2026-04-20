import { Provider } from '@angular/core';
import { provideConfig } from '@spartacus/core';
import { IconConfig, IconResourceType } from '@spartacus/storefront';
import { getWorldpayIconSymbols, WORLDPAY_ICONS } from '../core';

export const worldpaySymbolsProvider: () => Provider = (): Provider => (
  provideConfig({
    icon: {
      symbols: getWorldpayIconSymbols(),
      resources: [
        {
          type: IconResourceType.SVG,
          url: 'assets/worldpay/worldpay-icons.svg',
          types: Object.values(WORLDPAY_ICONS),
        },
      ]
    }
  } as IconConfig)
);