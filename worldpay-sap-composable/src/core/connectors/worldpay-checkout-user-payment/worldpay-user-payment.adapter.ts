import { UserPaymentAdapter } from '@spartacus/core';
import { Observable } from 'rxjs';
import { ApmPaymentDetails } from '../../interfaces';

export abstract class WorldpayUserPaymentAdapter extends UserPaymentAdapter {
  abstract loadAllForCart(userId: string, cartId: string): Observable<ApmPaymentDetails[]>;
}
