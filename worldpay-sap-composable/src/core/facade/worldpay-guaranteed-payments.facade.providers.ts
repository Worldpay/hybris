import { Provider } from '@angular/core';
import { WorldpayGuaranteedPaymentsService } from '../services';
import { WorldpayGuaranteedPaymentsFacade } from './worldpay-guaranteed-payments.facade';

export const worldpayGuaranteedPaymentsFacadeProviders: Provider[] = [
  WorldpayGuaranteedPaymentsService,
  {
    provide: WorldpayGuaranteedPaymentsFacade,
    useExisting: WorldpayGuaranteedPaymentsService,
  },
];
