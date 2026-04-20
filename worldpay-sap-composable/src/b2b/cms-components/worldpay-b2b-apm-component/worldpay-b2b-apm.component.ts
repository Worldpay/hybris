import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { WorldpayApmComponent } from '../../../storefrontlib';

@Component({
  selector: 'y-worldpay-b2b-apm-component',
  templateUrl: './worldpay-b2b-apm.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false
})
export class WorldpayB2bApmComponent extends WorldpayApmComponent implements OnInit, OnDestroy {

}
