import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
  MockCxAddToHomeScreenBannerComponent,
  MockCxCardComponent,
  MockCxConsignmentTrackingComponent,
  MockCxGuestRegisterFormComponent,
  MockCxIconComponent,
  MockCxMediaComponent,
  MockCxSpinnerComponent,
  MockFormRequiredAsterisksComponent,
  MockFormRequiredLegendComponent,
  MockWorldpayAPMACHComponent,
  MockWorldpayApmSubmitButtonsComponent,
  MockWorldpayBillingAddressComponent
} from './components';
import { MockWorldpayApmComponent } from './components/worldpay-apm-component.mock';
import { MockWorldpayApmSepaComponent, MockWorldpayB2BApmSepaComponent } from './components/worldpay-apm-sepa.component.mock';
import { MockAtMessageDirective, MockCxFeatureDirective, MockCxFeatureLevelDirective, MockCxOutletContextDirective, MockCxOutletDirective } from './directives';
import { MockUrlPipe } from './pipes';

@NgModule({
  declarations: [
    // Components
    MockCxAddToHomeScreenBannerComponent,
    MockCxCardComponent,
    MockCxConsignmentTrackingComponent,
    MockCxGuestRegisterFormComponent,
    MockCxIconComponent,
    MockCxMediaComponent,
    MockCxSpinnerComponent,
    MockFormRequiredAsterisksComponent,
    MockFormRequiredLegendComponent,
    MockWorldpayAPMACHComponent,
    MockWorldpayApmSubmitButtonsComponent,
    MockWorldpayBillingAddressComponent,
    MockWorldpayApmComponent,
    MockWorldpayApmSepaComponent,
    MockWorldpayB2BApmSepaComponent,
    // Directives
    MockAtMessageDirective,
    MockCxOutletDirective,
    MockCxOutletContextDirective,
    MockCxFeatureLevelDirective,
    MockCxFeatureDirective,
    // Pipes
    MockUrlPipe
  ],
  exports: [
    MockCxOutletContextDirective,
    MockWorldpayB2BApmSepaComponent
  ],
  imports: [
    CommonModule
  ]
})
export class WorldpayMockModule {
}
