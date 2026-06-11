import { Provider } from '@angular/core';
import { worldpayGuaranteedPaymentsConnectorProvider, worldpayGuaranteedPaymentsFacadeProviders } from '../core';

export const worldpayGuaranteedPaymentsCoreProviders: () => Provider[] = (): Provider[] => [
  ...worldpayGuaranteedPaymentsConnectorProvider,
  ...worldpayGuaranteedPaymentsFacadeProviders
];