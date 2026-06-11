import { Provider } from '@angular/core';
import { WorldpayGuaranteedPaymentsAdapter } from '../../../connectors';
import { OccWorldpayGuaranteedPaymentsAdapter } from './occ-worldpay-guaranteed-payments.adapter';

export const worldpayGuaranteedPaymentsAdapterProvider: Provider[] = [
  {
    provide: WorldpayGuaranteedPaymentsAdapter,
    useClass: OccWorldpayGuaranteedPaymentsAdapter,
  },
];

export const worldpayGuaranteedPaymentsAdapterProviders: () => Provider[] = (): Provider[] => worldpayGuaranteedPaymentsAdapterProvider;