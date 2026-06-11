/*
 * SPDX-FileCopyrightText: 2026 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, } from '@angular/core';
import { B2BCheckoutDeliveryAddressComponent } from '@spartacus/checkout/b2b/components';
import { TranslatePipe, } from '@spartacus/core';
import { CardComponent, SpinnerComponent } from '@spartacus/storefront';
import { WorldpayAddressFormComponent } from '../../../storefrontlib';

@Component({
  selector: 'y-worldpay-b2b-address-form',
  templateUrl: './worldpay-b2b-checkout-delivery-address.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CardComponent,
    SpinnerComponent,
    AsyncPipe,
    TranslatePipe,
    WorldpayAddressFormComponent
  ],
})
export class WorldpayCheckoutB2BCheckoutDeliveryAddressComponent extends B2BCheckoutDeliveryAddressComponent implements OnInit, OnDestroy {
  // Extended to include Japan address validation
}
