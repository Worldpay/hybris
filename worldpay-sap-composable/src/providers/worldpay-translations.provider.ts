import { Provider } from '@angular/core';
import { I18nConfig, provideConfig } from '@spartacus/core';
import { worldpayTranslations } from '../i18n';

export const worldpayTranslationsProvider: () => Provider = (): Provider => (
  provideConfig({
    i18n: { resources: worldpayTranslations }
  } as I18nConfig)
);