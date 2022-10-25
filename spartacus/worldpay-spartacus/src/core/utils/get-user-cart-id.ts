import { ActiveCartService, getCartIdByUserId, UserIdService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { first, map, take, withLatestFrom } from 'rxjs/operators';

export const getUserIdCartId = (
  userIdService: UserIdService,
  activeCartService: ActiveCartService,
): Observable<{ userId: string; cartId: string }> => activeCartService.getActive().pipe(
  first(cart => cart != null && typeof cart === 'object' && Object.keys(cart).length > 0),
  withLatestFrom(userIdService.getUserId()),
  map(([cart, userId]) => ({userId, cartId: getCartIdByUserId(cart, userId)})),
  take(1)
);
