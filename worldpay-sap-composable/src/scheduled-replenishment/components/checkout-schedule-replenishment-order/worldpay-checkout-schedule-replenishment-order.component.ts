import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, ViewEncapsulation, } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CheckoutScheduleReplenishmentOrderComponent } from '@spartacus/checkout/scheduled-replenishment/components';
import { QueryState } from '@spartacus/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { WorldpayApmPaymentInfo, WorldpayCheckoutPaymentFacade } from '../../../core';

@Component({
  selector: 'y-worldpay-schedule-replenishment-order',
  templateUrl: './worldpay-checkout-schedule-replenishment-order.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
  encapsulation: ViewEncapsulation.None
})
export class WorldpayCheckoutScheduleReplenishmentOrderComponent extends CheckoutScheduleReplenishmentOrderComponent implements OnInit, OnDestroy {

  showReplenishmentForm$: Observable<boolean>;
  protected worldpayCheckoutPaymentFacade: WorldpayCheckoutPaymentFacade = inject(WorldpayCheckoutPaymentFacade);
  protected destroyRef: DestroyRef = inject(DestroyRef);

  override ngOnInit(): void {
    super.ngOnInit();

    this.showReplenishmentForm$ = this.worldpayCheckoutPaymentFacade.getPaymentDetailsState().pipe(
      takeUntilDestroyed(this.destroyRef),
      filter((paymentDetails: QueryState<WorldpayApmPaymentInfo>): boolean => !paymentDetails.loading),
      map((paymentDetailsQueryState: QueryState<WorldpayApmPaymentInfo>): boolean => {
        const paymentDetails: WorldpayApmPaymentInfo = paymentDetailsQueryState.data;
        return Boolean(paymentDetails?.subscriptionId);
      }));
  }
}
