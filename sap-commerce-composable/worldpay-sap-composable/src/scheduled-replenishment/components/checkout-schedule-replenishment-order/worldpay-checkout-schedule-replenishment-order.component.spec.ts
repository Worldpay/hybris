import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { CheckoutReplenishmentFormService } from '@spartacus/checkout/scheduled-replenishment/components';
import { I18nTestingModule } from '@spartacus/core';
import { DaysOfWeek, ORDER_TYPE, recurrencePeriod, ScheduleReplenishmentForm, } from '@spartacus/order/root';
import { IconTestingModule } from '@spartacus/storefront';
import { of } from 'rxjs';
import { MockWorldpayCheckoutPaymentService } from 'worldpay-sap-composable-tests';
import { ApmPaymentDetails, PaymentMethod, WorldpayCheckoutPaymentService } from '../../../core';
import { WorldpayCheckoutScheduleReplenishmentOrderComponent } from './worldpay-checkout-schedule-replenishment-order.component';
import createSpy = jasmine.createSpy;

const selectedPaymentDetails: ApmPaymentDetails = {
  id: 'payment1',
  code: PaymentMethod.ACH,
  isAPM: true,
};
const mockReplenishmentOrderFormData: ScheduleReplenishmentForm = {
  numberOfDays: '14',
  nthDayOfMonth: '1',
  recurrencePeriod: recurrencePeriod.WEEKLY,
  numberOfWeeks: '1',
  replenishmentStartDate: '2025-01-30',
  daysOfWeek: [],
};

class MockCheckoutReplenishmentFormService implements Partial<CheckoutReplenishmentFormService> {
  getOrderType = createSpy().and.returnValue(of(ORDER_TYPE.PLACE_ORDER));
  setOrderType = createSpy();
  getScheduleReplenishmentFormData = createSpy().and.returnValue(of({}));
  setScheduleReplenishmentFormData = createSpy();
}

describe('WorldpayCheckoutScheduleReplenishmentOrderComponent', () => {
  let component: WorldpayCheckoutScheduleReplenishmentOrderComponent;
  let fixture: ComponentFixture<WorldpayCheckoutScheduleReplenishmentOrderComponent>;
  let worldpayCheckoutPaymentService: WorldpayCheckoutPaymentService;
  let checkoutReplenishmentFormService: CheckoutReplenishmentFormService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [I18nTestingModule, IconTestingModule],
      declarations: [WorldpayCheckoutScheduleReplenishmentOrderComponent],
      providers: [
        {
          provide: CheckoutReplenishmentFormService,
          useClass: MockCheckoutReplenishmentFormService,
        },
        {
          provide: WorldpayCheckoutPaymentService,
          useFactory: (sanitizer: DomSanitizer) => new MockWorldpayCheckoutPaymentService(sanitizer),
          deps: [DomSanitizer]
        }
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(
      WorldpayCheckoutScheduleReplenishmentOrderComponent
    );
    component = fixture.componentInstance;

    checkoutReplenishmentFormService = TestBed.inject(CheckoutReplenishmentFormService);
    worldpayCheckoutPaymentService = TestBed.inject(WorldpayCheckoutPaymentService);
    component.scheduleReplenishmentFormData = mockReplenishmentOrderFormData;
  });

  it('should get selected order type', (done) => {
    component.selectedOrderType$.subscribe((result) => {
      expect(result).toEqual(ORDER_TYPE.PLACE_ORDER);
      done();
    });
  });

  it('should change order type', () => {
    component.changeOrderType(ORDER_TYPE.SCHEDULE_REPLENISHMENT_ORDER);

    expect(checkoutReplenishmentFormService.setOrderType).toHaveBeenCalledWith(
      ORDER_TYPE.SCHEDULE_REPLENISHMENT_ORDER
    );
  });

  it('should change number of days of replenishment form data', () => {
    const mockNumberOfDays = '20';

    component.changeNumberOfDays(mockNumberOfDays);

    expect(
      checkoutReplenishmentFormService.setScheduleReplenishmentFormData
    ).toHaveBeenCalledWith({
      ...mockReplenishmentOrderFormData,
      numberOfDays: mockNumberOfDays,
    });
  });

  it('should change number of weeks of replenishment form data', () => {
    const mockNumberOfWeeks = '5';

    component.changeNumberOfWeeks(mockNumberOfWeeks);

    expect(
      checkoutReplenishmentFormService.setScheduleReplenishmentFormData
    ).toHaveBeenCalledWith({
      ...mockReplenishmentOrderFormData,
      numberOfWeeks: mockNumberOfWeeks,
    });
  });

  it('should change recurrence period type of replenishment form data', () => {
    const mockPeriodType = recurrencePeriod.MONTHLY;

    component.changeRecurrencePeriodType(mockPeriodType);

    expect(
      checkoutReplenishmentFormService.setScheduleReplenishmentFormData
    ).toHaveBeenCalledWith({
      ...mockReplenishmentOrderFormData,
      recurrencePeriod: mockPeriodType,
    });
  });

  it('should change day of month of replenishment form data', () => {
    const mockDayOfMonth = '31';

    component.changeDayOfTheMonth(mockDayOfMonth);

    expect(
      checkoutReplenishmentFormService.setScheduleReplenishmentFormData
    ).toHaveBeenCalledWith({
      ...mockReplenishmentOrderFormData,
      nthDayOfMonth: mockDayOfMonth,
    });
  });

  it('should change replenishment start date of replenishment form data', () => {
    const mockStartDate = '2021-10-31';

    component.changeReplenishmentStartDate(mockStartDate);

    expect(
      checkoutReplenishmentFormService.setScheduleReplenishmentFormData
    ).toHaveBeenCalledWith({
      ...mockReplenishmentOrderFormData,
      replenishmentStartDate: mockStartDate,
    });
  });

  it('should change repeat days when reoccurence is weekly of replenishment form data', () => {
    const mockRepeatDays = DaysOfWeek.MONDAY;

    component.changeRepeatDays(mockRepeatDays, true);

    expect(
      checkoutReplenishmentFormService.setScheduleReplenishmentFormData
    ).toHaveBeenCalledWith({
      ...mockReplenishmentOrderFormData,
      daysOfWeek: [mockRepeatDays],
    });
  });

  it('should return TRUE if the day exist in the currentDaysOfWeek array', () => {
    component.currentDaysOfWeek = [DaysOfWeek.FRIDAY];

    const result = component.hasDaysOfWeekChecked(DaysOfWeek.FRIDAY);

    expect(result).toBeTruthy();
  });

  it('should return FALSE if the day does NOT exist in the currentDaysOfWeek array', () => {
    component.currentDaysOfWeek = [DaysOfWeek.FRIDAY];

    const result = component.hasDaysOfWeekChecked(DaysOfWeek.MONDAY);

    expect(result).toBeFalsy();
  });

  describe('UI components', () => {
    it('should not render the component if subscriptionId is not present', () => {
      spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(of({
        loading: false,
        error: false,
        data: selectedPaymentDetails
      }));
      component.ngOnInit();
      fixture.detectChanges();

      const compiled = fixture.debugElement.nativeElement as HTMLElement;
      expect(compiled.querySelector('.cx-order-type-card')).toBeFalsy();
    });

    describe('it should render the component', () => {
      const getPlaceOrderInput = () => fixture.debugElement.nativeElement.querySelector(`#orderType-${ORDER_TYPE.PLACE_ORDER}`) as HTMLInputElement;
      const getReplenishmentOrderInput = () => fixture.debugElement.nativeElement.querySelector(`#orderType-${ORDER_TYPE.SCHEDULE_REPLENISHMENT_ORDER}`) as HTMLInputElement;
      beforeEach(() => {
        spyOn(worldpayCheckoutPaymentService, 'getPaymentDetailsState').and.returnValue(of({
          loading: false,
          error: false,
          data: {
            ...selectedPaymentDetails,
            subscriptionId: 'sub-12345',
          }
        }));
      });

      it('should not render replenishment form when order type is PLACE ORDER', () => {
        component.ngOnInit();
        fixture.detectChanges();
        const placeOrderOption = getPlaceOrderInput();
        const replenishmentOrderInput = getReplenishmentOrderInput();
        const replenishmentForm = fixture.debugElement.nativeElement.querySelector('.cx-replenishment-form-data-container');
        expect(placeOrderOption.checked).toBeTrue();
        expect(replenishmentOrderInput.checked).toBeFalse();
        expect(replenishmentForm).toBeFalsy();
      });

      it('should render replenishment form when order type is SCHEDULE_REPLENISHMENT_ORDER', () => {
        component.ngOnInit();
        component.selectedOrderType$ = of(ORDER_TYPE.SCHEDULE_REPLENISHMENT_ORDER);
        fixture.detectChanges();
        expect(fixture.debugElement.nativeElement.querySelector('.cx-replenishment-form-data-container')).toBeTruthy();
      });

      it('should show number of weeks input when recurrence period is weekly', () => {
        component.ngOnInit();
        component.selectedOrderType$ = of(ORDER_TYPE.SCHEDULE_REPLENISHMENT_ORDER);
        component.isMonthly = false;
        fixture.detectChanges();

        expect(fixture.debugElement.nativeElement.querySelector('#order-replenishment-period-type')).toBeTruthy();
      });
    });
  });
});
