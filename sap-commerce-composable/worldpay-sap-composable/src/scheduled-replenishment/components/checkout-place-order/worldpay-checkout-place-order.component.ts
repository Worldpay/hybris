import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CheckoutReplenishmentFormService } from '@spartacus/checkout/scheduled-replenishment/components';
import { GlobalMessageType, HttpErrorModel } from '@spartacus/core';
import { ORDER_TYPE, recurrencePeriod, ScheduledReplenishmentOrderFacade, ScheduleReplenishmentForm, } from '@spartacus/order/root';
import { BehaviorSubject } from 'rxjs';
import { WorldpayCheckoutPlaceOrderComponent } from '../../../storefrontlib';

@Component({
  selector: 'y-worldpay-checkout-place-order',
  templateUrl: './worldpay-checkout-place-order.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class WorldpayCheckoutScheduledReplenishmentPlaceOrderComponent extends WorldpayCheckoutPlaceOrderComponent implements OnInit, OnDestroy {
  currentOrderType: ORDER_TYPE;
  daysOfWeekNotChecked$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  protected checkoutReplenishmentFormService: CheckoutReplenishmentFormService = inject(CheckoutReplenishmentFormService);
  protected scheduledReplenishmentOrderFacade: ScheduledReplenishmentOrderFacade = inject(ScheduledReplenishmentOrderFacade);

  /**
   * Initializes the component by invoking the parent class's `ngOnInit` method
   * and fetching the order type and schedule replenishment form data.
   *
   * @override
   * @since 2211.43.0
   */
  override ngOnInit(): void {
    this.getOrderType();
    this.getScheduleReplenishmentFormData();
    super.ngOnInit();
  }

  override submitForm(): void {
    if (this.currentOrderType === ORDER_TYPE.PLACE_ORDER) {
      this.scheduleReplenishmentFormData = undefined;
      super.submitForm();
    } else {
      this.submitReplenishmentForm();
    }
  }

  /**
   * Retrieves the schedule replenishment form data from the `CheckoutReplenishmentFormService`.
   * Subscribes to the observable and assigns the retrieved data to the `scheduleReplenishmentFormData` property.
   * Updates the `daysOfWeekNotChecked$` BehaviorSubject based on whether the `daysOfWeek` array is empty
   * and the `recurrencePeriod` is set to weekly.
   *
   * @protected
   * @since 2211.43.0
   */
  override onSuccess(): void {
    switch (this.currentOrderType) {
      case ORDER_TYPE.PLACE_ORDER: {
        super.onSuccess();
        break;
      }

      case ORDER_TYPE.SCHEDULE_REPLENISHMENT_ORDER: {
        this.routingService.go({ cxRoute: 'replenishmentConfirmation' });
        break;
      }
    }
    this.checkoutReplenishmentFormService.resetScheduleReplenishmentFormData();
  }

  /**
   * Retrieves the current order type from the `CheckoutReplenishmentFormService`.
   * Subscribes to the observable and assigns the retrieved order type to the `currentOrderType` property.
   * Logs the current order type to the console for debugging purposes.
   *
   * @protected
   * @since 2211.43.0
   */
  protected getOrderType(): void {
    this.checkoutReplenishmentFormService.getOrderType().pipe(
      takeUntilDestroyed(this.destroyRef),
    ).subscribe({
      next: (orderType: ORDER_TYPE): void => {
        this.currentOrderType = orderType;
      }
    });
  }

  /**
   * Retrieves the schedule replenishment form data from the `CheckoutReplenishmentFormService`.
   * Subscribes to the observable and assigns the retrieved data to the `scheduleReplenishmentFormData` property.
   * Updates the `daysOfWeekNotChecked$` BehaviorSubject based on whether the `daysOfWeek` array is empty
   * and the `recurrencePeriod` is set to weekly.
   *
   * @protected
   * @since 2211.43.0
   */
  protected getScheduleReplenishmentFormData(): void {
    this.checkoutReplenishmentFormService.getScheduleReplenishmentFormData().pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (data: ScheduleReplenishmentForm): void => {
        this.scheduleReplenishmentFormData = data;
        this.daysOfWeekNotChecked$.next(
          data.daysOfWeek?.length === 0 &&
          data.recurrencePeriod === recurrencePeriod.WEEKLY
        );
      }
    });
  }

  protected submitReplenishmentForm(): void {
    if (this.checkoutSubmitForm.valid) {
      this.orderFacade.startLoading(this.vcr);
      this.globalMessageService.add({ key: 'checkoutReview.placingOrder' }, GlobalMessageType.MSG_TYPE_INFO);
      this.placeReplenishmentOrder();
    } else {
      this.checkoutSubmitForm.markAllAsTouched();
    }
  }

  protected placeReplenishmentOrder(): void {
    this.scheduledReplenishmentOrderFacade.scheduleReplenishmentOrder(
      this.scheduleReplenishmentFormData,
      this.checkoutSubmitForm.valid
    ).pipe(
      takeUntilDestroyed(this.destroyRef),
    ).subscribe({
      next: (): void => {
        this.onSuccess();
      },
      error: (error: unknown): void => {
        this.orderFacade.clearLoading();
        this.worldpayApmService.showErrorMessage(error as HttpErrorModel);
      }
    });
  }
}
