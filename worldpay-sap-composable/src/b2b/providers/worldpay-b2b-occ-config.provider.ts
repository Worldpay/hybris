import { Provider } from '@angular/core';
import { OccConfig, provideConfig } from '@spartacus/core';
import { worldpayB2BOccConfig } from '../core';

export const worldpayB2BOccConfigProvider: () => Provider = (): Provider => (
  provideConfig(worldpayB2BOccConfig as OccConfig)
);