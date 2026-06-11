import { CdkAccordion, CdkAccordionItem } from '@angular/cdk/accordion';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { SpinnerModule } from '@spartacus/storefront';
import {
  WorldpayApmAchComponent,
  WorldpayApmComponent,
  WorldpayApmGooglepayComponent,
  WorldpayApmIdealComponent,
  WorldpayApmSubmitButtonsComponent,
  WorldpayApmTileComponent,
  WorldpayApplepayComponent,
  WorldpayBillingAddressComponent
} from '../../../storefrontlib';
import { WorldpayB2BApmSepaComponent } from './worldpay-b2b-apm-sepa';

@Component({
  selector: 'y-worldpay-b2b-apm-component',
  templateUrl: './worldpay-b2b-apm.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    AsyncPipe,
    SpinnerModule,
    CdkAccordion,
    CdkAccordionItem,
    NgTemplateOutlet,
    WorldpayApmTileComponent,
    WorldpayApmIdealComponent,
    WorldpayB2BApmSepaComponent,
    WorldpayApmGooglepayComponent,
    WorldpayApplepayComponent,
    WorldpayApmAchComponent,
    WorldpayBillingAddressComponent,
    WorldpayApmSubmitButtonsComponent,
  ]
})
export class WorldpayB2bApmComponent extends WorldpayApmComponent implements OnInit, OnDestroy {

}
