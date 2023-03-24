import { Component, HostBinding, HostListener, OnInit } from '@angular/core';
import { WorldpayApmModel } from './worldpay-apm.model';
import { CmsComponentData } from '@spartacus/storefront';
import { WorldpayCheckoutPaymentService } from '../../../core/services/worldpay-checkout/worldpay-checkout-payment.service';
import { combineLatest } from 'rxjs';
import { filter } from 'rxjs/operators';
import { AbstractWorldpayApmComponent } from './abstract-worldpay-apm.component';
import { CmsService } from '@spartacus/core';

@Component({
  selector: 'worldpay-apm-component',
  templateUrl: './worldpay-apm-component.component.html',
  styleUrls: ['./worldpay-apm-component.component.scss']
})
export class WorldpayApmComponent extends AbstractWorldpayApmComponent
  implements OnInit {
  isSelected: boolean = false;
  isAvailable: boolean = false;

  code: string = '';

  constructor(
    public componentData: CmsComponentData<WorldpayApmModel>,
    protected checkoutPaymentService: WorldpayCheckoutPaymentService
  ) {
    super(checkoutPaymentService);
  }

  ngOnInit(): void {
    this.componentData.data$.subscribe(data =>
      console.log('received data from ', { data })
    );

    this.checkoutPaymentService
      .getWorldpayAvailableApmsFromState()
      .pipe(filter(availableApms => !!availableApms))
      .subscribe(availableApms => {
        this.isAvailable = availableApms.indexOf(this.code) !== -1;
      });

    this.checkoutPaymentService.checkApmAvailability('SOFORT-SSL');

    combineLatest([
      this.checkoutPaymentService.getSelectedAPMFromState(),
      this.componentData.data$
    ])
      .pipe(
        filter(
          ([_, data]) =>
            !!data &&
            Object.keys(data).length > 0 &&
            data.apmConfiguration &&
            data.apmConfiguration.hasOwnProperty('code')
        )
      )
      .subscribe(
        ([
          apm,
          {
            apmConfiguration: { code }
          }
        ]) => {
          this.isSelected = code === apm;
          this.code = code;
          this.checkoutPaymentService.checkApmAvailability(code);
        }
      );
  }

  @HostListener('click') onClick() {
    super.selectApm();
  }
}
