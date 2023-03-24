import { Injectable } from '@angular/core';
import { GetAvailableApms, PlaceOrderRedirect, SetSelectedAPM } from '../../store/worldpay.action';
import { ActiveCartService, CmsService, ConverterService, UserIdService } from '@spartacus/core';
import { first, map, take } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { COMPONENT_APM_NORMALIZER } from '../../occ/converters';
import { select, Store } from '@ngrx/store';
import { StateWithWorldpay } from '../../store/worldpay.state';
import { getWorldpayAPMRedirect, getWorldpayAvailableApms, getWorldpaySelectedAPM } from '../../store/worldpay.selectors';
import { ApmData, APMRedirectResponse, OccCmsComponentWithMedia, PaymentMethod } from '../../interfaces';
import { CheckoutService } from '@spartacus/checkout/core';

@Injectable({
  providedIn: 'root'
})
export class WorldpayApmService {

  constructor(
    protected cmsService: CmsService,
    protected convertService: ConverterService,
    protected worldpayStore: Store<StateWithWorldpay>,
    protected userIdService: UserIdService,
    protected activeCartService: ActiveCartService,
    protected checkoutService: CheckoutService,
  ) {
  }

  getApmComponentById(componentUid: string, code: PaymentMethod): Observable<ApmData> {
    return this.cmsService.getComponentData<OccCmsComponentWithMedia>(componentUid)
      .pipe(this.convertService.pipeable(COMPONENT_APM_NORMALIZER),
        map((apmData: ApmData) => ({
          ...apmData,
          code
        }))
      );
  }

  selectAPM(apm: ApmData): void {
    this.worldpayStore.dispatch(new SetSelectedAPM(apm));
  }

  getSelectedAPMFromState(): Observable<ApmData> {
    return this.worldpayStore.pipe(select(getWorldpaySelectedAPM));
  }

  getWorldpayAPMRedirectUrl(): Observable<APMRedirectResponse> {
    return this.worldpayStore.pipe(select(getWorldpayAPMRedirect));
  }

  getWorldpayAvailableApmsFromState(): Observable<ApmData[]> {
    return this.worldpayStore.pipe(select(getWorldpayAvailableApms));
  }

  requestAvailableApms(): void {
    const cartId = this.getCartId();
    const userId = this.getUserId();

    this.worldpayStore.dispatch(
      new GetAvailableApms({
        userId,
        cartId
      })
    );
  }

  placeRedirectOrder(cartId: string, userId: string): Observable<boolean> {
    this.worldpayStore.dispatch(
      new PlaceOrderRedirect({
        userId,
        cartId
      })
    );

    return this.checkoutService.getOrderDetails().pipe(
      first(result => result != null),
      map(orderDetails => !!orderDetails)
    );
  }

  private getCartId(): string {
    let cartId: string;
    this.activeCartService
      .getActiveCartId()
      .pipe(take(1))
      .subscribe((res: string) => (cartId = res))
      .unsubscribe();
    return cartId;
  }

  private getUserId(): string {
    let userId: string;
    this.userIdService
      .getUserId()
      .pipe(take(1))
      .subscribe((res: string) => (userId = res))
      .unsubscribe();
    return userId;
  }
}
